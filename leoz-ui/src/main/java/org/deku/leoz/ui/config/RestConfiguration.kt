package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
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
class RestConfiguration : org.deku.leoz.config.RestConfiguration() {
    companion object {
        val log = LoggerFactory.getLogger(RestConfiguration::class.java)

        val module = Kodein.Module {
            /** Rest configuration */
            bind<RestConfiguration>() with eagerSingleton {
                val config = RestConfiguration()

                val discoveryService: DiscoveryService = instance()
                discoveryService.updatedEvent.subscribe {
                    data class Result(val host: String, val service: DiscoveryInfo.Service)
                    val result = discoveryService.directory
                            .mapNotNull {
                                val service = it.info?.services?.firstOrNull { it.type == DiscoveryInfo.ServiceType.HTTP }
                                if (service == null) return@mapNotNull null

                                Result(host = it.address.hostName, service = service)
                            }
                            .firstOrNull()

                    if (result != null) {
                        config.httpHost = result.host
                        config.https = false
                        log.info("Updated REST host to ${config.httpHost}")
                    }
                }
                return@eagerSingleton config
            }

            /** Rest client */
            bind<RestClient>() with provider {
                val config: RestConfiguration = instance()
                config.createClient(config.httpHost, DEFAULT_PORT, config.https)
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
    var httpHost: String = "localhost"

    /**
     * Connect via HTTPS
     */
    var https: Boolean = false
}