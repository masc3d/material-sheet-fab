package org.deku.leo2.central;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import java.util.logging.Logger;

/**
 * Created by masc on 05.05.15.
 */
@Configuration
@ComponentScan("org.deku.leo2.node.rest")
public class WebApplicationInitializer implements org.springframework.web.WebApplicationInitializer {
    Logger mLog = Logger.getLogger(WebApplicationInitializer.class.getName());

    @Override
    public void onStartup(ServletContext container) {
        mLog.info("Leo2 web application intializer");
    }
}
