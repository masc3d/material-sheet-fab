package org.deku.leoz.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.singleton
import feign.Feign
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.jaxrs.JAXRSContract
import org.deku.leoz.rest.service.internal.v1.StationService

/**
 * Created by n3 on 10/12/2016.
 */
class RestConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<RestConfiguration>() with singleton {
                RestConfiguration()
            }

            bind<Feign.Builder>() with singleton {
                Feign.builder()
                        .encoder(JacksonEncoder())
                        .decoder(JacksonDecoder())
                        .contract(JAXRSContract())
            }

            bind<StationService>() with provider {
                val builder: Feign.Builder = instance()
                val config: RestConfiguration = instance()
                builder.target(StationService::class.java, config.url)
            }
        }
    }

    var url: String = "https://leoz.derkurier.de:13000/rs/api"
}