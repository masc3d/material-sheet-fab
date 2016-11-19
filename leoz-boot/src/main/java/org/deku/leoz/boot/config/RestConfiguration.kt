package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.global
import org.deku.leoz.boot.Settings
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.config.HostConfiguration
import org.deku.leoz.rest.RestClient
import org.deku.leoz.service.discovery.DiscoveryService
import rx.Observable
import rx.lang.kotlin.cast
import java.time.Duration

/**
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
        }
    }

    /**
     * HTTP host to use for rest clients
     */
    var httpHost: String = HostConfiguration.CENTRAL_HOST

    /**
     * Connect via HTTPS
     */
    var https: Boolean = true
}