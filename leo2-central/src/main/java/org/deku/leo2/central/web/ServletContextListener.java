package org.deku.leo2.central.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.central.data.sync.DatabaseSync;
import org.deku.leo2.central.data.sync.EntitySync;
import org.deku.leo2.node.data.PersistenceContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletContextEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by masc on 17.09.14.
 */
@Named("central.ServletContextListener")
public class ServletContextListener implements javax.servlet.ServletContextListener {
    Log mLog = LogFactory.getLog(ServletContextListener.class);

    @Inject
    DatabaseSync mDatabaseSync;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        mLog.info("Leo2 central servlet context initalizer");

        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());

        // Trigger database sync
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(() -> {
            mDatabaseSync.sync();
        });

        exec.shutdown();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        mLog.info("Leo2 servlet Context destroyed");
    }
}
