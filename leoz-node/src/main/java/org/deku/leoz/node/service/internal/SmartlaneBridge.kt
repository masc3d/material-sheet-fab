package org.deku.leoz.node.service.internal

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.internal.schedulers.SchedulerWhen
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.identity.Identity
import org.deku.leoz.model.TourRouteMeta
import org.deku.leoz.model.TourStopRouteMeta
import org.deku.leoz.service.internal.*
import org.deku.leoz.service.internal.TourServiceV1.*
import org.deku.leoz.smartlane.SmartlaneApi
import org.deku.leoz.smartlane.api.*
import org.deku.leoz.smartlane.model.*
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import org.threeten.bp.temporal.ChronoUnit
import sx.LazyInstance
import sx.io.serialization.Serializable
import sx.log.slf4j.info
import sx.log.slf4j.trace
import sx.rs.client.RestEasyClient
import sx.text.toHexString
import sx.time.replaceDate
import sx.time.toDate
import sx.time.toLocalDateTime
import java.net.URI
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import kotlin.NoSuchElementException
import kotlin.concurrent.withLock

/**
 * Bridge operations between leoz and smartlane
 * Created by masc on 15.11.17.
 */
@Named
class SmartlaneBridge {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Smartlane base URI */
    private val baseUri = URI.create("https://dispatch.smartlane.io/")

    /** Default customer id */
    private val customerId = "der-kurier-test"

    @Inject
    private lateinit var identity: Identity

    /**
     * Rest client with connection pool for efficient use across all smartlane domains
     */
    private val restClient by lazy {
        RestEasyClient(
                baseUri = baseUri,
                connectionPoolSize = 20,
                objectMapper = SmartlaneApi.mapper
        )
    }

    /**
     * Smartlane customer domain.
     * Each domain has it's own scheduler and authorization token.
     * @param customerId Smartlane customer id / domain path
     */
    inner class Domain(
            val customerId: String
    ) {
        private val log = LoggerFactory.getLogger(this.javaClass)

        /**
         * RX scheduler specific to this domain/customer, for fine-grained customer based concurrency control
         */
        val scheduler by lazy {
            SchedulerWhen({ workers ->
                Completable.merge(Flowable.merge(workers, 2))
            }, Schedulers.io())
        }

        private val authorizationLock = ReentrantLock()

        private val jwtTokenInstance = LazyInstance({
            Observable.fromCallable {
                // Authorize smartlane customer
                log.trace { "Authorizing [${customerId}]" }

                this@SmartlaneBridge.restClient
                        .proxy(AuthApi::class.java,
                                path = customerId)
                        .auth(AuthApi.Request(
                                email = "juergen.toepper@derkurier.de",
                                password = "PanicLane"
                        ))
                        .accessToken
            }
                    .subscribeOn(Schedulers.io())
                    .blockingFirst()
        })

        val jwtToken
            get() = authorizationLock.withLock { this.jwtTokenInstance.get() }

        fun resetToken() {
            this.jwtTokenInstance.reset()
        }
    }

    /** Map of customer specific smartlane domains */
    private val domains = ConcurrentHashMap<String, Domain>()

    private fun domain(customerId: String): Domain {
        return this.domains.getOrPut(customerId, {
            Domain(this.customerId)
        })
    }

    /**
     * Smartlane custom id
     */
    @Serializable
    private data class CustomId(
            /** Domain tour id */
            val id: Long,
            /** Domain tour uid. Required for uniquely mapping tours in shared container */
            val uid: String
    ) {
        /** Serialize to smartlane */
        fun serialize(): String =
                "${id}/${uid}"

        companion object {
            /** Deserialize from smartlane */
            fun deserialize(value: String) {
                value.split("/")
                        .map { it.trim() }
                        .let {
                            CustomId(
                                    id = it.get(0).toLong(),
                                    uid = it.get(1)
                            )
                        }
            }
        }
    }

    /**
     * Get rest client proxy for specific customer.
     * This method will authenticate synchronously (via `Domain`) if necessary.
     *
     * @param serviceClass Service class
     * @param customerId Smartlane customer id
     */
    private fun <T> proxy(serviceClass: Class<T>, customerId: String): T {
        return this.restClient.proxy(
                serviceClass,
                path = customerId,
                jwtToken = { this.domain(customerId).jwtToken }
        )
    }

