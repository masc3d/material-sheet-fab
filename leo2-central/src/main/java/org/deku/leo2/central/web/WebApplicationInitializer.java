package org.deku.leo2.central.web;

import javax.inject.Named;
import javax.servlet.ServletContext;
import java.util.logging.Logger;

/**
 * Created by masc on 05.05.15.
 */
@Named("webapp-init-central")
public class WebApplicationInitializer implements org.springframework.web.WebApplicationInitializer {
    Logger mLog = Logger.getLogger(WebApplicationInitializer.class.getName());

    @Override
    public void onStartup(ServletContext container) {
        mLog.severe("Leo2 central web application initializer");
        //throw new IllegalStateException();
    }
}
