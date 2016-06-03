package org.deku.leoz

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.rest.services.internal.v1.StationService
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.client.proxy.WebResourceFactory

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget

/**
 * Webservice factory

 * Created by masc on 27.08.14.
 */
object WebserviceFactory {
    private val mLog = LogFactory.getLog(WebserviceFactory::class.java)
    private var mClient: Client? = null
    private var mWebTarget: WebTarget? = null

    /** Convenience method to get service instance
     * @param c service interface class
     */
    fun <T> getService(c: Class<T>): T {
        // Setup client
        if (mClient == null) {
            mClient = ClientBuilder.newClient()
            mClient!!.register(JacksonJsonProvider::class.java)
            mClient!!.property(ClientProperties.CONNECT_TIMEOUT, 2000)
            mClient!!.property(ClientProperties.READ_TIMEOUT, 2000)
        }

        if (mWebTarget == null) {
            mWebTarget = mClient!!.target("http://127.0.0.1:13000/rs/api")
            //mWebTarget = mClient.target("http://localhost:8080/leo2");
        }

        val instance = WebResourceFactory.newResource(c, mWebTarget)
        return instance
    }

    fun depotService(): StationService {
        return getService(StationService::class.java)
    }
}
