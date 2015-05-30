package org.deku.leo2.node.web;

import org.deku.leo2.node.Global;

import javax.inject.Named;
import javax.servlet.ServletContext;
import java.util.logging.Logger;

/**
 * Created by masc on 05.05.15.
 */
@Named
public class WebApplicationInitializer implements org.springframework.web.WebApplicationInitializer {
    Logger mLog = Logger.getLogger(WebApplicationInitializer.class.getName());

    @Override
    public void onStartup(ServletContext container) {
        mLog.info("Leo2 node web application initializer");
        Global.instance().initializeLogging();
    }
}
