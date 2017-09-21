package org.deku.leoz.mobile.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.erased.*
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.deku.leoz.mobile.RemoteSettings
import org.slf4j.LoggerFactory
import sx.rs.proxy.FeignClientProxy
import sx.rs.proxy.RestClientProxy
import java.net.URI

/**
 * Mobile REST client confuguration
 * Created by n3 on 15/02/2017.
 */
class RestClientConfiguration : org.deku.leoz.config.RestClientConfiguration() {
    override fun createClientProxy(baseUri: URI, ignoreSsl: Boolean): RestClientProxy =
            FeignClientProxy(baseUri, ignoreSsl, JacksonEncoder(), JacksonDecoder())

    companion object {
        private val log = LoggerFactory.getLogger(RestClientConfiguration::class.java)

        val module = Kodein.Module {
            import(org.deku.leoz.config.RestClientConfiguration.module)

            bind<org.deku.leoz.config.RestClientConfiguration>() with eagerSingleton {
                RestClientConfiguration()
            }

            bind<FeignClientProxy>() with provider {
                val config: org.deku.leoz.config.RestClientConfiguration = instance()
                config.createDefaultClientProxy() as FeignClientProxy
            }

            onReady {
                val remoteSettings: RemoteSettings = instance()
                val restConfiguration: org.deku.leoz.config.RestClientConfiguration = instance()

                restConfiguration.host = remoteSettings.host
                restConfiguration.https = remoteSettings.http.ssl
                restConfiguration.port = remoteSettings.http.port
            }
        }
    }
}