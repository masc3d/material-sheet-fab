package org.deku.leo2.central.web;

import org.deku.leo2.central.data.DatabaseSync;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.inject.Named;
import javax.servlet.ServletContextEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by masc on 17.09.14.
 */
@Named("central.ServletContextListener")
public class ServletContextListener implements javax.servlet.ServletContextListener {
    Logger mLog = Logger.getLogger(ServletContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        mLog.info("Leo2 central servlet context initalizer");

        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());

        // Trigger database sync
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(() -> {
            DatabaseSync sync = ac.getBean(DatabaseSync.class);
            sync.sync();
        });
        exec.shutdown();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        mLog.info("Leo2 servlet Context destroyed");
    }
}
