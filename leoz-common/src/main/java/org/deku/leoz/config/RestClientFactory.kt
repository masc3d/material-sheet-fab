package org.deku.leoz.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.provider
import org.deku.leoz.service.internal.*
import sx.rs.proxy.RestClient
import java.net.URI

/**
 * Base class for JAX/RS REST client configurations
 * Created by masc on 07/11/2016.
 */
abstract class RestClientFactory {
    /**
     * Overridden in derived configurations to provide a specific proxy client
     */
    abstract fun create(baseUri: URI, ignoreSsl: Boolean, apiKey: String? = null): RestClient

    /**
     * HTTP host to use for rest clients
     */
    var host: String = "localhost"

    /**
     * Connect via HTTPS
     */
    var https: Boolean = false

    /**
     * Header API key
     */
    var apiKey: String? = null

    /**
     * HTTP/S port
     */
    var port: Int = Rest.DEFAULT_PORT

    fun createUri(https: Boolean, host: String, port: Int, basePath: String = Rest.MAPPING_PREFIX): URI {
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
    fun create(uri: URI): RestClient {
        // Ignore SSL certificate for (usually testing/dev) remote hosts which are not in the business domain
        val ignoreSslCertificate = !uri.host.endsWith(org.deku.leoz.config.HostConfiguration.Companion.CENTRAL_DOMAIN)

        return this.create(uri, ignoreSslCertificate, this.apiKey)
    }

    /**
     * Factory method for creating a default leoz rest client
     * using the configurations remote settings (host/port)
     */
    fun create(): RestClient {
        val uri = this.createUri(https, host, port)

        return this.create(uri)
    }

    companion object {
        val module = Kodein.Module {
            /**
             * Helper for creating service proxy
             */
            fun <T> createProxy(clientFactory: RestClientFactory, serviceType: Class<T>): T =
                    clientFactory.create().proxy(serviceType)

            bind<StationService>() with provider {
                createProxy(clientFactory = instance(), serviceType = StationService::class.java)
            }

            bind<BundleServiceV2>() with provider {
                createProxy(clientFactory = instance(), serviceType = BundleServiceV2::class.java)
            }

            bind<DeliveryListService>() with provider {
                createProxy(clientFactory = instance(), serviceType = DeliveryListService::class.java)
            }

            bind<OrderService>() with provider {
                createProxy(clientFactory = instance(), serviceType = OrderService::class.java)
            }

            bind<UserService>() with provider {
                createProxy(clientFactory = instance(), serviceType = UserService::class.java)
            }

            bind<AuthorizationService>() with provider {
                createProxy(clientFactory = instance(), serviceType = AuthorizationService::class.java)
            }

            bind<LocationServiceV2>() with provider {
                createProxy(clientFactory = instance(), serviceType = LocationServiceV2::class.java)
            }
        }
    }
}