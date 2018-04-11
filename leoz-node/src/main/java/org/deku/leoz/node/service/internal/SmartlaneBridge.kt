package org.deku.leoz.node.service.internal

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.internal.schedulers.SchedulerWhen
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.identity.Identity
import org.deku.leoz.model.TimeRange
import org.deku.leoz.model.TourRouteMeta
import org.deku.leoz.model.TourStopRouteMeta
import org.deku.leoz.service.internal.LocationServiceV2
import org.deku.leoz.service.internal.TourServiceV1.Tour
import org.deku.leoz.service.internal.TourServiceV1.TourOptimizationOptions
import org.deku.leoz.service.internal.UserService
import org.deku.leoz.service.internal.uid
import org.deku.leoz.smartlane.SmartlaneApi
import org.deku.leoz.smartlane.api.*
import org.deku.leoz.smartlane.model.*
import org.deku.leoz.time.toShortDate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.threeten.bp.Duration
import org.threeten.bp.temporal.ChronoUnit
import sx.LazyInstance
import sx.io.serialization.Serializable
import sx.log.slf4j.info
import sx.log.slf4j.trace
import sx.log.slf4j.warn
import sx.rs.client.RestEasyClient
import sx.rx.toObservable
import sx.text.toHexString
import sx.time.*
import java.net.URI
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import kotlin.NoSuchElementException
import kotlin.concurrent.withLock


/**
 * Bridge operations between leoz and smartlane
 * Created by masc on 15.11.17.
 */
