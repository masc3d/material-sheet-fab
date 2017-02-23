package org.deku.leoz.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.singleton
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.github.salomonbrys.kodein.bind
import feign.Client
import feign.Feign
import feign.Request
import feign.Retryer
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.jaxrs.JAXRSContract
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.deku.leoz.rest.service.internal.v1.StationService
import sx.net.TrustingSSLSocketFactory
import java.io.OutputStream
import java.lang.reflect.Type
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 * Feign REST client configuration
 * Created by n3 on 10/12/2016.
 */
class FeignRestClientConfiguration {
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

    /**
     * Default feign client
     */
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
         * Convenience extension method for creating feign target with streaming support
         */
        fun <T> Feign.Builder.target(
                apiType: Class<T>,
                output: OutputStream,
                progressCallback: ((p: Float, bytesCopied: Long) -> Unit)? = null): T {

            val config: FeignRestClientConfiguration = Kodein.global.instance()

            return this.decoder(
                    sx.feign.StreamDecoder(
                            fallbackDecoder = JacksonDecoder(),
                            output = output,
                            progressCallback = progressCallback
                    ))
                    .target(apiType, config.url)
        }

        /**
         * Injection module
         */
        val module = Kodein.Module {
            /**
             * Helper for creating service proxy
             */
            fun <T> createServiceProxy(config: FeignRestClientConfiguration, builder: Feign.Builder, serviceType: Class<T>): T {
                return builder.target(serviceType, config.url)
            }

            bind<FeignRestClientConfiguration>() with singleton {
                FeignRestClientConfiguration()
            }

            bind<Feign.Builder>() with provider {
                val config: FeignRestClientConfiguration = instance()
                Feign.builder()
                        .client(if (config.sslValidation) config.client else config.clientWithoutSslValidation)
                        .encoder(JacksonEncoder())
                        .decoder(JacksonDecoder())
                        .retryer(Retryer.NEVER_RETRY)
                        .options(Request.Options(5000, 10000))
                        .contract(JAXRSContract())
            }

            bind<StationService>() with provider {
                createServiceProxy(config = instance(), builder = instance(), serviceType = StationService::class.java)
            }

            bind<BundleService>() with provider {
                createServiceProxy(config = instance(), builder = instance(), serviceType = BundleService::class.java)
            }
        }
    }
}