package org.deku.leoz.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.provider
import org.deku.leoz.service.internal.AuthorizationService
import org.deku.leoz.service.internal.BundleServiceV2
import org.deku.leoz.service.internal.StationService
import org.deku.leoz.service.internal.UserService
import org.deku.leoz.service.internal.entity.User
import sx.rs.proxy.RestClientProxy
import java.net.URI
import java.sql.Date

/**
 * Base class for JAX/RS REST client configurations
 * Created by masc on 07/11/2016.
 */
abstract class RestClientConfiguration {
    /**
     * Overridden in derived configurations to provide a specific proxy client
     */
    protected abstract fun createClientProxyImpl(baseUri: URI, ignoreSsl: Boolean): RestClientProxy

    /**
     * HTTP host to use for rest clients
     */
    var host: String= "localhost"

    /**
     * Connect via HTTPS
     */
    var https: Boolean = false

    /**
     * HTTP/S port
     */
    var port: Int = RestConfiguration.DEFAULT_PORT

    fun createUri(https: Boolean, host: String, port: Int, basePath: String): URI {
        val scheme = when(https) {
            true -> "https"
            false -> "http"
        }

        return URI("${scheme}://${host}:${port}")
                .resolve(basePath)
    }

    /**
     * Factory method for creating a leoz rest client
     * @param host REST server host name
     * @param port REST server port
     * @param https Use https or regular http. Defaults to false (=http)
     */
    fun createClientProxy(): RestClientProxy {
        val scheme = when(https) {
            true -> "https"
            false -> "http"
        }

        val uri = this.createUri(https, host, port, RestConfiguration.MAPPING_PREFIX)

        // Ignore SSL certificate for (usually testing/dev) remote hosts which are not in the business domain
        val ignoreSslCertificate = !host.endsWith(org.deku.leoz.config.HostConfiguration.Companion.CENTRAL_DOMAIN)

        return this.createClientProxyImpl(uri, ignoreSslCertificate)
    }

    companion object {
        val module = Kodein.Module {
            /**
             * Helper for creating service proxy
             */
            fun <T> createServiceProxy(config: RestClientConfiguration, serviceType: Class<T>): T {
                return config.createClientProxy().create(serviceType)
            }

            bind<StationService>() with provider {
                createServiceProxy(config = instance(), serviceType = StationService::class.java)
            }

            bind<BundleServiceV2>() with provider {
                createServiceProxy(config = instance(), serviceType = BundleServiceV2::class.java)
            }

            bind<UserService>() with provider {
                //createServiceProxy(config = instance(), serviceType = UserService::class.java)
                object : UserService {
                    override fun get(email: String?): User {
                        return User(
                                email = "foo@bar.com",
                                debitorId = 0,
                                stations = listOf("002", "020", "100"),
                                alias = "testuser",
                                role = User.ROLE_DRIVER,
                                password = "password",
                                salt = "salt",
                                firstName = "Foo",
                                lastName = "Bar",
                                apiKey = "a1b2c3d4e5g6",
                                active = true,
                                externalUser = false,
                                phone = "+491725405765",
                                expiresOn = Date(Date.parse("31.12.2099"))
                        )
                    }
                }
            }

            bind<AuthorizationService>() with provider {
                object : AuthorizationService {
                    override fun authorizeWeb(request: AuthorizationService.Credentials): AuthorizationService.WebResponse {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun authorizeMobile(request: AuthorizationService.MobileRequest): AuthorizationService.MobileResponse {
                        return AuthorizationService.MobileResponse(
                                key = "a1b2c3d4"
                        )
                    }
                }
            }
        }
    }
}