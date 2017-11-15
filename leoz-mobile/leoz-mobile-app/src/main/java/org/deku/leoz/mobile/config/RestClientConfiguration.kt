package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.erased.provider
import org.deku.leoz.config.RestClientConfiguration
import org.deku.leoz.mobile.rest.RestClientFactory
import org.deku.leoz.mobile.settings.RemoteSettings
import org.slf4j.LoggerFactory
import sx.rs.client.FeignClient

/**
 * Rest client configuration
 * Created by masc on 14.11.17.
 */
class RestClientConfiguration {
    companion object {
        private val log = LoggerFactory.getLogger(RestClientFactory::class.java)

        val module = Kodein.Module {
            import(RestClientConfiguration.module)

            bind<RestClientFactory>() with eagerSingleton {
                RestClientFactory()
            }

            bind<FeignClient>() with provider {
                instance<RestClientFactory>().create() as FeignClient
            }

            onReady {
                val remoteSettings: RemoteSettings = instance()
                val restConfiguration: RestClientFactory = instance()

                restConfiguration.host = remoteSettings.host
                restConfiguration.https = remoteSettings.http.ssl
                restConfiguration.port = remoteSettings.http.port
            }
        }
    }
}