package org.deku.leo2.node.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by masc on 12.05.15.
 */
public class WebserviceApplication extends Application {
    private Log mLog = LogFactory.getLog(WebserviceApplication.class);
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
