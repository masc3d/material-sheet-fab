package org.deku.leoz.rest

import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.client.JerseyClientBuilder
import org.glassfish.jersey.client.JerseyWebTarget
import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget
import sx.LazyInstance
import java.net.URI
import java.security.cert.X509Certificate
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.ws.rs.client.WebTarget

/**
 * REST client supporting both Jersey or Resteasy (client), depending on which framework is on the classpath.
 * The reason for currently supporting both:
 * - Jersey/grizzly is generally more easily to embedd as a light-weight REST server (as used in `leoz-ui`/bridge)
 * - RESTeasy performs better under load, especially with undertow/netty, thus it's the currently the better choice for `leoz-node`
 * - RESTeasy client cannot be used when jersey is on the classpath (as javax.ws.rs factories loads whatever it finds first, which will cause cast issues)
 * @param baseUri Optional base URI (can also be set via property)
 * @property ignoreSslCertificate Ignore SSL certificates
 * Created by masc on 06/11/2016.
 */
class RestClient(val baseUri: URI,
                 val ignoreSslCertificate: Boolean = false) {

    private val connectTimeout = Duration.ofSeconds(5)
    private val socketTimeout = Duration.ofSeconds(5)

    private val ignoringCertificateSslContext by lazy {
        val ignoringCertificateSslContext = SSLContext.getInstance("TLS")
        ignoringCertificateSslContext.init(null, arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkServerTrusted(p0: Array<out java.security.cert.X509Certificate>?, p1: String?) {
            }

            override fun checkClientTrusted(p0: Array<out java.security.cert.X509Certificate>?, p1: String?) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf<X509Certificate>()
            }
        }), java.security.SecureRandom())
        ignoringCertificateSslContext
    }

    private interface Proxy {
        fun <T> proxy(serviceClass: Class<T>): T
    }

    /**
     * Jersey client/proxy support
     */
    private inner class Jersey : Proxy {
        private val client by lazy {
            var clientBuilder = JerseyClientBuilder()
                .property(ClientProperties.CONNECT_TIMEOUT, connectTimeout.toMillis().toInt())
                .property(ClientProperties.READ_TIMEOUT, socketTimeout.toMillis().toInt())

            if (ignoreSslCertificate) {
                clientBuilder =  clientBuilder
                        .sslContext(ignoringCertificateSslContext)
                        .hostnameVerifier { _, _ -> true  }
            }
            clientBuilder.build()
        }

        override fun <T> proxy(serviceClass: Class<T>): T {
            val webTarget = this.client.target(baseUri)
            return WebResourceFactory.newResource(serviceClass, webTarget)
        }
    }

    /**
     * Resteasy client/proxy support
     */
    private inner class Resteasy : Proxy {
        private val client by lazy {
            var clientBuilder = ResteasyClientBuilder()
                    .establishConnectionTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS)
                    .socketTimeout(socketTimeout.toMillis(), TimeUnit.MILLISECONDS)

            if (ignoreSslCertificate) {
                clientBuilder =  clientBuilder
                        .sslContext(ignoringCertificateSslContext)
                        .hostnameVerifier { _, _ -> true  }
            }
            clientBuilder.build()
        }

        override fun <T> proxy(serviceClass: Class<T>): T {
            val webTarget = this.client.target(baseUri)
            return webTarget.proxy(serviceClass)
        }
    }

    private val proxy: Proxy

    init {
        proxy = try {
            Class.forName("org.glassfish.jersey.client.JerseyClientBuilder")
            Jersey()
        }
        catch(e: Exception) {
            Resteasy()
        }

    }

    /**
     * Creates proxy for service
     * @param serviceClass Service class
     */
    fun <T> proxy(serviceClass: Class<T>): T {
        return this.proxy.proxy(serviceClass)
    }
}