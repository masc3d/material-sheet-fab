package org.leo2.rest;

import org.glassfish.jersey.jackson1.Jackson1Feature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by masc on 23.07.14.
 */
public class WebserviceResourceConfig extends ResourceConfig {
    public WebserviceResourceConfig()
    {
        super(/*NonJaxbBeanResource.class,
                CombinedAnnotationResource.class, */
                Jackson1Feature.class);

        packages("org.leo2.rest",
                "org.leo2.rest.v1");
    }
}
