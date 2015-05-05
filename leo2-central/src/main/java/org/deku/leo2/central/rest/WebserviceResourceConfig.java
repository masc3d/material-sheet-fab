package org.deku.leo2.central.rest;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by masc on 23.07.14.
 */
public class WebserviceResourceConfig extends ResourceConfig {
    private Logger mLog = Logger.getLogger(WebserviceResourceConfig.class.getName());

    private static boolean mLogRequestsEnabled = false;
    private static boolean mLogEntitiesEnabled = false;

    public WebserviceResourceConfig()
    {
        if (mLogEntitiesEnabled)
            mLogRequestsEnabled = true;

        if (mLogRequestsEnabled) {
            register(new LoggingFilter(Logger.getGlobal(), mLogEntitiesEnabled));
        }

        // Packages containing web serivces
        packages("org.deku.leo2.central.rest");

        // TRACE: Log clases of this web resource
        Set<Class<?>> classes = this.getClasses();
        mLog.info("Web resource classes: " + classes.toString());
    }

    public static boolean isLogRequestsEnabled() {
        return mLogRequestsEnabled;
    }

    public static void setLogRequestsEnabled(boolean logRequestsEnabled) {
        mLogRequestsEnabled = logRequestsEnabled;
    }

    public static boolean isLogEntitiesEnabled() {
        return mLogEntitiesEnabled;
    }

    public static void setLogEntitiesEnabled(boolean logEntitiesEnabled) {
        mLogEntitiesEnabled = logEntitiesEnabled;
    }
}
