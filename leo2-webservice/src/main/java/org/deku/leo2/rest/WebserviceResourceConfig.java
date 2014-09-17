package org.deku.leo2.rest;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by masc on 23.07.14.
 */
public class WebserviceResourceConfig extends ResourceConfig {
    public WebserviceResourceConfig()
    {
        super(JacksonFeature.class);

        packages("org.deku.leo2.rest.services");
    }
}
