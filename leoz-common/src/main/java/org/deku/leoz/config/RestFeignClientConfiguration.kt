package org.deku.leoz.config

import com.github.salomonbrys.kodein.*
import feign.Feign
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.jaxrs.JAXRSContract
import org.deku.leoz.rest.service.internal.v1.StationService

/**
 * Created by n3 on 10/12/2016.
 */
class RestFeignClientConfiguration {
    companion object {
        val module = Kodein.Module {
            bind<RestFeignClientConfiguration>() with singleton {
                RestFeignClientConfiguration()
            }

            bind<Feign.Builder>() with singleton {
                Feign.builder()
                        .encoder(JacksonEncoder())
                        .decoder(JacksonDecoder())
                        .contract(JAXRSContract())
            }

            bind<StationService>() with provider {
                val builder: Feign.Builder = instance()
                val config: RestFeignClientConfiguration = instance()
                builder.target(StationService::class.java, config.url)
            }
        }
    }

    var url: String = "https://leoz.derkurier.de:13000/rs/api"
}