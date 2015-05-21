package org.deku.leo2.node.rest;

import org.deku.leo2.node.rest.services.SwaggerListingResourceInternal;
import org.deku.leo2.node.rest.services.SwaggerListingResourcePublic;

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
        mClasses.add(SwaggerListingResourcePublic.class);
        mClasses.add(SwaggerListingResourceInternal.class);
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
