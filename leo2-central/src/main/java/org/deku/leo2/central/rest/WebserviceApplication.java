package org.deku.leo2.central.rest;

import org.deku.leo2.central.rest.services.internal.v1.DepotService;
import org.deku.leo2.central.rest.services.v1.RoutingService;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.springmvc.ResteasyHandlerAdapter;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by masc on 12.05.15.
 */
public class WebserviceApplication extends Application {
    private Set<Object> mSingletons = new HashSet<Object>();
    private Set<Class<?>> mClasses = new HashSet<>();

    public WebserviceApplication() {
        mClasses.add(com.wordnik.swagger.jaxrs.listing.ApiListingResource.class);
    }

    @Override
    public Set<Object> getSingletons() {
        return mSingletons;
    }

    @Override
    public Set<Class<?>> getClasses() {
        return mClasses;
    }
}
