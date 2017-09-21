package org.deku.leoz.ui.config

import com.github.salomonbrys.kodein.*
import org.deku.leoz.config.Rest
import sx.rs.proxy.RestClientProxy
import org.deku.leoz.service.internal.BundleServiceV2
import org.deku.leoz.service.internal.StationService
import org.slf4j.LoggerFactory
import sx.rs.proxy.JerseyClientProxy
import java.net.URI

/**
 * Rest configuration
 * Created by masc on 08/11/2016.
 */
class RestClientConfiguration : org.deku.leoz.config.RestClientConfiguration() {
    override fun createClientProxy(baseUri: URI, ignoreSsl: Boolean): RestClientProxy {
        return JerseyClientProxy(baseUri, ignoreSsl)
    }

    init {
        this.host = "localhost"
        this.https = false
        this.port = Rest.DEFAULT_PORT
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
                config.createDefaultClientProxy()
            }

            /** Bundle service */
            bind<BundleServiceV2>() with provider {
                val restClient: RestClientProxy = instance()
                restClient.create(BundleServiceV2::class.java)
            }

            /** Station service */
            bind<StationService>() with provider {
                val restClient: RestClientProxy = instance()
                restClient.create(StationService::class.java)
            }
        }
    }
}