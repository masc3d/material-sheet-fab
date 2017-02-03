package org.deku.leoz.config

import com.github.salomonbrys.kodein.*
import feign.Client
import feign.Feign
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.jaxrs.JAXRSContract
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.deku.leoz.rest.service.internal.v1.StationService
import sx.LazyInstance
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
    private val clientWithoutSslValidation: Client = Client.Default(
            TrustingSSLSocketFactory.get(),
            object : HostnameVerifier {
                override fun verify(s: String, sslSession: SSLSession): Boolean {
                    return true
                }
            })

    /**
     * Feign builder
     */
    private val builder = LazyInstance<Feign.Builder>({
        Feign.builder()
                .client(if (sslValidation) Client.Default(null, null) else clientWithoutSslValidation)
                .encoder(JacksonEncoder())
                .decoder(JacksonDecoder())
                .contract(JAXRSContract())
    })

    /**
     * REST base url
     */
    var url: String = "https://leoz.derkurier.de:13000/rs/api"

    /**
     * Toggle SSL validation
     */
    var sslValidation = true
        set(value) {
            field = value
            this.builder.reset()
        }

    companion object {
        /**
         * Injection module
         */
        val module = Kodein.Module {
            bind<RestFeignClientConfiguration>() with singleton {
                RestFeignClientConfiguration()
            }

            bind<StationService>() with provider {
                val config: RestFeignClientConfiguration = instance()
                config.builder.get().target(StationService::class.java, config.url)
            }

            bind<BundleService>() with provider {
                val config: RestFeignClientConfiguration = instance()
                config.builder.get().target(BundleService::class.java, config.url)
            }
        }
    }
}