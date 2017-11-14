package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import org.deku.leoz.boot.RestClientFactory
import sx.rs.proxy.RestClient

/**
 * Created by masc on 14.11.17.
 */
class RestClientConfiguration {
    companion object {
        val module = Kodein.Module {
            /** Rest configuration */
            bind<RestClientFactory>() with eagerSingleton {
                RestClientFactory().also {
                    it.host = "leoz.derkurier.de"
                    it.https = true
                }
            }

            /** Rest client provider */
            bind<RestClient>() with provider {
                instance<RestClientFactory>().create()
            }
        }
    }
}