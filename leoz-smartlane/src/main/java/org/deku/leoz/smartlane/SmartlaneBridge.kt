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
import java.util.concurrent.TimeUnit

/**
 * Bridge operations between leoz and smartlane
 * Created by masc on 15.11.17.
 */
class SmartlaneBridge {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val restClient by lazy {
        RestEasyClient(
                baseUri = URI.create("https://dispatch.smartlane.io/der-kurier-test/"),
                objectMapper = SmartlaneApi.mapper
        )
    }

    private fun authorize() {
        log.trace("Authorizing")
        this.restClient.jwtToken = restClient
                .proxy(AuthApi::class.java)
                .auth(AuthApi.Request(
                        email = "juergen.toepper@derkurier.de",
                        password = "PanicLane"
                ))
                .accessToken
                .also {
                    log.trace("Authorized")
                }
    }

    fun calculateRoute(routingInput: Routinginput): Observable<Route> {
        this.authorize()

        val routeApi = this.restClient.proxy(RouteApiGeneric::class.java)

        return Observable.fromCallable {
            // Start async route calculation
            routeApi.postCalcrouteOptimizedTimewindowAsync(body = routingInput)
        }
                .flatMap { status ->
                    Observable.fromCallable<RouteApiGeneric.SuccessMeta> {
                        // Poll status
                        routeApi.getProcessStatusById(
                                processId = status.processId
                        )
                    }
                            .retryWith(
                                    count = 100,
                                    action = { _, e ->
                                        when (e) {
                                        // Retry when pending
                                            is PendingException -> Observable.timer(1, TimeUnit.SECONDS)
                                            else -> throw e
                                        }
                                    }
                            )
                            .map {
                                this.restClient.proxy(RouteApi::class.java)
                                        .getRouteById(it.routeIds.first())
                            }
                }
    }
}