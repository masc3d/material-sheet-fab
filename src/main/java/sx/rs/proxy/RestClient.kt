package sx.rs.proxy

import feign.Client
import feign.Feign
import feign.Request
import feign.Retryer
import feign.jackson.JacksonDecoder
import feign.jaxrs.JAXRSContract
import feign.okhttp.OkHttpClient
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.client.JerseyClientBuilder
import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget
import org.threeten.bp.Duration
import sx.net.TrustingSSLSocketFactory
import java.io.OutputStream
import java.net.URI
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

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
abstract class RestClientProxy(
        val baseUri: URI,
        val ignoreSslCertificate: Boolean = false) {

    protected val connectTimeout = Duration.ofSeconds(5)
    protected val socketTimeout = Duration.ofSeconds(5)

    protected val ignoringCertificateSslContext by lazy {
        val ignoringCertificateSslContext = SSLContext.getInstance("TLS")
        ignoringCertificateSslContext.init(null, arrayOf<TrustManager>(IgnoringX509TrustManager()), SecureRandom())
        ignoringCertificateSslContext
    }

    abstract fun <T> create(serviceClass: Class<T>): T
}

/**
 * Jersey client proxy implementation
 */
class JerseyClientProxy(
        baseUri: URI,
        ignoreSslCertificate: Boolean = false) : RestClientProxy(baseUri, ignoreSslCertificate) {

    private val client by lazy {
        var clientBuilder = JerseyClientBuilder()
                .property(ClientProperties.CONNECT_TIMEOUT, connectTimeout.toMillis().toInt())
                .property(ClientProperties.READ_TIMEOUT, socketTimeout.toMillis().toInt())

        if (ignoreSslCertificate) {
            clientBuilder = clientBuilder
                    .sslContext(ignoringCertificateSslContext)
                    .hostnameVerifier { _, _ -> true }
        }
        clientBuilder.build()
    }


    override fun <T> create(serviceClass: Class<T>): T {
        val webTarget = this.client.target(baseUri)
        return WebResourceFactory.newResource(serviceClass, webTarget)
    }
}

/**
 * RESTEasy client proxy implementation
 */
class RestEasyClientProxy(
        baseUri: URI,
        ignoreSslCertificate: Boolean = false) : RestClientProxy(baseUri, ignoreSslCertificate) {

    private val client by lazy {
        var clientBuilder = ResteasyClientBuilder()
                .establishConnectionTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .socketTimeout(socketTimeout.toMillis(), TimeUnit.MILLISECONDS)

        if (ignoreSslCertificate) {
            clientBuilder = clientBuilder
                    .sslContext(ignoringCertificateSslContext)
                    .hostnameVerifier { _, _ -> true }
        }
        clientBuilder.build()
    }

    override fun <T> create(serviceClass: Class<T>): T {
        val webTarget: ClientWebTarget = this.client.target(baseUri) as ClientWebTarget

        // return webTarget.proxy(serviceClass)
        // masc20170329. for compatibility with spring-boot-devtools restart, need to use the serviceclass' classloader
        // to avoid `IllegalArgumentException` `is not visible from class loader`
        return webTarget
                .proxyBuilder(serviceClass)
                .classloader(serviceClass.classLoader)
                .build()
    }
}

/**
 * Feign client proxy implementation
 */
class FeignClientProxy(
        baseUri: URI,
        ignoreSslCertificate: Boolean = false,
        val encoder: feign.codec.Encoder,
        val decoder: feign.codec.Decoder)
    : RestClientProxy(baseUri, ignoreSslCertificate) {

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
                .options(Request.Options(connectTimeout.toMillis().toInt(), socketTimeout.toMillis().toInt()))
                .encoder(this.encoder)
                .decoder(this.decoder)
                .contract(JAXRSContract())
    }

    override fun <T> create(serviceClass: Class<T>): T =
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
                        fallbackDecoder = this@FeignClientProxy.decoder,
                        output = output,
                        progressCallback = progressCallback
                ))
                .target(apiType, baseUri.toString())
    }
}