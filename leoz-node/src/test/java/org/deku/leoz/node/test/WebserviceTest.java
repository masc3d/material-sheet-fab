package org.deku.leoz.node.test;

import org.deku.leoz.node.Main;
import org.deku.leoz.node.rest.ObjectMapperProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.net.URI;

/**
 * Created by masc on 20.04.15.
 */
@Ignore
public class WebserviceTest {
    private Client client;
    private WebTarget target;

    private static final URI BASE_URI = URI.create("http://localhost:8080/leoz/rs/api/");

    @Before
    public void setup() throws Exception {
        Main.main(null);

        // Setup jaxrs client & target
        this.client = ClientBuilder.newClient();
        client.register(ObjectMapperProvider.class);
        this.target = client.target(BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
    }


    public <T> T getService(Class<T> c) {
        return ((ResteasyWebTarget)target).proxy(c);
    }
}