    /**
     * Retries an operation which consumes smartlane REST apis in case of token expiry.
     * The REST proxy creation must be part of the observable (not cached) for this extension to work.
     */
    private fun <T> Observable<T>.retryOnTokenExpiry(domain: Domain): Observable<T> {
        // Retry once with token reset when authorization error occurs
        return this.retry(1, { e ->
            if (e is WebApplicationException &&
                    e.response.status == Response.Status.UNAUTHORIZED.statusCode) {

                log.info { "Resetting expired authorization token" }
                domain.resetToken()

                true
            } else {
                false
            }
        })
    }

    /**
     * Updates or inserts a smartlane driver
     * @param User User to put as driver
     */
    fun putDriver(
            user: UserService.User
    ): Completable {
        val domain = domain(customerId)

        return Observable.fromCallable {
            val driverApi = this.proxy(DriverApi::class.java, customerId = customerId)

            val srcDriver = user.toDriver()
            // Use srcdriver email with sugared identity (instead of email on domain user level)
            val dstDriver = driverApi.getDriverByEmail(srcDriver.email)

            if (dstDriver != null) {
                driverApi.patchDriverById(
                        srcDriver,
                        dstDriver.id
                )
            } else {
                driverApi.postDriver(
                        srcDriver
                )
            }
        }
                .retryOnTokenExpiry(domain)
                .ignoreElements()
    }

    /** Driver id by email. Also caches misses (-> null) */
    private val driverIdByEmail = ConcurrentHashMap<String, Int?>()

    /**
     * Get driver id with caching support
     * @param email User / driver email
     */
    private fun getDriverId(email: String): Int? {
        val domain = domain(customerId)

        val driverApi = this.proxy(DriverApi::class.java, customerId = customerId)

        return this.driverIdByEmail.getOrPut(
                this.formatEmail(email),
                // Default value -> determine driver (id)
                {
                    Observable.fromCallable {

                        driverApi.getDriverByEmail(
                                email = this.formatEmail(email)
                        )
                                ?.id
                    }
                            .retryOnTokenExpiry(domain)
                            .subscribeOn(domain.scheduler)
                            .blockingFirst()
                }
        )
    }

    /**
     * Indicates if a driver is known @smartlane
     */
    fun hasDriver(email: String): Boolean =
            this.getDriverId(email) != null

    /**
     * Update a drivers geo position
     * @param email User / driver email
     * @param positions Driver's geo positions
     */
    fun putDriverPosition(
            email: String,
            positions: Iterable<LocationServiceV2.GpsDataPoint>
    ): Completable {
        val domain = domain(customerId)

        val driverId = this.getDriverId(email)
                ?: throw NoSuchElementException("Driver not found")

        return Observable
                .fromIterable(positions)
                // Take it slow with position batch updates, to avoid smartlane request limit
                .window(1, TimeUnit.SECONDS, 10)
                .flatMap { positionWindow ->
                    positionWindow.flatMap { position ->
                        Observable.fromCallable {
                            val driverApi = this.proxy(DriverApi::class.java, customerId = customerId)

                            driverApi.postDrivertracking(Drivertracking().also {
                                it.driverId = driverId
                                it.position = Location().also {
                                    it.type = "Point"
                                    it.coordinates = listOf(
                                            position.latitude?.toBigDecimal(),
                                            position.longitude?.toBigDecimal()
                                    )
                                }
                                it.timestamp = position.time

                                log.trace { "Put driver tracking ${it}" }
                            })
                        }
                                .retryOnTokenExpiry(domain)
                    }
                }
                .ignoreElements()
                .subscribeOn(domain.scheduler)
    }

    /**
     * Delete smartlane routes
     */
    fun deleteRoutes(uids: List<String>): Completable {
        val domain = domain(customerId)

        val routeApi = this.proxy(RouteExtendedApi::class.java, customerId = customerId)
        val deliveryApi = this.proxy(DeliveryExtendedApi::class.java, customerId = customerId)

        return routeApi.getRouteByCustomId(*uids.toTypedArray())
                .toList()
                .toObservable()
                .doOnNext {
                    deliveryApi.delete(it.flatMap { it.deliveries }.map { it.id })
                    routeApi.delete(it.map { it.id })
                }
                .retryOnTokenExpiry(domain)
                .ignoreElements()
                .onErrorComplete { e ->
                    when (e) {
                        is NoSuchElementException -> true
                        else -> false
                    }
                }
                .subscribeOn(domain.scheduler)
    }

