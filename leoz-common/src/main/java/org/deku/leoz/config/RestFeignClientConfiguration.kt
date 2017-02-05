package org.deku.leoz.config

import com.github.salomonbrys.kodein.*
import feign.Client
import feign.Feign
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.jaxrs.JAXRSContract
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.deku.leoz.rest.service.internal.v1.StationService
import sx.net.TrustingSSLSocketFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 * Feign REST client configuration
 * Created by n3 on 10/12/2016.
 */
class RestFeignClientConfiguration {
    /**
     * A client without https cerfificate validation
     */
    private val clientWithoutSslValidation: Client by lazy {
        Client.Default(
                TrustingSSLSocketFactory.get(),
                object : HostnameVerifier {
                    override fun verify(s: String, sslSession: SSLSession): Boolean {
                        return true
                    }
                })
    }

    private val client: Client by lazy { Client.Default(null, null) }

    /**
     * REST base url
     */
    var url: String = "https://leoz.derkurier.de:13000/rs/api"

    /**
     * Toggle SSL validation
     */
    var sslValidation = true

    companion object {
        /**
         * Injection module
         */
        val module = Kodein.Module {
            bind<RestFeignClientConfiguration>() with singleton {
                RestFeignClientConfiguration()
            }

            bind<Feign.Builder>() with provider {
                val config: RestFeignClientConfiguration = instance()
                Feign.builder()
                        .client(if (config.sslValidation) config.client else config.clientWithoutSslValidation)
                        .encoder(JacksonEncoder())
                        .decoder(JacksonDecoder())
                        .contract(JAXRSContract())
            }

            bind<StationService>() with provider {
                val config: RestFeignClientConfiguration = instance()
                val builder: Feign.Builder = instance()
                builder.target(StationService::class.java, config.url)
            }

            bind<BundleService>() with provider {
                val config: RestFeignClientConfiguration = instance()
                val builder: Feign.Builder = instance()
                builder.target(BundleService::class.java, config.url)
            }
        }
    }
}