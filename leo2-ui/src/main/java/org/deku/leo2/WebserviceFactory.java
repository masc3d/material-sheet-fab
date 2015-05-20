package org.deku.leo2;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.deku.leo2.rest.services.internal.v1.DepotService;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.logging.Logger;

/**
 * Webservice factory
 *
 * Created by masc on 27.08.14.
 */
public class WebserviceFactory {
    private static Logger mLog = Logger.getLogger(WebserviceFactory.class.getName());
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
            mWebTarget = mClient.target("http://10.0.10.10:8080/leo2/rs/api");
            //mWebTarget = mClient.target("http://localhost:8080/leo2");
        }

        T instance = WebResourceFactory.newResource(c, mWebTarget);
        return instance;
    }

    public static DepotService depotService() {
        return getService(DepotService.class);
    }
}
