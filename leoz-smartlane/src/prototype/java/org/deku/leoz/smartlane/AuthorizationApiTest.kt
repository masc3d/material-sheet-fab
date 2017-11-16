package org.deku.leoz.smartlane

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import org.deku.leoz.smartlane.api.AuthApi
import org.deku.leoz.smartlane.api.AuthorizationApi
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
                    it.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
                }
        )
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
}