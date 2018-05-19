package org.deku.leoz.node.service.smartlane

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.internal.schedulers.SchedulerWhen
import io.reactivex.rxkotlin.merge
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.identity.Identity
import org.deku.leoz.model.TimeRange
import org.deku.leoz.model.TourRouteMeta
import org.deku.leoz.model.TourStopRouteMeta
import org.deku.leoz.service.internal.LocationServiceV2
import org.deku.leoz.service.internal.TourServiceV1.Tour
import org.deku.leoz.service.internal.TourServiceV1.TourOptimizationOptions
import org.deku.leoz.service.internal.UserService.User
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
import sx.time.TimeSpan
import sx.time.plusDays
import sx.time.plusMinutes
import sx.time.replaceDate
import sx.time.threeten.toDate
import sx.time.threeten.toLocalDateTime
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
    private val CONTAINER_PATH_TEST = "der-kurier-test"

    /** Smartlane administrative credentials for all containers */
    private val ADMIN_USERNAME = "dekuadmin@smartlane.de"
    private val ADMIN_PASSWORD = "d7#PfDKiv9.M"

    @Inject
    private lateinit var identity: Identity

    /** Smartlane container */
    data class Container(
            val path: String
    )

    /** Smartlaner container resolver interface */
    interface Resolver {
        /**
         * Resolve container by tour
         * @throws NoSuchElementException if container could not be resolved
         */
        fun containerByTour(tour: Tour): Container

        /**
         * Resolve container by user
         * @throws NoSuchElementException if container could not be resolved
         */
        fun containerByUser(user: User): Container
    }

    @Inject
    private lateinit var resolver: Resolver

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
            val container: Container
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
                log.trace { "Authorizing at [${container}]" }

                this@SmartlaneBridge.restClient
                        .proxy(AuthApi::class.java,
                                path = container.path)
                        .auth(AuthApi.Request(
                                email = ADMIN_USERNAME,
                                password = ADMIN_PASSWORD
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

        /**
         * Get rest client proxy for specific container.
         * This method will authenticate synchronously (via `Domain`) if necessary.
         *
         * @param serviceClass service class
         * @param container smartlane conatiner
         */
        fun <T> proxy(serviceClass: Class<T>): T {
            return this@SmartlaneBridge.restClient.proxy(
                    serviceClass,
                    path = container.path,
                    jwtToken = { this.jwtToken }
            )
        }
    }

    /** Map of containers to smartlane domains */
    private val domains = ConcurrentHashMap<Container, Domain>()

    fun Container.domain(): Domain {
        return domains.getOrPut(this, {
            Domain(this)
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
            user: User
    ): Completable {
        val domain = resolver.containerByUser(user).domain()

        return Observable.fromCallable {
            val driverApi = domain.proxy(DriverApi::class.java)

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
    private fun getDriverId(user: User): Int? {
        val domain = resolver.containerByUser(user).domain()
        val driverApi = domain.proxy(DriverApi::class.java)

        return synchronized(this.driverIdByEmail) {
            this.driverIdByEmail.getOrPut(
                    this.formatEmail(user.email),
                    // Default value -> determine driver (id)
                    {
                        Callable {
                            driverApi.getDriverByEmail(
                                    email = this.formatEmail(user.email)
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
    fun hasDriver(user: User): Boolean =
            this.getDriverId(user) != null

    /**
     * Update a drivers geo position
     * @param user user
     * @param positions Driver's geo positions
     */
    fun putDriverPosition(
            user: User,
            positions: Iterable<LocationServiceV2.GpsDataPoint>
    ): Completable {
        val domain = resolver.containerByUser(user).domain()

        val driverId = this.getDriverId(user)
                ?: throw NoSuchElementException("Driver not found")

        return Observable
                .fromIterable(positions)
                // Take it slow with position batch updates, to avoid smartlane request limit
                .window(1, TimeUnit.SECONDS, 10)
                .flatMap { positionWindow ->
                    positionWindow.flatMap { position ->
                        Observable.fromCallable {
                            val driverApi = domain.proxy(DriverApi::class.java)

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
     * Delete smartlane routes.
     */
    fun deleteRoutes(tours: List<Tour>): Completable {
        val byContainer = tours
                .groupBy {
                    try {
                        resolver.containerByTour(it)
                    } catch (e: NoSuchElementException) {
                        log.trace { "Could not resolve container for tour, thus ignoring [${it.uid}] [${e.message}]" }
                        null
                    }
                }

        return byContainer.keys
                .filterNotNull()
                .map { container ->
                    val domain = container.domain()
                    val domainTours = byContainer.getValue(container)

                    val routeApi = domain.proxy(RouteExtendedApi::class.java)
                    val deliveryApi = domain.proxy(DeliveryExtendedApi::class.java)

                    this.getRoutes(domainTours)
                            // Collect for batch deletion
                            .toList().toObservable()
                            .flatMap { routes ->
                                deliveryApi.delete(routes.flatMap { it.deliveries }.map { it.id })
                                        .concatWith(
                                                routeApi.delete(routes.map { it.id })
                                        )
                                        .toObservable<Unit>()
                            }
                            .composeRest(domain)
                            .onErrorResumeNext { e: Throwable ->
                                when (e) {
                                    is NoSuchElementException -> Observable.empty()
                                    else -> throw e
                                }
                            }
                }
                .merge()
                .ignoreElements()
    }

    /**
     * Get routes from tours
     * @param tours Tours
     */
    private fun getRoutes(tours: List<Tour>): Observable<Route> {
        // Make sure all tours belong to the same container, as this method does not support crossdomain lookups
        val containers = tours
                .groupBy { resolver.containerByTour(it) }
                .keys
                .filterNotNull()

        if (containers.count() > 1)
            throw UnsupportedOperationException("Cross domain lookup not supported for batch route retrieval")

        val container = containers.first()
        val domain = container.domain()

        val routeApi = domain.proxy(RouteExtendedApi::class.java)

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
        val byContainer = tours.groupBy { resolver.containerByTour(it) }

        return byContainer.keys
                .filterNotNull()
                .map { container ->
                    val domain = container.domain()
                    val domainTours = byContainer.getValue(container)

                    val routeApi = domain.proxy(RouteExtendedApi::class.java)

                    this.getRoutes(domainTours)
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
                            .onErrorResumeNext { e: Throwable ->
                                when (e) {
                                    is NoSuchElementException -> Observable.empty()
                                    else -> throw e
                                }
                            }
                }
                .merge()
                .ignoreElements()
    }


    /**
     * Assign driver to route
     * @param email User / driver email
     * @param tour Tour to assign driver to
     */
    fun assignDriver(
            user: User,
            tour: Tour
    ): Completable {
        val domain = resolver.containerByTour(tour).domain()
        val routeApi = domain.proxy(RouteExtendedApi::class.java)

        return this.getDriverId(user)
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
                ?: Completable.error(NoSuchElementException("Driver [${user.email}] not found"))
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
        val domain = resolver.containerByTour(tour).domain()
        val routeApi = domain.proxy(RouteExtendedApi::class.java)

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
                    // Sanity checks
                    if (routes.count() > vehicleCount)
                        throw IllegalStateException("Amount of optimized routes [${routes.count()}] is not supposed " +
                                "to exceed number of vehicles [${vehicleCount}]")


                    val missingStopUids = (tour.stops?.map { it.uid } ?: listOf())
                            .subtract(
                                    routes.flatMap { it.deliveries.map { it.customId } }
                            )

                    if (missingStopUids.count() > 0) {
                        val missingStops = missingStopUids.map { uid -> tour.stops?.first { it.uid == uid } }

                        throw IllegalStateException(
                                "${routes.count()} optimized route(s) are missing ${missingStops.count()} stops"
                        )
                    }

                    // Transform route to tour
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
                    val deliveryApi = domain.proxy(DeliveryExtendedApi::class.java)

                    try {
                        deliveryApi.deleteUnreferenced()
                    } catch (t: Throwable) {
                        log.warn { "Could not remove unreferenced deliveries [${t.message}]" }
                    }
                }
                .firstOrError()
    }

    /**
     * Clean routes, deliveries and drivertracking info from smartlane container
     */
    fun clean() {
        log.info { "Cleaning smartlane container [${CONTAINER_PATH_TEST}]" }
        val domain = Container(path = CONTAINER_PATH_TEST).domain()

        val deliveryApi = domain.proxy(DeliveryExtendedApi::class.java)
        val routeApi = domain.proxy(RouteExtendedApi::class.java)
        val addressApi = domain.proxy(AddressExtendedApi::class.java)
        val driverApi = domain.proxy(DriverExtendedApi::class.java)
        val drivertrackingApi = domain.proxy(DrivertrackingExtendedApi::class.java)

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
    private fun User.toDriver(): Driver {
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
                                it.appointmentStart = it.appointmentStart?.replaceDate(tourDate)
                                it.appointmentEnd = it.appointmentEnd?.replaceDate(tourDate)
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
                vehicleType = vehicleType,
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

            it.startTime = options.startTime
            it.vehcapacities = vehicles.map { Math.ceil(it.capacity).toInt() }
        }
    }
}