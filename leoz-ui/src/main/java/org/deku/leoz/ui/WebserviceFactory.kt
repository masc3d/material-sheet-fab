package org.deku.leoz.ui

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.discovery.DiscoveryInfo
import org.deku.leoz.discovery.DiscoveryService
import org.deku.leoz.rest.services.internal.v1.StationService
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.glassfish.jersey.logging.LoggingFeature
import org.slf4j.LoggerFactory
import sx.LazyInstance
import sx.net.UdpDiscoveryService
import java.util.logging.Level
import java.util.logging.Logger
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget

/**
 * Webservice factory
 * Created by masc on 27.08.14.
 */
object WebserviceFactory {
    const val CLIENT_LOG = false

    private val log = LoggerFactory.getLogger(WebserviceFactory::class.java)

    private val discoveryService: DiscoveryService by Kodein.global.lazy.instance()

    /**
     * Creates a client ignoring SSL certificate (local node connections)
     */
    private fun createClientIgnoringSSL(): Client {
        val sslcontext = SSLContext.getInstance("TLS")
        sslcontext.init(null, arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkServerTrusted(p0: Array<out java.security.cert.X509Certificate>?, p1: String?) {
            }

            override fun checkClientTrusted(p0: Array<out java.security.cert.X509Certificate>?, p1: String?) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf<X509Certificate>()
            }
        }), java.security.SecureRandom())
        return ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier { s1, s2 -> true }.build()
    }

    /**
     * Webservice client
     */
    private val client by lazy({
        // Setup client
        val client = ClientBuilder.newClient()
        client.register(JacksonJsonProvider::class.java)
        client.property(ClientProperties.CONNECT_TIMEOUT, 2000)
        client.property(ClientProperties.READ_TIMEOUT, 2000)

        if (CLIENT_LOG) {
            val logger = Logger.getLogger(this.javaClass.name)
            client.register(LoggingFeature(logger, Level.INFO, null, null))
        }

        client
    })

    private val _webTarget = LazyInstance<WebTarget>( { this.client.target("http://127.0.0.1:13000/rs/api")
    })

    /**
     * Update web target
     */
    private fun update() {
        var foundService: DiscoveryInfo.Service? = null
        val host = this.discoveryService.directory.firstOrNull {
            foundService = it.info?.services?.firstOrNull { it.type == DiscoveryInfo.ServiceType.HTTP }
            foundService != null
        }

        val service = foundService

        if (host != null && service != null) {
            val url = "http://${host.address.hostName}:${service.port}/rs/api"
            log.info("Setting webservice url to ${url}")
            _webTarget.reset {
                this.client.target(url)
            }
        }
    }

    init {
        this.discoveryService.rxOnUpdate.subscribe {
            this.update()
        }

        this.update()
    }

    /** Convenience method to get service instance
     * @param c service interface class
     */
    fun <T> getService(c: Class<T>): T {
        val instance = WebResourceFactory.newResource(c, _webTarget.get())
        return instance
    }

    fun depotService(): StationService {
        return getService(StationService::class.java)
    }
}
