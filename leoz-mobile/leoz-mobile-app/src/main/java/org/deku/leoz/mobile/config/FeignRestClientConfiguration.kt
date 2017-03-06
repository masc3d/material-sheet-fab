package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.erased.*
import org.slf4j.LoggerFactory
import sx.ConfigurationMap
import sx.ConfigurationMapPath

/**
 * Created by n3 on 15/02/2017.
 */
class FeignRestClientConfiguration {
    /**
     * REST settings
     */
    @ConfigurationMapPath("rest")
    class Settings(map: ConfigurationMap) {
        val url: String by map.value("https://leoz.derkurier.de:13000/rs/api")
        val sslValidation: Boolean by map.value(true)
    }

    companion object {
        private val log = LoggerFactory.getLogger(FeignRestClientConfiguration::class.java)

        val module = Kodein.Module {
            import(org.deku.leoz.config.FeignRestClientConfiguration.module)

            bind<Settings>() with singleton {
                Settings(instance())
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