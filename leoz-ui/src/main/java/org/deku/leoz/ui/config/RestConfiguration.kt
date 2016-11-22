package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import org.deku.leoz.rest.RestClient
import org.deku.leoz.rest.service.internal.v1.BundleService

/**
 * Rest configuration
 * Created by masc on 08/11/2016.
 */
class RestConfiguration : org.deku.leoz.config.RestConfiguration() {
    companion object {
        val module = Kodein.Module {
            /** Rest configuration */
            bind<RestConfiguration>() with eagerSingleton {
                RestConfiguration()
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