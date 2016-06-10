package org.deku.leoz

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import org.apache.commons.logging.LogFactory
import org.deku.leoz.rest.services.internal.v1.StationService
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.glassfish.jersey.logging.LoggingFeature
import java.util.logging.Level
import java.util.logging.Logger
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget

/**
 * Webservice factory

 * Created by masc on 27.08.14.
 */
object WebserviceFactory {
    const val CLIENT_LOG = false

    private val log = LogFactory.getLog(WebserviceFactory::class.java)

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

    private val webTarget: WebTarget by lazy({
        // Setup webtarget
        this.client.target("http://127.0.0.1:13000/rs/api")
    })

    /** Convenience method to get service instance
     * @param c service interface class
     */
    fun <T> getService(c: Class<T>): T {
        val instance = WebResourceFactory.newResource(c, webTarget)
        return instance
    }

    fun depotService(): StationService {
        return getService(StationService::class.java)
    }
}
