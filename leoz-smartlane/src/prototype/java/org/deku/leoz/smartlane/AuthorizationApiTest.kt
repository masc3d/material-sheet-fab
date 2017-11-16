package org.deku.leoz.smartlane

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import org.deku.leoz.smartlane.api.AddressApi
import org.deku.leoz.smartlane.api.AuthApi
import org.deku.leoz.smartlane.api.AuthorizationApi
import org.deku.leoz.smartlane.model.Address
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.log.slf4j.*
import sx.rs.client.RestEasyClient
import java.net.URI

/**
 * Created by masc on 15.11.17.
 */
@Category(sx.junit.PrototypeTest::class)
class AuthorizationApiTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val restClient by lazy {
        RestEasyClient(
                baseUri = URI.create("https://dispatch.smartlane.io/der-kurier-test/"),
                objectMapper = ObjectMapper().also {
                    // Don't epxlicitly serialize nulls with json (breaks swagger-ui too when having all those nulls in swagger.json)
                    it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    it.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
                    it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                }
        )
    }

    private fun auth() {
        this.restClient.jwtToken = restClient
                .proxy(AuthApi::class.java)
                .auth(AuthApi.Request(
                        email = "juergen.toepper@derkurier.de",
                        password = "PanicLane"
                ))
                .accessToken
    }

    private val jwtToken by lazy {
        restClient
                .proxy(AuthApi::class.java)
                .auth(AuthApi.Request(
                        email = "juergen.toepper@derkurier.de",
                        password = "PanicLane"
                ))
                .accessToken
    }

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
        this.auth()

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
        this.auth()

        restClient.proxy(AddressApi::class.java).also {
            log.trace(
                    it.address
            )
        }
    }
}