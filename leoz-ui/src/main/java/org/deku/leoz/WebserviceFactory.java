package org.deku.leoz;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.rest.services.internal.v1.StationService;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Webservice factory
 *
 * Created by masc on 27.08.14.
 */
public class WebserviceFactory {
    private static Log mLog = LogFactory.getLog(WebserviceFactory.class);
    private static Client mClient;
    private static WebTarget mWebTarget;

    /** Convenience method to get service instance
     * @param c service interface class*/
    public static <T> T getService(Class<T> c) {
        // Setup client
        if (mClient == null) {
            mClient = ClientBuilder.newClient();
            mClient.register(JacksonJsonProvider.class);
            mClient.property(ClientProperties.CONNECT_TIMEOUT, 2000);
            mClient.property(ClientProperties.READ_TIMEOUT, 2000);
        }

        if (mWebTarget == null) {
            mWebTarget = mClient.target("http://10.0.10.10:8080/leoz/rs/api");
            //mWebTarget = mClient.target("http://localhost:8080/leo2");
        }

        T instance = WebResourceFactory.newResource(c, mWebTarget);
        return instance;
    }

    public static StationService depotService() {
        return getService(StationService.class);
    }
}
