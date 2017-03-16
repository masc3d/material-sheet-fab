package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.config.RestConfiguration
import sx.rs.proxy.RestClientProxy
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.deku.leoz.rest.service.internal.v1.StationService
import org.deku.leoz.service.discovery.DiscoveryInfo
import org.deku.leoz.service.discovery.DiscoveryService
import org.slf4j.LoggerFactory
import sx.rs.proxy.JerseyClientProxy
import java.net.URI

/**
 * Rest configuration
 * Created by masc on 08/11/2016.
 */
class RestClientConfiguration : org.deku.leoz.config.RestClientConfiguration() {
    override fun createClientProxyImpl(baseUri: URI, ignoreSsl: Boolean): RestClientProxy {
        return JerseyClientProxy(baseUri, ignoreSsl)
    }

    companion object {
        val log = LoggerFactory.getLogger(RestClientConfiguration::class.java)

        val module = Kodein.Module {
            /** Rest configuration */
            bind<RestClientConfiguration>() with eagerSingleton {
                RestClientConfiguration()
            }

            /** Rest client */
            bind<RestClientProxy>() with provider {
                val config: RestClientConfiguration = instance()
                config.createClientProxy(config.httpHost, RestConfiguration.DEFAULT_PORT, config.https)
            }

            /** Bundle service */
            bind<BundleService>() with provider {
                val restClient: RestClientProxy = instance()
                restClient.proxy(BundleService::class.java)
            }

            /** Station service */
            bind<StationService>() with provider {
                val restClient: RestClientProxy = instance()
                restClient.proxy(StationService::class.java)
            }
        }
    }

    /**
     * HTTP host to use for rest clients
     */
    var httpHost: String= "localhost"

    /**
     * Connect via HTTPS
     */
    var https: Boolean = false
}