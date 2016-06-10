package org.deku.leoz.rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.After;
import org.junit.Before;

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
        this.target = client.target("http://10.0.10.10:8080/leoz");
        this.target = client.target("http://127.0.0.1:13000/rs/api");
    }

    @After
    public void tearDown() throws Exception {

    }

    /** Convenience method to get service instance
     * @param c service interface class*/
    public <T> T getService(Class<T> c) {
        return ((ResteasyWebTarget)target).proxy(c);
    }
}
