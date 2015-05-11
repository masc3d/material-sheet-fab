package org.deku.leo2.central.rest;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by masc on 11.05.15.
 */
public class SwaggerWebserviceResourceConfig extends ResourceConfig {

    public SwaggerWebserviceResourceConfig() {
        // Packages containing web serivces
        packages("com.wordnik.swagger.jaxrs.listing", "org.deku.leo2.rest");
    }
}
