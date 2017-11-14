package org.deku.leoz.config

import com.github.salomonbrys.kodein.*
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.deku.leoz.rest.RestClientFactory
import sx.rs.client.FeignClient
import sx.rs.client.RestClient
import java.net.URI

/**
 * Rest client test configuration
 * Created by masc on 16/03/2017.
 */
class RestClientTestFactory : RestClientFactory() {
    override fun create(baseUri: URI, ignoreSsl: Boolean, apiKey: String?): RestClient {
        return FeignClient(
                baseUri = baseUri,
                ignoreSslCertificate = ignoreSsl,
                headers = apiKey?.let { mapOf(Rest.API_KEY to apiKey) },
                encoder = JacksonEncoder(),
                decoder = JacksonDecoder())
    }

    companion object {
        val module = Kodein.Module {
            import(RestClientConfiguration.module)

            bind<FeignClient>() with provider {
                instance<RestClientFactory>().create() as FeignClient
            }

            bind<RestClientFactory>() with singleton {
                RestClientTestFactory()
            }
        }
    }
}