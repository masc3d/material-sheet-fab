package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.erased.*
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.slf4j.LoggerFactory
import sx.ConfigurationMap
import sx.ConfigurationMapPath
import sx.rs.proxy.FeignClientProxy
import sx.rs.proxy.RestClientProxy
import java.net.URI

/**
 * Created by n3 on 15/02/2017.
 */
class RestClientConfiguration : org.deku.leoz.config.RestClientConfiguration() {
    override fun createClientProxyImpl(baseUri: URI, ignoreSsl: Boolean): RestClientProxy {
        return FeignClientProxy(baseUri, ignoreSsl, JacksonEncoder(), JacksonDecoder())
    }

    /**
     * REST settings
     */
    @ConfigurationMapPath("rest")
    class Settings(map: ConfigurationMap) {
        val url: String by map.value("https://leoz.derkurier.de:13000/rs/api")
        val sslValidation: Boolean by map.value(true)
    }

    companion object {
        private val log = LoggerFactory.getLogger(RestClientConfiguration::class.java)

        val module = Kodein.Module {
            import(org.deku.leoz.config.RestClientConfiguration.module)

            bind<org.deku.leoz.config.RestClientConfiguration>() with eagerSingleton {
                RestClientConfiguration()
            }

            bind<Settings>() with singleton {
                Settings(map = instance())
            }

            bind<FeignClientProxy>() with provider {
                val config: RestClientConfiguration = instance()
                config.createClientProxy() as FeignClientProxy
            }

            onReady {
                val settings: Settings = instance()
                val restConfiguration: org.deku.leoz.config.RestClientConfiguration = instance()

                val uri = URI.create(settings.url)
                restConfiguration.host = uri.host
                restConfiguration.https = uri.scheme.equals("https", true)
                restConfiguration.port = uri.port
            }
        }
    }
}