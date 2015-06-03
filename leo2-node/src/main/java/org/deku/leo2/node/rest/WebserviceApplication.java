package org.deku.leo2.node.rest;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by masc on 12.05.15.
 */
public class WebserviceApplication extends Application {
    private Logger mLog = Logger.getLogger(WebserviceApplication.class.getName());
    private Set<Object> mSingletons = new HashSet<Object>();
    private Set<Class<?>> mClasses = new HashSet<>();

    public WebserviceApplication() {
        mLog.info("Leo2 node jax/ws/rs webservice application");
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
