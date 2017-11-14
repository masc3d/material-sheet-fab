package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import org.deku.leoz.service.internal.BundleServiceV2
import org.deku.leoz.service.internal.StationService
import org.deku.leoz.ui.RestClientFactory
import org.slf4j.LoggerFactory
import sx.rs.proxy.RestClient

/**
 * Created by masc on 14.11.17.
 */
class RestClientConfiguration {
    companion object {
        val log = LoggerFactory.getLogger(RestClientFactory::class.java)

        val module = Kodein.Module {
            /** Rest configuration */
            bind<RestClientFactory>() with eagerSingleton {
                RestClientFactory()
            }

            /** Rest client */
            bind<RestClient>() with provider {
                instance<RestClientFactory>().create()
            }

            /** Bundle service */
            bind<BundleServiceV2>() with provider {
                instance<RestClient>().proxy(BundleServiceV2::class.java)
            }

            /** Station service */
            bind<StationService>() with provider {
                instance<RestClient>().proxy(StationService::class.java)
            }
        }
    }
}