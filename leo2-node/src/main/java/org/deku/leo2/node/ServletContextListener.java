package org.deku.leo2.node;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.inject.Named;
import javax.servlet.ServletContextEvent;
import java.util.logging.Logger;

/**
 * Created by masc on 17.09.14.
 */
@Named
public class ServletContextListener implements javax.servlet.ServletContextListener {
    Logger mLog = Logger.getLogger(ServletContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        mLog.info("Leo2 servlet context initalizer");

        // Log4j logging (to make resteasy log entries visible, needs refinement)
        // BasicConfigurator.configure();
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        mLog.info("Leo2 servlet Context destroyed");
    }
}
