package org.deku.leo2.central.rest;

import org.deku.leo2.node.rest.ObjectMapperProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.After;
import org.junit.Before;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.net.URI;

/**
 * Created by masc on 20.04.15.
 */
public class WebserviceTest {
    Server mServer;
    private Client client;
    private WebTarget target;

    private static final URI BASE_URI = URI.create("http://localhost:8080/leo2/rs/api/");

    @Before
    public void setup() throws Exception {
        org.deku.leo2.central.Main.main(null);

        // Setup jaxrs client & target
        this.client = ClientBuilder.newClient();
        client.register(ObjectMapperProvider.class);
        this.target = client.target(BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        mServer.stop();
    }


    public <T> T getService(Class<T> c) {
        return (T) ((ResteasyWebTarget)target).proxy(c);
    }
}
