package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.*
import sx.rs.proxy.RestClient
import sx.rs.proxy.RestEasyClient
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

        return RestEasyClient(baseUri, ignoreSsl)
    }

    companion object {
        val module = Kodein.Module {
            /** Rest configuration */
            bind<RestClientFactory>() with eagerSingleton {
                RestClientFactory().also {
                    it.host = "leoz.derkurier.de"
                    it.https = true
                }
            }

            /** Rest client */
            bind<RestClient>() with provider {
                instance<RestClientFactory>().create()
            }
        }
    }
}