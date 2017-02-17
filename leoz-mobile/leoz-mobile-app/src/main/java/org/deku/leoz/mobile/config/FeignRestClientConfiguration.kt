package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import org.slf4j.LoggerFactory
import sx.maps.mapValue

/**
 * Created by n3 on 15/02/2017.
 */
class FeignRestClientConfiguration {
    /**
     * REST settings
     */
    class Settings(map: Map<String, Any> = mapOf()) {
        val url: String by mapValue(map, "https://leoz.derkurier.de:13000/rs/api")
        val sslValidation: Boolean by mapValue(map, true)
    }

    companion object {
        private val log = LoggerFactory.getLogger(FeignRestClientConfiguration::class.java)

        val module = Kodein.Module {
            import(org.deku.leoz.config.FeignRestClientConfiguration.module)

            bind<Settings>() with singleton {
                val rootSettings: org.deku.leoz.mobile.Settings = instance()
                Settings(rootSettings.resolve("rest"))
            }

            onReady {
                val settings: Settings = instance()
                val feignRestConfiguration: org.deku.leoz.config.FeignRestClientConfiguration = instance()

                feignRestConfiguration.url = settings.url
                feignRestConfiguration.sslValidation = settings.sslValidation
            }
        }
    }
}