    /**
     * Optimize a tour
     * @param tour Tour to optimize
     * @param options Optimization options
     * @param uidSupplier A supplier generating uid's for newly created tours
     * @return Single observable of optimized tours
     */
    fun optimize(
            tour: Tour,
            options: TourOptimizationOptions,
            uidSupplier: () -> String
    ): Single<List<Tour>> {
        val domain = domain(customerId)

        val routeApi = this.proxy(RouteExtendedApi::class.java, customerId = customerId)

        val inPlaceUpdate = options.vehicles == null
        val vehicleCount = options.vehicles?.count() ?: 1

        return routeApi.optimize(
                routingInput = tour.toRoutingInput(
                        options
                ),
                numvehicles = vehicleCount
        )
                .map { routes ->
                    if (routes.count() > vehicleCount)
                        throw IllegalStateException("Amount of optimized routes [${routes.count()}] is not supposed " +
                                "to exceed number of vehicles [${vehicleCount}]")

                    val now = Date()
                    routes.map { route ->
                        val stops = route.deliveries
                                .sortedBy { it.orderindex }
                                .map { delivery ->
                                    tour.stops.first { it.uid == delivery.customId }
                                            .also {
                                                it.route = TourStopRouteMeta(
                                                        etaFrom = delivery.etaFrom,
                                                        etaTo = delivery.etaTo,
                                                        driverFrom = delivery.ddtFrom,
                                                        driverTo = delivery.ddtTo,
                                                        estimatedStayLength = delivery.els,
                                                        stayLengtH = delivery.als?.toInt(),
                                                        targetFrom = delivery.tdtFrom,
                                                        targetTo = delivery.tdtTo
                                                )
                                            }
                                }

                        val orders = stops
                                .flatMap { it.tasks }
                                .map { it.orderId }.distinct()
                                .map { orderId -> tour.orders.first { it.id == orderId } }

                        val uid: String
                        if (inPlaceUpdate) {
                            uid = tour.uid ?: throw IllegalArgumentException("Uid required for in place update")

                            this.deleteRoutes(uids = listOf(uid))
                                    .blockingAwait()
                        } else {
                            // Generate uid for new tour
                            uid = uidSupplier()
                        }

                        // Update smartlane route custom id with tour uid
                        routeApi.patchRouteById(
                                route.id,
                                Route().also {
                                    it.customId = tour.smartlaneCustomId
                                }
                        )

                        Tour(
                                id = null,
                                uid = uid,
                                nodeUid = tour.nodeUid,
                                userId = tour.userId,
                                stationNo = tour.stationNo,
                                parentId = tour.id,
                                optimized = now,
                                stops = stops,
                                orders = orders,

                                route = TourRouteMeta(
                                        start = route.ast,
                                        targetFrom = route.tstFrom,
                                        targetTo = route.tstTo,
                                        distance = route.distance?.let { it.toDouble() / 1000 },
                                        totalDuration = route.grossDuration,
                                        drivingTime = route.netDuration
                                )
                        )
                    }
                }
                .retryOnTokenExpiry(domain)
                .firstOrError()
                .subscribeOn(domain.scheduler)
    }

    /**
     * Formats an email to include identity
     */
    private fun formatEmail(email: String) = "${email} ${identity.shortUid}"

    /**
     * Transform domain user to smartlane driver
     */
    private fun UserService.User.toDriver(): Driver {
        return Driver().also {
            it.companyId = 1
            it.vehicle = "car"
            it.usertype = "driver"
            it.email = formatEmail(this.email)
            it.firstname = this.firstName
            it.lastname = this.lastName
            it.mobilenr = this.phoneMobile ?: "n/a"
            it.isActive = this.active
            it.htmlcolor = this.email.hashCode().toHexString().take(6)
        }
    }

    /**
     * Smartlane custom id extension
     */
    private val Tour.smartlaneCustomId: String
        get() = CustomId(
                id = this.id!!,
                uid = this.uid!!
        )
                .serialize()

