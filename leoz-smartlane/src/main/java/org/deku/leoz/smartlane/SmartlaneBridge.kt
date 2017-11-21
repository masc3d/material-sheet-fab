package org.deku.leoz.smartlane

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.internal.schedulers.SchedulerWhen
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.smartlane.api.AuthApi
import org.deku.leoz.smartlane.api.PendingException
import org.deku.leoz.smartlane.api.RouteApi
import org.deku.leoz.smartlane.api.RouteApiGeneric
import org.deku.leoz.smartlane.api.getProcessStatusById
import org.deku.leoz.smartlane.api.postCalcrouteOptimizedTimewindowAsync
import org.deku.leoz.smartlane.model.Route
import org.deku.leoz.smartlane.model.Routinginput
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.rs.client.RestEasyClient
import sx.rx.retryWith
import sx.text.toHexString
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
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
            SchedulerWhen({
                workers -> Completable.merge(Flowable.merge(workers, 2))
            }, Schedulers.io())
        }

        private val authorizationLock = ReentrantLock()

        private val jwtTokenInstance = LazyInstance({
            Observable.fromCallable {
                // Authorize smartlane customer
                log.trace("Authorizing [${customerId}]")

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

    // TODO: transparently handle authentication failures/refresh token

    /**
     * Optimize route
     */
    fun optimizeRoute(routingInput: Routinginput): Observable<Route> {
        val routeApiGeneric by lazy { this.proxy(RouteApiGeneric::class.java, customerId = customerId) }
        val routeApi by lazy { this.proxy(RouteApi::class.java, customerId = customerId) }

        val id = routingInput.hashCode().toHexString()

        return Observable.fromCallable {
            log.trace("[${id}] Requesting route")
            // Start async route calculation
            routeApiGeneric.postCalcrouteOptimizedTimewindowAsync(
                    body = routingInput
            )
        }
                .flatMap { status ->
                    Observable.fromCallable<RouteApiGeneric.RouteProcessStatus> {
                        // Poll status
                        log.trace("[${id}] Requesting status")
                        routeApiGeneric.getProcessStatusById(
                                processId = status.processId
                        )
                    }
                            .retryWith(
                                    count = 100,
                                    action = { _, e ->
                                        when (e) {
                                        // Retry when pending
                                            is PendingException -> {
                                                log.trace("[${id}] Pending")
                                                Observable.timer(1, TimeUnit.SECONDS)
                                            }
                                            else -> throw e
                                        }
                                    }
                            )
                            .map {
                                routeApi
                                        .getRouteById(it.routeIds.first())
                            }
                }
                .subscribeOn(domain(customerId).scheduler)
    }
}