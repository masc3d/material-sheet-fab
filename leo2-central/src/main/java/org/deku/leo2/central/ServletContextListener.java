package org.deku.leo2.central;

import org.apache.log4j.BasicConfigurator;
import org.springframework.context.ConfigurableApplicationContext;

import javax.inject.Named;
import javax.servlet.ServletContextEvent;
import java.util.logging.Logger;

/**
 * Created by masc on 17.09.14.
 */
@Named
public class ServletContextListener implements javax.servlet.ServletContextListener {
    ConfigurableApplicationContext mContext;

    Logger mLog = Logger.getLogger(ServletContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        mLog.info("Leo2 servlet context initalizer");

        // Log4j logging
        BasicConfigurator.configure();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        mLog.info("Leo2 servlet Context destroyed");
    }
}
