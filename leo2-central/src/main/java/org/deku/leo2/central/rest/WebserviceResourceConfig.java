package org.deku.leo2.central.rest;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by masc on 23.07.14.
 */
public class WebserviceResourceConfig extends ResourceConfig {
    public WebserviceResourceConfig()
    {
        register(new LoggingFilter(Logger.getGlobal(), true));

        packages("org.deku.leo2.central.rest");

        Set<Class<?>> classes = this.getClasses();
        System.out.println(classes);
    }
}
