package org.deku.leoz.config

import com.github.salomonbrys.kodein.*
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import sx.rs.proxy.FeignClientProxy
import sx.rs.proxy.RestClientProxy
import java.net.URI

/**
 * Created by masc on 16/03/2017.
 */
class RestClientTestConfiguration : org.deku.leoz.config.RestClientConfiguration() {
    override fun createClientProxy(baseUri: URI, ignoreSsl: Boolean): RestClientProxy {
        return FeignClientProxy(baseUri, ignoreSsl, JacksonEncoder(), JacksonDecoder())
    }

    companion object {
        val module = Kodein.Module {
            import(org.deku.leoz.config.RestClientConfiguration.module)

            bind<FeignClientProxy>() with provider {
                instance<RestClientConfiguration>().createDefaultClientProxy() as FeignClientProxy
            }

            bind<org.deku.leoz.config.RestClientConfiguration>() with singleton {
                RestClientTestConfiguration()
            }
        }
    }
}