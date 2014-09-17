package org.leo2.rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Webservice test base class
 *
 * Created by masc on 25.07.14.
 */
public abstract class WebserviceTest {
    private Client client;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        // Setup client
        this.client = ClientBuilder.newClient();
        client.register(JacksonJsonProvider.class);

        // Setup target
        this.target = client.target("http://10.0.10.10:8080/leo2");
    }

    @After
    public void tearDown() throws Exception {

    }

    /** Convenience method to get service instance
     * @param c service interface class*/
    public <T> T getService(Class<T> c) {
        return (T)WebResourceFactory.newResource(c, this.target);
    }
}
