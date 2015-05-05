package org.deku.leo2.central;

import javax.servlet.ServletContext;
import java.util.logging.Logger;

/**
 * Created by masc on 05.05.15.
 */
public class WebApplicationInitializer implements org.springframework.web.WebApplicationInitializer {
    Logger mLog = Logger.getLogger(WebApplicationInitializer.class.getName());

    @Override
    public void onStartup(ServletContext container) {
        mLog.info("Leo2 web application intializer");
    }
}
