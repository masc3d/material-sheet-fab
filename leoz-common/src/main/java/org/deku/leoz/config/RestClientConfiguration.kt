package org.deku.leoz.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.provider
import org.deku.leoz.service.internal.*
import sx.rs.proxy.RestClientProxy
import java.net.URI

/**
 * Base class for JAX/RS REST client configurations
 * Created by masc on 07/11/2016.
 */
abstract class RestClientConfiguration {
    /**
     * Overridden in derived configurations to provide a specific proxy client
     */
    abstract fun createClientProxy(baseUri: URI, ignoreSsl: Boolean): RestClientProxy

    /**
     * HTTP host to use for rest clients
     */
    var host: String = "localhost"

    /**
     * Connect via HTTPS
     */
    var https: Boolean = false

    /**
     * HTTP/S port
     */
    var port: Int = RestConfiguration.DEFAULT_PORT

    fun createUri(https: Boolean, host: String, port: Int, basePath: String): URI {
        val scheme = when (https) {
            true -> "https"
            false -> "http"
        }

        return URI("${scheme}://${host}:${port}")
                .resolve(basePath)
    }

    /**
     * Factory method for creating a leoz rest client
     */
    fun createDefaultClientProxy(): RestClientProxy {
        val uri = this.createUri(https, host, port, RestConfiguration.MAPPING_PREFIX)

        // Ignore SSL certificate for (usually testing/dev) remote hosts which are not in the business domain
        val ignoreSslCertificate = !host.endsWith(org.deku.leoz.config.HostConfiguration.Companion.CENTRAL_DOMAIN)

        return this.createClientProxy(uri, ignoreSslCertificate)
    }

    companion object {
        val module = Kodein.Module {
            /**
             * Helper for creating service proxy
             */
            fun <T> createServiceProxy(config: RestClientConfiguration, serviceType: Class<T>): T {
                return config.createDefaultClientProxy().create(serviceType)
            }

            bind<StationService>() with provider {
                createServiceProxy(config = instance(), serviceType = StationService::class.java)
            }

            bind<BundleServiceV2>() with provider {
                createServiceProxy(config = instance(), serviceType = BundleServiceV2::class.java)
            }

            bind<DeliveryListService>() with provider {
                 createServiceProxy(config = instance(), serviceType = DeliveryListService::class.java)
            }

            bind<OrderService>() with provider {
                createServiceProxy(config = instance(), serviceType = OrderService::class.java)
            }

            bind<UserService>() with provider {
                createServiceProxy(config = instance(), serviceType = UserService::class.java)
            }

            bind<AuthorizationService>() with provider {
                createServiceProxy(config = instance(), serviceType = AuthorizationService::class.java)
            }

            bind<LocationServiceV2>() with provider {
                createServiceProxy(config = instance(), serviceType = LocationServiceV2::class.java)
            }
        }
    }
}