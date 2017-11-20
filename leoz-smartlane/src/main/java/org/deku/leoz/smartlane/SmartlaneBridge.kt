package org.deku.leoz.smartlane

import io.reactivex.Observable
import org.deku.leoz.smartlane.api.AuthApi
import org.deku.leoz.smartlane.api.PendingException
import org.deku.leoz.smartlane.api.RouteApi
import org.deku.leoz.smartlane.api.RouteApiGeneric
import org.deku.leoz.smartlane.api.getProcessStatusById
import org.deku.leoz.smartlane.api.postCalcrouteOptimizedTimewindowAsync
import org.deku.leoz.smartlane.model.Route
import org.deku.leoz.smartlane.model.Routinginput
import org.slf4j.LoggerFactory
import sx.rs.client.RestEasyClient
import sx.rx.retryWith
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Bridge operations between leoz and smartlane
 * Created by masc on 15.11.17.
 */
class SmartlaneBridge {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val baseUri = URI.create("https://dispatch.smartlane.io/")

    private val customerId = "der-kurier-test"

    private val restClient by lazy {
        RestEasyClient(
                baseUri = baseUri,
                objectMapper = SmartlaneApi.mapper
        )
    }

    /** Map of customer ids to authorization tokens */
    private val tokens = ConcurrentHashMap<String, String>()

    /**
     * Authorize smartlane customer
     * @param customerId Smartlane customer id
     */
    fun jwtToken(customerId: String): Observable<String> {
        return Observable.fromCallable {
            this.tokens.getOrPut(
                    customerId,
                    {
                        log.trace("Authorizing [${customerId}]")

                        this.restClient
                                .proxy(AuthApi::class.java,
                                        path = customerId)
                                .auth(AuthApi.Request(
                                        email = "juergen.toepper@derkurier.de",
                                        password = "PanicLane"
                                ))
                                .accessToken
                    }
            )
        }
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
                jwtToken = this.jwtToken(customerId).blockingFirst())
    }

    /**
     * Optimize route
     */
    fun optimizeRoute(routingInput: Routinginput): Observable<Route> {
        val routeApiGeneric by lazy { this.proxy(RouteApiGeneric::class.java, customerId = customerId) }
        val routeApi by lazy { this.proxy(RouteApi::class.java, customerId = customerId) }

        return Observable.fromCallable {
            // Start async route calculation
            routeApiGeneric.postCalcrouteOptimizedTimewindowAsync(
                    body = routingInput
            )
        }
                .flatMap { status ->
                    Observable.fromCallable<RouteApiGeneric.RouteProcessStatus> {
                        // Poll status
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
    }
}