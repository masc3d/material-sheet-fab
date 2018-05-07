package sx.rs.client

import com.fasterxml.jackson.databind.ObjectMapper
import feign.Client
import feign.Feign
import feign.Request
import feign.Retryer
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.jaxrs2.JAXRS2Contract
import feign.okhttp.OkHttpClient
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.client.JerseyClientBuilder
import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import sx.net.TrustingSSLSocketFactory
import sx.rs.LoggingFilter
import java.io.OutputStream
import java.net.URI
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*
import javax.ws.rs.client.ClientRequestContext
import javax.ws.rs.client.ClientRequestFilter
import javax.ws.rs.core.HttpHeaders

/**
 * Ignoring X509 trust manager, typically used when disabling SSL certificate checks
 */
private class IgnoringX509TrustManager : X509TrustManager {
    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf<X509Certificate>()
}

/**
 * REST client proxy base clas
 *
 * Reasoning behind this abstraction:
 * * Jersey/grizzly is generally more easily to embedd as a light-weight REST server (as used in `leoz-ui`/bridge)
 * *  RESTeasy performs better under load, especially with undertow/netty, thus it's the currently the better choice for `leoz-node`
 * * RESTeasy client cannot be used when jersey is on the classpath (as javax.ws.rs factories loads whatever it finds first, which will cause cast issues)
 * @param baseUri Optional base URI (can also be set via property)
 * @property ignoreSslCertificate Ignore SSL certificates
 * Created by masc on 06/11/2016.
 */
abstract class RestClient(
        var baseUri: URI,
        val ignoreSslCertificate: Boolean = false) {

    protected val connectTimeout = Duration.ofSeconds(5)
    protected val readTimeout = Duration.ofSeconds(15)

    protected val ignoringCertificateSslContext by lazy {
        val ignoringCertificateSslContext = SSLContext.getInstance("TLS")
        ignoringCertificateSslContext.init(null, arrayOf<TrustManager>(IgnoringX509TrustManager()), SecureRandom())
        ignoringCertificateSslContext
    }

    abstract fun <T> proxy(serviceClass: Class<T>): T
}

/**
 * Jersey client proxy implementation
 */
class JerseyClient(
        baseUri: URI,
        ignoreSslCertificate: Boolean = false) : RestClient(baseUri, ignoreSslCertificate) {

    private val client by lazy {
        var clientBuilder = JerseyClientBuilder()
                .property(ClientProperties.CONNECT_TIMEOUT, connectTimeout.toMillis().toInt())
                .property(ClientProperties.READ_TIMEOUT, readTimeout.toMillis().toInt())

        if (ignoreSslCertificate) {
            clientBuilder = clientBuilder
                    .sslContext(ignoringCertificateSslContext)
                    .hostnameVerifier { _, _ -> true }
        }
        clientBuilder.build()
    }


    override fun <T> proxy(serviceClass: Class<T>): T {
        val webTarget = this.client.target(baseUri)
        return WebResourceFactory.newResource(serviceClass, webTarget)
    }
}

/**
 * RESTEasy client proxy implementation
 *
 * @param baseUri The base uri for all targets
 * @param objectMapper Jackson object mapper
 * @param ignoreSslCertificate Ignore ssl certificate (useful for test)
 */
