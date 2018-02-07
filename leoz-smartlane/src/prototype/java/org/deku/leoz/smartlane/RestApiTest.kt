package org.deku.leoz.smartlane

import org.deku.leoz.smartlane.api.*
import org.deku.leoz.smartlane.model.Address
import org.deku.leoz.smartlane.model.Driver
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.log.slf4j.trace
import sx.rs.client.RestEasyClient
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
    fun testAddressSearch() {
        this.authorize()

        restClient.proxy(AddressApi::class.java).also {
            log.trace {
                it.getSearchaddress("{id:[523 TO 524]}", 1, false)
            }
        }
    }

    @Test
    fun testAddressDeleteAll() {
        this.authorize()

        val internalApi = restClient.proxy(AddressExtendedApi::class.java)

        internalApi.deleteAddressesNotIn(listOf(1))
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
                        .blockingSubscribe {
                            log.trace("Delivery ${it.id}")
                        }
            }
        }
    }

    @Test
    fun testDeliveryDeleteAll() {
        this.authorize()

        val internalApi = restClient.proxy(DeliveryExtendedApi::class.java)

        internalApi.deleteDelivery("{}")
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
    fun testRouteGetByCustomId() {
        this.authorize()

        restClient.proxy(RouteExtendedApi::class.java).also { routeApi ->
            log.trace {
                routeApi.getRouteByCustomId("280")
                        .blockingFirst()
            }
        }
    }

    @Test
    fun testRouteDeleteAll() {
        this.authorize()

        val internalApi = restClient.proxy(RouteExtendedApi::class.java)

        internalApi.deleteRoute("{}")
    }

    @Test
    fun testClean() {
        // Clean all data from smartlane container
        this.authorize()

        val deliveryApi = restClient.proxy(DeliveryExtendedApi::class.java)
        val routeApi = restClient.proxy(RouteExtendedApi::class.java)
        val addressApi = restClient.proxy(AddressExtendedApi::class.java)
        val driverApi = restClient.proxy(DriverExtendedApi::class.java)
        val drivertrackingApi = restClient.proxy(DrivertrackingExtendedApi::class.java)

        deliveryApi.deleteAll()
        routeApi.deleteAll()
        drivertrackingApi.deleteAll()
        // TODO: drivers must not be deleted until it's thoroughly implmented @smartlane, otherwise causing conflicts
//        driverApi.deleteAll()
        addressApi.deleteAddressesNotIn(
                // Exclude the company/owner address id
                listOf(1)
        )
    }

    @Test
    fun testDriverGetByEmail() {
        this.authorize()

        val driverApi = restClient.proxy(DriverApi::class.java)

        log.trace {
            driverApi.getDriverByEmail("masc@disappear.de")
        }
    }

    @Test
    fun testDriverGet() {
        this.authorize()

        val driverApi = restClient.proxy(DriverApi::class.java)

        log.trace {
            driverApi.getDriver("{}", true, true, true)
        }
    }

    @Test
    fun testDriverPost() {
        this.authorize()

        val driverApi = restClient.proxy(DriverApi::class.java)

        driverApi.postDriver(
                Driver().also {
                    it.companyId = 1
                    it.firstname = "first"
                    it.lastname = "lst"
                    it.email = "meh@meh.com"
                    it.usertype = "driver"
                    it.vehicle = "car"
                    it.isActive = false
                    it.mobilenr = "0000"
                    it.htmlcolor = "000000"
                }
        )
    }

    @Test
    fun testDriverDeleteAll() {
        this.authorize()

        val internalApi = restClient.proxy(DriverExtendedApi::class.java)

        internalApi.deleteAll()
    }
}