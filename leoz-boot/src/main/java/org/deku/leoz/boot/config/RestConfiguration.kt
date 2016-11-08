package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
import org.deku.leoz.boot.Settings
import org.deku.leoz.rest.RestClient

/**
 * Created by masc on 08/11/2016.
 */
class RestConfiguration : org.deku.leoz.config.RestConfiguration() {
    companion object {
        val module = Kodein.Module {
            /** Rest configuration */
            bind<RestConfiguration>() with singleton {
                RestConfiguration()
            }

            /** Rest client */
            bind<RestClient>() with provider {
                val config: RestConfiguration = instance()
                val settings: Settings = instance()

                config.createClient("leoz.derkurier.de", DEFAULT_PORT, true)
            }
        }
    }
}