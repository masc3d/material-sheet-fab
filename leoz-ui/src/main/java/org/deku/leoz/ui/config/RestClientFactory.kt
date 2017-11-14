package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.config.Rest
import sx.rs.proxy.RestClient
import org.deku.leoz.service.internal.BundleServiceV2
import org.deku.leoz.service.internal.StationService
import org.slf4j.LoggerFactory
import sx.rs.proxy.JerseyClient
import java.net.URI

/**
 * Rest configuration
 * Created by masc on 08/11/2016.
 */
class RestClientFactory : org.deku.leoz.config.RestClientFactory() {
    override fun create(
            baseUri: URI,
            ignoreSsl: Boolean,
            apiKey: String?): RestClient {

        return JerseyClient(baseUri, ignoreSsl)
    }

    init {
        this.host = "localhost"
        this.https = false
        this.port = Rest.DEFAULT_PORT
    }

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