@Component
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
            val id: Long? = null,
            /** Domain tour uid. Required for uniquely mapping tours in shared container */
            val shortUid: String
    ) {
        companion object {
            private val SEPARATOR = "/"

            /** Helper to create smartlane short uid from uuid */
            fun UUID.toSmartlaneUid(): String = this.mostSignificantBits.shr(8 * 4).toInt().toHexString()

            /** Deserialize from smartlane */
            fun deserialize(value: String): CustomId {
                return value.split(SEPARATOR)
                        .map { it.trim() }
                        .let {
                            when (it.size) {
                            // Custom id with uid only
                                1 -> CustomId(
                                        shortUid = it.get(0))
                                else -> CustomId(
                                        id = it.get(0).toLong(),
                                        shortUid = it.get(1))
                            }
                        }
            }

            fun create(
                    id: Long? = null,
                    uid: UUID): CustomId {
                return CustomId(
                        id = id,
                        shortUid = uid.toSmartlaneUid()
                )
            }
        }

        /** Serialize to smartlane */
        fun serialize(): String =
                if (id == null)
                    shortUid
                else
                    arrayOf(id, shortUid).joinToString(SEPARATOR)

        override fun toString(): String =
                this.serialize()
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
    private fun <T> Observable<T>.composeRest(domain: Domain): Observable<T> {
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
                .subscribeOn(domain.scheduler)
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
                .composeRest(domain)
                .ignoreElements()
    }

    /** Driver id by email. Also caches misses (-> null) */
    private val driverIdByEmail = mutableMapOf<String, Int?>()

    /**
     * Get driver id with caching support
     * @param email User / driver email
     */
    private fun getDriverId(email: String): Int? {
        val domain = domain(customerId)

        val driverApi = this.proxy(DriverApi::class.java, customerId = customerId)

        return synchronized(this.driverIdByEmail) {
            this.driverIdByEmail.getOrPut(
                    this.formatEmail(email),
                    // Default value -> determine driver (id)
                    {
                        Callable {
                            driverApi.getDriverByEmail(
                                    email = this.formatEmail(email)
                            )
                                    ?.id
                        }
                                .toObservable()
                                .composeRest(domain)
                                .blockingFirst(null)
                    }
            )
        }
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
                                .composeRest(domain)
                    }
                }
                .ignoreElements()
                .subscribeOn(domain.scheduler)
    }

    /**
     * Delete smartlane routes
     */
    fun deleteRoutes(tours: List<Tour>): Completable {
        val domain = domain(customerId)

        val routeApi = this.proxy(RouteExtendedApi::class.java, customerId = customerId)
        val deliveryApi = this.proxy(DeliveryExtendedApi::class.java, customerId = customerId)

        return this.getRoutes(tours)
                // Collect for batch deletion
                .toList()
                .toObservable()
                .doOnNext {
                    deliveryApi.delete(it.flatMap { it.deliveries }.map { it.id })
                    routeApi.delete(it.map { it.id })
                }
                .composeRest(domain)
                .ignoreElements()
                .onErrorComplete { e ->
                    when (e) {
                        is NoSuchElementException -> true
                        else -> false
                    }
                }
    }

    /**
     * Get routes from tours
     * @param tours Tours
     */
    private fun getRoutes(tours: List<Tour>): Observable<Route> {
        val domain = domain(customerId)

        val routeApi = this.proxy(RouteExtendedApi::class.java, customerId = customerId)

        return routeApi.getRouteByCustomIdSubstring(
                *tours.map {
                    CustomId.create(uid = UUID.fromString(it.uid)).serialize()
                }.toTypedArray()
        )
                .composeRest(domain)
    }

    /**
     * Update smartlane routes from tours
     * TODO: limited support, currently only custom ids are updated
     * @param tours Tours
     */
    fun updateRoutes(tours: List<Tour>): Completable {
        val domain = domain(customerId)

        val routeApi = this.proxy(RouteExtendedApi::class.java, customerId = customerId)

        return this.getRoutes(tours)
                .doOnNext { route ->
                    val customId = CustomId.deserialize(route.customId)

                    // Backreference tours from parsed custom id
                    tours.firstOrNull { it.uid?.startsWith(customId.shortUid) ?: false }
                            ?.also { tour ->
                                // Update route with complete custom id
                                routeApi.patchRouteById(
                                        route.id,
                                        Route().also {
                                            it.customId = CustomId.create(
                                                    id = tour.id,
                                                    uid = UUID.fromString(tour.uid)
                                            ).serialize()
                                        }
                                )
                            }
                }
                .composeRest(domain)
                .ignoreElements()
                .onErrorComplete { e ->
                    when (e) {
                        is NoSuchElementException -> true
                        else -> false
                    }
                }
    }

    /**
     * Assign driver to route
     * @param email User / driver email
     * @param tour Tour to assign driver to
     */
    fun assignDriver(
            email: String,
            tour: Tour
    ): Completable {
        val domain = domain(customerId)

        val routeApi = this.proxy(RouteExtendedApi::class.java, customerId = customerId)

        return this.getDriverId(email)
                ?.let { driverId ->
                    this.getRoutes(listOf(tour))
                            .firstElement()
                            .doOnSuccess { route ->
                                routeApi.patchRouteById(
                                        route.id,
                                        Route().also {
                                            it.driverId = driverId
                                        }
                                )
                            }
                            .toObservable()
                            .composeRest(domain)
                            .ignoreElements()
                }
                ?: Completable.error(NoSuchElementException("Driver [${email}] not found"))
    }

    /**
     * Optimize a tour
     * @param tour Tour to optimize
     * @param options Optimization options
     * @return Single observable of optimized tours
     */
    fun optimize(
            tour: Tour,
            options: TourOptimizationOptions
    ): Single<List<Tour>> {
        // The smartlane domain to interact with
        val domain = domain(customerId)

        // Smartlane APIs
        val routeApi = this.proxy(RouteExtendedApi::class.java, customerId = customerId)

        val vehicleCount = (options.vehicles?.count() ?: 0).let {
            if (it > 0) it else 1
        }

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

                    routes.map { route ->
                        // Create optimized (partial) tour from original one and route information
                        tour.toOptimizedTour(
                                options = options,
                                route = route
                        )
                    }
                            .also { optimizedTours ->
                                // Delete previous routes relating to optimized tours
                                this.deleteRoutes(optimizedTours)
                                        .blockingAwait()

                                // Update smartlane routes custom id with tour uid
                                optimizedTours.forEachIndexed { index, optimizedTour ->
                                    routeApi.patchRouteById(
                                            routes[index].id,
                                            Route().also {
                                                it.customId = CustomId.create(
                                                        id = optimizedTour.id,
                                                        uid = UUID.fromString(optimizedTour.uid)
                                                ).serialize()
                                            }
                                    )
                                }
                            }
                }
                .composeRest(domain)
                .doOnError {
                    val deliveryApi = this.proxy(DeliveryExtendedApi::class.java, customerId = customerId)

                    try {
                        deliveryApi.deleteUnreferenced()
                    } catch(t: Throwable) {
                        log.warn { "Could not remove unreferenced deliveries [${t.message}]" }
                    }
                }
                .firstOrError()
    }

    /**
     * Clean routes, deliveries and drivertracking info from smartlane container
     */
    fun clean() {
        log.info { "Cleaning smartlane container [${customerId}]"}
        val deliveryApi = this.proxy(DeliveryExtendedApi::class.java, customerId)
        val routeApi = this.proxy(RouteExtendedApi::class.java, customerId)
        val addressApi = this.proxy(AddressExtendedApi::class.java, customerId)
        val driverApi = this.proxy(DriverExtendedApi::class.java, customerId)
        val drivertrackingApi = this.proxy(DrivertrackingExtendedApi::class.java, customerId)

        deliveryApi.deleteAll()
        routeApi.deleteAll()
        drivertrackingApi.deleteAll()
        // TODO: drivers must not be deleted until it's thoroughly implmented @smartlane, otherwise causing conflicts
//        driverApi.deleteAll()
        // TODO. routes are still archived @smartlane, which will prevent removal of addresses
//        addressApi.deleteAddressesNotIn(
//                // Exclude the company/owner address id
//                listOf(1)
//        )
    }

    /**
     * Formats an email to include identity
     */
    private fun formatEmail(email: String) = "${email} ${identity.shortUid}"

    /**
     * Indicates if optimization options imply an in place update (tour is not split)
     */
    private val TourOptimizationOptions.isInPlaceOptimization: Boolean
        get() = (this.vehicles?.count() ?: 0) == 0

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
     * Transform tour to optimized tour using routing information.
     * @param options tour optimization options
     * @param route smartlane routing result
     */
    private fun Tour.toOptimizedTour(
            options: TourOptimizationOptions,
            route: Route): Tour {

        val tour = this

        val tourDate = Date().plusDays(options.appointments.shiftDaysFromNow ?: 0)

        val inPlaceUpdate = options.isInPlaceOptimization

        val stops = route.deliveries
                .sortedBy { it.orderindex }
                .map { delivery ->
                    val stops = tour.stops ?: listOf()
                    stops.first { it.uid == delivery.customId }
                            .also {
                                it.route = TourStopRouteMeta(
                                        eta = TimeRange(delivery.etaFrom, delivery.etaTo),
                                        driver = TimeRange(delivery.ddtFrom, delivery.ddtTo),
                                        delivery = TimeRange(delivery.deliveryFrom, delivery.deliveryTo),
                                        stayLengtH = delivery.als?.toInt(),
                                        target = TimeRange(delivery.tdtFrom, delivery.tdtTo)
                                )
                            }
                }

        val tourOrders = tour.orders ?: listOf()

        val orders = stops
                .flatMap { it.tasks }
                .map { it.orderId }.distinct()
                .map { orderId -> tourOrders.first { it.id == orderId } }

        // Determine optimized tour id / uid
        val id: Long?
        val uid: String
        val nodeUid: String?
        val parentId: Long?
        val customId: String?
        when (inPlaceUpdate) {
            true -> {
                uid = tour.uid ?: throw IllegalArgumentException("Uid required for in place update")
                id = tour.id ?: throw IllegalArgumentException("Id required for in place update")
                nodeUid = tour.nodeUid
                parentId = tour.parentId
                customId = tour.customId
            }
            false -> {
                // Generate uid for new tour
                uid = UUID.randomUUID().toString()
                id = null
                nodeUid = null
                parentId = tour.id
                customId = null
            }
        }

        return Tour(
                id = id,
                uid = uid,
                nodeUid = nodeUid,
                userId = tour.userId,
                stationNo = tour.stationNo,
                customId = customId,
                parentId = parentId,
                date = tourDate.toShortDate(),
                optimized = Date(),
                stops = stops,
                orders = orders,

                route = TourRouteMeta(
                        start = route.ast,
                        target = TimeRange(route.tstFrom, route.tstTo),
                        distance = route.distance?.let { it.toDouble() / 1000 },
                        totalDuration = route.grossDuration,
                        drivingTime = route.netDuration,
                        quality = this.calculateQuality()
                )
        )
    }

    /**
     * Calculate tour route quality based on etas
     * @return quality in percentage
     */
    private fun Tour.calculateQuality(): Double {
        val stops = this.stops ?: listOf()

        val stopsWithAppointments = stops.filter { it.appointmentEnd != null }

        if (stopsWithAppointments.count() == 0)
            return 1.0

        val stopsNotOverdue = stopsWithAppointments.filter {
            val from = it.route?.eta?.from ?: return@filter true
            val to = it.route?.eta?.to ?: return@filter true

            // Determine median eta
            val eta = from.plusMinutes(
                    (TimeSpan.between(from, to).totalMinutes / 2).toInt()
            )

            eta <= it.appointmentEnd
        }

        return stopsNotOverdue.count().toDouble() / stopsWithAppointments.count()
    }

    /**
     * Transform tour into smartlane routing input
     * @param options Optimization options
     */
    private fun Tour.toRoutingInput(
            options: TourOptimizationOptions
    ): Routinginput {
        return Routinginput().also {
            val omitLoads = options.omitLoads ?: false
            val vehicles = options.vehicles?.let {
                // When vehicles are empty instead of null, revert to default as well
                if (it.count() > 0) it else null
            } ?: listOf(TourOptimizationOptions.Vehicle())

            val stops = this.stops ?: listOf()

            it.deliverydata = stops
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
                                    it.load = stop.weight?.let { Math.ceil(it).toInt() }
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
                            //region Appointment transformations
                            // Replace appointment dates with today for optimization
                            it.forEach {
                                it.pdtFrom = it.pdtFrom?.replaceDate(now)
                                it.pdtTo = it.pdtTo?.replaceDate(now)
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
                            //endregion
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

            it.vehcapacities = vehicles.map { Math.ceil(it.capacity).toInt() }
        }
    }
}