package org.deku.leoz.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.bind
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.provider
import org.deku.leoz.service.internal.v1.BundleService
import org.deku.leoz.service.internal.v1.StationService
import org.deku.leoz.service.internal.v1.UserService
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

        val uri = URI("${scheme}://${host}:${port}")
                .resolve(RestConfiguration.MAPPING_PREFIX)

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

            bind<BundleService>() with provider {
                createServiceProxy(config = instance(), serviceType = BundleService::class.java)
            }

            bind<UserService>() with provider {
                createServiceProxy(config = instance(), serviceType = UserService::class.java)
            }
        }
    }
}