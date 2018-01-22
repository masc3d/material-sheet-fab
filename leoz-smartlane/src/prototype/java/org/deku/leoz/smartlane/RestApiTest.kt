package org.deku.leoz.smartlane

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.smartlane.api.*
import org.deku.leoz.smartlane.model.Address
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.log.slf4j.trace
import sx.rs.client.RestEasyClient
import sx.rx.limit
import java.net.URI

/**
 * Created by masc on 15.11.17.
 */
@Category(sx.junit.PrototypeTest::class)
class RestApiTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val restClient by lazy {
        RestEasyClient(
                baseUri = URI.create("https://dispatch.smartlane.io/der-kurier-test/"),
                connectionPoolSize = 4,
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

    private val addresses = listOf(
            Address().also {
                it.contactfirstname = "Jürgen"
                it.contactlastname = "Töpper"
                it.contactcompany = "Test"
                it.street = "Waldhof"
                it.housenumber = "1"
                it.city = "Schaafheim"
                it.postalcode = "64850"
            },
            Address().also {
                it.contactcompany = "DERKURIER"
                it.street = "Dörrwiese"
                it.housenumber = "2"
                it.city = "Neuenstein"
                it.postalcode = "36286"
            }
    )

    @Test
    fun testRefreshToken() {
        restClient.proxy(AuthorizationApi::class.java).also {
            log.trace("Token refreshed ${it.refreshToken}")
        }
    }

    @Test
    fun testAuth() {
        restClient.proxy(AuthApi::class.java).also {
            log.trace {
                it.auth(AuthApi.Request(
                        email = "juergen.toepper@derkurier.de",
                        password = "PanicLane"
                ))
            }
        }
    }

    @Test
    fun testAddressPost() {
        this.authorize()

        restClient.proxy(AddressApi::class.java).also {
            log.trace {
                it.postAddress(
                        Address().also {
                            it.contactcompany = "Test"
                            it.street = "Waldhof"
                            it.housenumber = "1"
                            it.city = "Schaafheim"
                            it.postalcode = "64850"
                        }
                )
            }
        }
    }

    @Test
    fun testAddressGet() {
        this.authorize()

        restClient.proxy(AddressApi::class.java).also {
            log.trace {
                it.address
            }
        }
    }

    @Test
    fun testAddressDelete() {
        this.authorize()

        val addressApi = restClient.proxy(AddressApi::class.java)
        val internalApi = restClient.proxy(InternalApi::class.java)

        addressApi.address.objects.forEach { address ->
            log.trace { "Deleting address [${address.id}]" }
            internalApi.deleteAddress(address.id)
        }
    }

    @Test
    fun testDeliveryStatus() {
        this.authorize()

        restClient.proxy(DeliveryApi::class.java).also { service ->
            val status = service.deliverystatus

            log.trace {
                status
            }
        }
    }

    @Test
    fun testDeliveryGet() {
        this.authorize()

        restClient.proxy(DeliveryApi::class.java).also {
            log.trace {
                it.getDelivery("{}")
                        .subscribe {
                            log.trace("Delivery ${it.id}")
                        }
            }
        }
    }

    @Test
    fun testDeliveryCancelAll() {
        this.authorize()

        restClient.proxy(DeliveryApi::class.java).also { deliveryApi ->
            deliveryApi.getDelivery("{}")
                    .doOnNext {
                        log.trace("fetched ${it.id}")
                    }
                    .subscribeOn(Schedulers.io().limit(4))
                    .blockingSubscribe {
                        log.trace("Cancelling delivery ${it.id}")
                        try {
                            deliveryApi.postCanceldeliveryById(it.id)
                        } catch (e: Exception) {
                            log.error(e.message)
                        }
                    }
        }
    }

    @Test
    fun testDeliveryDeleteAll() {
        this.authorize()

        val deliveryApi = restClient.proxy(DeliveryApi::class.java)
        val internalApi = restClient.proxy(InternalApi::class.java)

        deliveryApi.getDelivery("{}")
                .doOnNext {
                    log.trace("fetched ${it.id}")
                }
                .subscribeOn(Schedulers.io().limit(4))
                .blockingSubscribe {
                    log.trace("Deleting delivery ${it.id}")
                    try {
                        internalApi.deleteDelivery((it.id))
                    } catch (e: Exception) {
                        log.error(e.message)
                    }
                }
    }

    @Test
    fun testRouteStatus() {
        this.authorize()

        restClient.proxy(RouteApi::class.java).also { service ->
            log.trace {
                service.routestatus
            }
        }
    }

    @Test
    fun testRouteGet() {
        this.authorize()

        restClient.proxy(RouteApi::class.java).also { routeApi ->
            routeApi.getRoute("{}")
                    .blockingIterable()
                    .forEach {
                        log.trace("Route ${it.id}")
                    }
        }
    }

    @Test
    fun testRouteCancelAll() {
        this.authorize()

        restClient.proxy(RouteApi::class.java).also { routeApi ->
            routeApi.getRoute("{}")
                    .doOnNext {
                        log.trace("fetched ${it.id}")
                    }
                    .subscribeOn(Schedulers.io().limit(4))
                    .blockingSubscribe {
                        log.trace("Cancelling route ${it.id}")
                        try {
                            routeApi.postCancelrouteById(it.id)
                        } catch (e: Exception) {
                            log.error(e.message)
                        }
                    }
        }
    }


    @Test
    fun testRouteDeleteAll() {
        this.authorize()

        val routeApi = restClient.proxy(RouteApi::class.java)
        val internalApi = restClient.proxy(InternalApi::class.java)
        routeApi.getRoute("{}")
                .doOnNext {
                    log.trace("fetched ${it.id}")
                }
                .subscribeOn(Schedulers.io().limit(4))
                .blockingSubscribe {
                    log.trace("Deleting route ${it.id}")
                    try {
                        internalApi.deleteRoute(it.id)
                    } catch (e: Exception) {
                        log.error(e.message)
                    }
                }
    }
}