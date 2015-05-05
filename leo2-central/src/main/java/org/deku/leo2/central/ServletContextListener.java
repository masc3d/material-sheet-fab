package org.deku.leo2.central;

import javax.servlet.ServletContextEvent;
import java.util.logging.Logger;

/**
 * Created by masc on 17.09.14.
 */
public class ServletContextListener implements javax.servlet.ServletContextListener {
    Logger mLog = Logger.getLogger(ServletContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        mLog.info("Leo2 servlet context initalizer");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
