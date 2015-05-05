package org.deku.leo2.central.rest;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
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
    HttpServer mServer;
    private Client client;
    private WebTarget target;

    private static final URI BASE_URI = URI.create("http://localhost:8080/leo2/");

    @Before
    public void setup() {
        mServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, new WebserviceResourceConfig());

        // Setup client
        this.client = ClientBuilder.newClient();
        client.register(ObjectMapperProvider.class);

        // Setup target
        this.target = client.target(BASE_URI);
    }

    @After
    public void tearDown() {
        mServer.shutdownNow();
    }


    public <T> T getService(Class<T> c) {
        return (T) WebResourceFactory.newResource(c, this.target);
    }
}
