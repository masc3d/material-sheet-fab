package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import org.deku.leoz.config.RestConfiguration
import org.deku.leoz.rest.RestClient
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.deku.leoz.rest.service.internal.v1.StationService
import org.deku.leoz.service.discovery.DiscoveryInfo
import org.deku.leoz.service.discovery.DiscoveryService
import org.slf4j.LoggerFactory

/**
 * Rest configuration
 * Created by masc on 08/11/2016.
 */
class RestClientConfiguration : org.deku.leoz.config.RestClientConfiguration() {
    companion object {
        val log = LoggerFactory.getLogger(RestClientConfiguration::class.java)

        val module = Kodein.Module {
            /** Rest configuration */
            bind<RestClientConfiguration>() with eagerSingleton {
                RestClientConfiguration()
            }

            /** Rest client */
            bind<RestClient>() with provider {
                val config: RestClientConfiguration = instance()
                config.createClient(config.httpHost, RestConfiguration.DEFAULT_PORT, config.https)
            }

            /** Bundle service */
            bind<BundleService>() with provider {
                val restClient: RestClient = instance()
                restClient.proxy(BundleService::class.java)
            }

            /** Station service */
            bind<StationService>() with provider {
                val restClient: RestClient = instance()
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