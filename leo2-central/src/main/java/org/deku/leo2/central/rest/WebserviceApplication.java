package org.deku.leo2.central.rest;

import com.wordnik.swagger.jaxrs.listing.ApiListingResource;
import org.deku.leo2.central.rest.services.v1.DepotService;
import org.springframework.beans.factory.annotation.Autowired;

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
    }

    @Override
    public Set<Object> getSingletons() {
        return super.getSingletons();
    }
}
