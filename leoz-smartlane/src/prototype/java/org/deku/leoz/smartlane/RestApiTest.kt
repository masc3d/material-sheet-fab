package org.deku.leoz.smartlane

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import org.deku.leoz.smartlane.api.AddressApi
import org.deku.leoz.smartlane.api.AuthApi
import org.deku.leoz.smartlane.api.AuthorizationApi
import org.deku.leoz.smartlane.model.Address
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
            log.trace(
                    it.auth(AuthApi.Request(
                            email = "juergen.toepper@derkurier.de",
                            password = "PanicLane"
                    ))
            )
        }
    }

    @Test
    fun testAddressPost() {
        this.authorize()

        restClient.proxy(AddressApi::class.java).also {
            log.trace(
                    it.postAddress(
                            Address().also {
                                it.contactcompany = "Test"
                                it.street = "Waldhof"
                                it.housenumber = "1"
                                it.city = "Schaafheim"
                                it.postalcode = "64850"
                            }
                    )
            )
        }
    }

    @Test
    fun testAddressGet() {
        this.authorize()

        restClient.proxy(AddressApi::class.java).also {
            log.trace(
                    it.address
            )
        }
    }
}