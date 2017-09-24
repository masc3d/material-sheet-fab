package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.*
import sx.rs.proxy.RestClientProxy
import sx.rs.proxy.RestEasyClientProxy
import java.net.URI

/**
 * Rest configuration
 * Created by masc on 08/11/2016.
 */
class RestClientConfiguration : org.deku.leoz.config.RestClientConfiguration() {
    override fun createClientProxy(baseUri: URI, ignoreSsl: Boolean, apiKey: String?): RestClientProxy {
        return RestEasyClientProxy(baseUri, ignoreSsl)
    }

    companion object {
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
        }
    }
}