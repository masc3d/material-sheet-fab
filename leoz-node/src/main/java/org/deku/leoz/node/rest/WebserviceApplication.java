package org.deku.leoz.node.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by masc on 12.05.15.
 */
public class WebserviceApplication extends Application {
    private Logger mLog = LoggerFactory.getLogger(WebserviceApplication.class);
    private Set<Object> mSingletons = new HashSet<Object>();
    private Set<Class<?>> mClasses = new HashSet<>();

    public WebserviceApplication() {
        mLog.info("Leoz node jax/ws/rs webservice application");
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
