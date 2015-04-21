package org.deku.leo2.central.rest;

import org.glassfish.jersey.server.ResourceConfig;

import java.util.Set;

/**
 * Created by masc on 23.07.14.
 */
public class WebserviceResourceConfig extends ResourceConfig {
    public WebserviceResourceConfig()
    {
        packages("org.deku.leo2.central.rest");

        Set<Class<?>> classes = this.getClasses();
        System.out.println(classes);
    }
}