class RestEasyClient(
        baseUri: URI,
        objectMapper: ObjectMapper = ObjectMapper(),
        connectionPoolSize: Int = 0,
        ignoreSslCertificate: Boolean = false
) :
        RestClient(baseUri, ignoreSslCertificate) {

    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    /** JWT token to include in header */
    var jwtToken: String? = null

    private val client by lazy {
        ResteasyClientBuilder()
                .connectionPoolSize(connectionPoolSize)
                .connectTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .also {
                    if (ignoreSslCertificate) {
                        it
                                .sslContext(ignoringCertificateSslContext)
                                .hostnameVerifier { _, _ -> true }
                    }
                }
                // masc20171116. it's mandatory to derive from ResteasyJackson2Provider so it's recognized as an override
                .register(object : ResteasyJackson2Provider() {}.also {
                    it.setMapper(objectMapper)
                })
                .register(object : ClientRequestFilter {
                    override fun filter(requestContext: ClientRequestContext) {
                        if (this@RestEasyClient.jwtToken != null)
                            requestContext.headers.add(HttpHeaders.AUTHORIZATION, "JWT ${this@RestEasyClient.jwtToken}")
                    }
                })
                .register(LoggingFilter())
                .build()
    }

    /**
     * Create service proxy
     */
    override fun <T> proxy(serviceClass: Class<T>): T {
        return this.proxy(
                serviceClass = serviceClass,
                path = null)
    }

    fun createUri(path: String? = null): URI =
            if (path != null)
                this.baseUri.resolve(path)
            else
                this.baseUri

    /**
     * Create target for consumers which require connection pooling on customized requests
     *
     * @param path Optional path override
     * @param jwtToken Optional jwt token override
     */
    private fun target(path: String? = null, jwtToken: () -> String? = { null }): ResteasyWebTarget {
        val webTarget: ClientWebTarget = this.client.target(
                this.createUri(path)
        ) as ClientWebTarget

        return webTarget
                .register(object : ClientRequestFilter {
                    override fun filter(requestContext: ClientRequestContext) {
                        jwtToken()?.also {
                            requestContext.headers.add("Authorization", "JWT ${it}")
                        }
                    }
                })
    }

    /**
     * Create proxy for consumers which require connection pooling on customized requests
     *
     * @param serviceClass Service class
     * @param path Optional path override
     * @param jwtToken Optional jwt token override
     */
    fun <T> proxy(serviceClass: Class<T>, path: String? = null, jwtToken: () -> String? = { null }): T {
        return this.target(
                path = path,
                jwtToken = jwtToken
        )
                .proxyBuilder(serviceClass)
                // return webTarget.proxy(serviceClass)
                // masc20170329. for compatibility with spring-boot-devtools restart, need to use the serviceclass' classloader
                // to avoid `IllegalArgumentException` `is not visible from class loader`
                .classloader(serviceClass.classLoader)
                .build()
    }
}

/**
 * Feign client proxy implementation
 *
 * @param baseUri The base uri for all targets
 * @param ignoreSslCertificate Ignore ssl certificate (useful for test)
 * @param headers Custom headers
 * @param encoder Encoder to use
 * @param decoder Decoder to use
 */
class FeignClient(
        baseUri: URI,
        ignoreSslCertificate: Boolean = false,
        val headers: Map<String, String>? = null,
        val encoder: feign.codec.Encoder = JacksonEncoder(),
        val decoder: feign.codec.Decoder = JacksonDecoder())
    : RestClient(baseUri, ignoreSslCertificate) {

    /**
     * A client without https cerfificate validation
     */
    private val clientWithoutSslValidation: Client by lazy {
        OkHttpClient(
                okhttp3.OkHttpClient.Builder()
                        .sslSocketFactory(TrustingSSLSocketFactory.get(), IgnoringX509TrustManager())
                        .hostnameVerifier(object : HostnameVerifier {
                            override fun verify(s: String, sslSession: SSLSession): Boolean = true
                        })
                        .build()
        )
    }

    /**
     * Default feign client
     */
    private val client: Client by lazy {
        OkHttpClient()
    }

    val builder by lazy {
        Feign.builder()
                .client(if (!this.ignoreSslCertificate) client else clientWithoutSslValidation)
                .retryer(Retryer.NEVER_RETRY)
                .options(Request.Options(
                        connectTimeout.toMillis().toInt(),
                        readTimeout.toMillis().toInt()))
                .encoder(this.encoder)
                .decoder(this.decoder)
                // TODO: temporarily customized feign client with workaround for https://github.com/OpenFeign/feign/issues/669
                .contract(JAXRS2Contract())
                .also {
                    when {
                        this.headers != null -> {
                            // Intercept request to set headers
                            it.requestInterceptor {
                                it.headers(this.headers.mapValues { listOf(it.value) })
                            }
                        }
                    }
                }
    }

    override fun <T> proxy(serviceClass: Class<T>): T =
            this.builder.target(serviceClass, this.baseUri.toString())

    /**
     * Convenience extension method for creating feign target with streaming support
     */
    fun <T> target(
            apiType: Class<T>,
            output: OutputStream,
            progressCallback: ((p: Float, bytesCopied: Long) -> Unit)? = null): T {

        return this.builder.decoder(
                sx.feign.StreamDecoder(
                        fallbackDecoder = this@FeignClient.decoder,
                        output = output,
                        progressCallback = progressCallback
                ))
                .target(apiType, baseUri.toString())
    }
}