    /**
     * Transform tour into smartlane routing input
     * @param options Optimization options
     */
    private fun Tour.toRoutingInput(
            options: TourOptimizationOptions
    ): Routinginput {
        return Routinginput().also {
            val omitLoads = options.omitLoads ?: false

            it.deliverydata = this.stops
                    .map { stop ->
                        Routedeliveryinput().also {
                            stop.address?.also { address ->
                                it.contactcompany = address.line1
                                it.contactlastname = address.line2
                                it.notes = address.line3
                                it.street = address.street
                                it.housenumber = address.streetNo
                                it.city = address.city
                                it.postalcode = address.zipCode
                                address.geoLocation?.also { geo ->
                                    it.lat = geo.latitude.toString()
                                    it.lng = geo.longitude.toString()
                                }
                                address.countryCode = address.countryCode

                                if (!omitLoads)
                                    it.load = stop.weight?.let { (it * 100.0).toInt() }
                            }

                            // Track stop via custom id
                            it.customId = stop.uid

                            if (!options.appointments.omit) {
                                it.pdtFrom = stop.appointmentStart
                                it.pdtTo = stop.appointmentEnd
                            }
                        }
                    }
                    .also {
                        // Current time
                        val now = Date()

                        if (!options.appointments.omit) {

                            if (options.appointments.replaceDatesWithToday) {
                                it.forEach {
                                    it.pdtFrom = it.pdtFrom?.replaceDate(now)
                                    it.pdtTo = it.pdtTo?.replaceDate(now)
                                }
                            }

                            val earliestAppointmentTime by lazy {
                                (it.mapNotNull { it.pdtFrom }.min() ?: Date()).toLocalDateTime()
                            }

                            if (options.appointments.shiftHoursFromNow != null) {

                                fun Date.shiftAppointmentTime(): Date =
                                        now.toLocalDateTime()
                                                // Round to next full hour
                                                .truncatedTo(ChronoUnit.HOURS)
                                                .plus(Duration.ofHours(1))
                                                // Add duration between earlist appointment time and this one
                                                .plus(Duration.between(
                                                        earliestAppointmentTime,
                                                        this.toLocalDateTime()))
                                                // Add shift offset
                                                .plus(Duration.ofHours(
                                                        options.appointments.shiftHoursFromNow?.toLong() ?: 0))
                                                .toDate()


                                it.forEach {
                                    it.pdtFrom = it.pdtFrom?.shiftAppointmentTime()
                                    it.pdtTo = it.pdtTo?.shiftAppointmentTime()
                                }
                            }

                            if (options.appointments.shiftDaysFromNow != null) {

                                fun Date.shiftAppointmentTime(): Date =
                                        this.toLocalDateTime()
                                                .plusDays(
                                                        Duration.between(
                                                                earliestAppointmentTime,
                                                                now.toLocalDateTime()
                                                        ).toDays()
                                                )
                                                .plusDays(options.appointments.shiftDaysFromNow?.toLong() ?: 0)
                                                .toDate()

                                it.forEach {
                                    it.pdtFrom = it.pdtFrom?.shiftAppointmentTime()
                                    it.pdtTo = it.pdtTo?.shiftAppointmentTime()
                                }
                            }

                            // Sanity checks
                            it.forEach {
                                it.pdtTo = if (it.pdtTo != null && it.pdtTo > now) it.pdtTo else null
                                it.pdtFrom = if (it.pdtTo != null) it.pdtFrom else null

                                log.trace { "PDT ${it.pdtFrom} -> ${it.pdtTo}" }
                            }
                        }
                    }

            // Set start address
            options.start?.also { startAddress ->
                it.startaddress = Inputaddress().also {
                    it.street = startAddress.street
                    it.housenumber = startAddress.streetNo
                    it.postalcode = startAddress.zipCode
                    it.city = startAddress.city
                    it.country = startAddress.countryCode
                    startAddress.geoLocation?.also { location ->
                        it.lat = location.latitude
                        it.lng = location.longitude
                    }
                }
            }

            if (!omitLoads)
                it.vehcapacities = options.vehicles?.map { (it.capacity * 100).toInt() }
        }
    }
}