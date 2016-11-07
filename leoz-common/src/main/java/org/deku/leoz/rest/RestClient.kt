package org.deku.leoz.rest

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget
import sx.LazyInstance
import java.net.URI
import java.net.URL
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget

/**
 * REST client
 * @param baseUri Optional base URI (can also be set via property)
 * @property ignoreSslCertificate Ignore SSL certificates
 * Created by masc on 06/11/2016.
 */
class RestClient(baseUri: URI? = null,
                 val ignoreSslCertificate: Boolean = false) {
    /**
     * Webservice client
     */
    private val client by lazy {
        var client = ResteasyClientBuilder()
                .establishConnectionTimeout(2, TimeUnit.SECONDS)
                .socketTimeout(2, TimeUnit.SECONDS)

        if (this.ignoreSslCertificate) {
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

            client = client
                    .sslContext(ignoringCertificateSslContext)
                    .hostnameVerifier { s, sslSession -> true  }
        }

//        if (CLIENT_LOG) {
//            val logger = Logger.getLogger(this.javaClass.name)
//            client.register(LoggingFeature(logger, Level.INFO, null, null))
//        }

        client.build()
    }

    /**
     * Webservice base URI
     */
    var baseUri: URI = URI("http://localhost")
        set(value: URI) {
            _webTarget.reset(this.client.target(value))
        }

    /** Lazy webtarget */
    private val _webTarget = LazyInstance<ResteasyWebTarget>( {
        this.client.target(this.baseUri)
    })

    /**
     * c'tor
     */
    init {
        if (baseUri != null)
            this.baseUri = baseUri
    }

    /**
     * Creates proxy for service
     * @param serviceClass Service class
     */
    fun <T> proxy(serviceClass: Class<T>): T {
        return _webTarget.get().proxy(serviceClass)
    }
}