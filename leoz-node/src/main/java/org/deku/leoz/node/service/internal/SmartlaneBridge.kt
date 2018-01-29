package org.deku.leoz.node.service.internal

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.internal.schedulers.SchedulerWhen
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.service.internal.TourServiceV1
import org.deku.leoz.service.internal.TourServiceV1.*
import org.deku.leoz.service.internal.id
import org.deku.leoz.service.internal.uid
import org.deku.leoz.smartlane.SmartlaneApi
import org.deku.leoz.smartlane.api.*
import org.deku.leoz.smartlane.model.Inputaddress
import org.deku.leoz.smartlane.model.Routedeliveryinput
import org.deku.leoz.smartlane.model.Routinginput
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ResponseStatus
import org.threeten.bp.Duration
import org.threeten.bp.temporal.ChronoUnit
import sx.LazyInstance
import sx.log.slf4j.info
import sx.log.slf4j.trace
import sx.rs.client.RestEasyClient
import sx.time.replaceDate
import sx.time.toDate
import sx.time.toLocalDateTime
import java.net.URI
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import kotlin.concurrent.withLock

/**
 * Bridge operations between leoz and smartlane
 * Created by masc on 15.11.17.
 */
class SmartlaneBridge {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Smartlane base URI */
    private val baseUri = URI.create("https://dispatch.smartlane.io/")

    /** Default customer id */
    private val customerId = "der-kurier-test"

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
     * Get rest client proxy for specific customer.
     * This method will authenticate synchronously if necessary.
     * @param serviceClass Service class
     * @param customerId Smartlane customer id
     */
    private fun <T> proxy(serviceClass: Class<T>, customerId: String): T {
        return this.restClient.proxy(
                serviceClass,
                path = customerId,
                jwtToken = this.domain(customerId).jwtToken)
    }

    /**
     * Retries an operation which consumes smartlane REST apis in case of token expiry.
     * The REST proxy creation must be part of the observable (not cached) for this extension to work.
     */
    fun <T> Observable<T>.retryOnTokenExpiry(domain: Domain): Observable<T> {
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
     * Optimize a tour
     * @param tour Tour to optimize
     * @param options Optimization options
     * @return Single observable of optimized tours
     */
    fun optimize(
            tour: Tour,
            options: TourOptimizationOptions
    ): Single<List<Tour>> {
        val domain = domain(customerId)

        return Observable.fromCallable {
            // Create proxies within observable (@see #retryOnTokenExpiry)
            this.proxy(RouteExtendedApi::class.java, customerId = customerId)
        }
                .flatMap {
                    it.optimize(tour.toRoutingInput(
                            options
                    ))
                }
                .map { routes ->
                    val now = Date()
                    routes.map { route ->
                        val stops = route.deliveries
                                .sortedBy { it.orderindex }
                                .map { delivery ->
                                    tour.stops.first { it.uid == delivery.customId }
                                }

                        val orders = stops
                                .flatMap { it.tasks }
                                .map { it.orderId }.distinct()
                                .map { orderId -> tour.orders.first { it.id == orderId } }

                        Tour(
                                id = null,
                                nodeUid = tour.nodeUid,
                                userId = tour.userId,
                                stationNo = tour.stationNo,
                                deliverylistId = tour.deliverylistId,
                                optimized = now,
                                stops = stops,
                                orders = orders
                        )
                    }
                }
                .retryOnTokenExpiry(domain)
                .firstOrError()
                .subscribeOn(domain.scheduler)
    }

    /**
     * Transform tour into smartlane routing input
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
                                it.contactlastname = address.line1
                                it.contactfirstname = address.line2
                                it.contactcompany = address.line3
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
                    it.postalcode = startAddress.zipCode
                    it.city = startAddress.city
                    it.country = startAddress.countryCode
                    it.housenumber = startAddress.streetNo
                    startAddress.geoLocation?.also { location ->
                        it.lat = location.latitude
                        it.lng = location.longitude
                    }
                }
            }

            if (it.startaddress == null) {
                this.stops.firstOrNull()?.address?.also { startAddress ->
                    it.startaddress = Inputaddress().also {
                        it.street = startAddress.street
                        it.postalcode = startAddress.zipCode
                        it.city = startAddress.city
                        it.country = startAddress.countryCode

                        //region TODO: workaround for smartlane issue, where start address not being properly recognized / placed at end of tour (omit geo & street no)
//                    it.housenumber = startAddress.streetNo
//                    startAddress.geoLocation?.also { location ->
//                        it.lat = location.latitude
//                        it.lng = location.longitude
//                    }
                        //endregion
                    }
                }

            }

            if (!omitLoads)
                it.vehcapacities = options.vehicles?.map { (it.capacity * 100).toInt() }
        }
    }
}