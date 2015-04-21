package org.deku.leo2.central;

import org.deku.leo2.central.Persistence;

import javax.servlet.ServletContextEvent;

/**
 * Created by masc on 17.09.14.
 */
public class ServletContextListener implements javax.servlet.ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Persistence.instance().initialize();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Persistence.instance().dispose();
    }
}
