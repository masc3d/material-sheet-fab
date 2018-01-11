package org.deku.leoz.config

import com.github.salomonbrys.kodein.*
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.deku.leoz.rest.RestClientFactory
import sx.rs.client.FeignClient
import sx.rs.client.RestClient
import sx.rs.client.RestEasyClient
import java.net.URI

/**
 * Rest client test configuration
 * Created by masc on 16/03/2017.
 */
class RestClientTestConfiguration {

    class FeignClientFactory : RestClientFactory() {
        override fun create(baseUri: URI, ignoreSsl: Boolean, apiKey: String?): RestClient {
            return FeignClient(
                    baseUri = baseUri,
                    ignoreSslCertificate = ignoreSsl,
                    headers = apiKey?.let { mapOf(Rest.API_KEY to apiKey) },
                    encoder = JacksonEncoder(),
                    decoder = JacksonDecoder())
        }
    }

    class RestEasyClientFactory : RestClientFactory() {
        override fun create(baseUri: URI, ignoreSsl: Boolean, apiKey: String?): RestClient {
            return RestEasyClient(
                    baseUri = baseUri,
                    ignoreSslCertificate = ignoreSsl
            )
        }
    }

    companion object {
        val module = Kodein.Module {
            import(RestClientConfiguration.module)

            bind<FeignClientFactory>() with singleton {
                FeignClientFactory()
            }

            bind<RestEasyClientFactory>() with singleton() {
                RestEasyClientFactory()
            }

            bind<FeignClient>() with provider {
                instance<FeignClientFactory>().create() as FeignClient
            }

            bind<RestEasyClient>() with provider {
                instance<RestEasyClientFactory>().create() as RestEasyClient
            }

            // Default rest client factory
            bind<RestClientFactory>() with provider {
                instance<FeignClientFactory>()
            }

            bind<RestClient>() with provider {
                instance<FeignClient>()
            }
        }
    }
}