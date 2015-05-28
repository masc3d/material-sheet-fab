package org.deku.leo2.node.web;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.inject.Named;
import javax.servlet.ServletContextEvent;
import org.slf4j.Logger;
//import java.util.logging.Logger;

/**
 * Created by masc on 17.09.14.
 */
@Named("node.ServletContextListener")
public class ServletContextListener implements javax.servlet.ServletContextListener {
    Logger mLog = LoggerFactory.getLogger(ServletContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        mLog.info("Leo2 node servlet context initalizer");

        // Log all bean/names in the context
        ConfigurableWebApplicationContext wac = (ConfigurableWebApplicationContext)
                WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());

        ConfigurableListableBeanFactory clbf = wac.getBeanFactory();

        for (String beanName : wac.getBeanDefinitionNames()) {
            Object s = clbf.getSingleton(beanName);
            mLog.info(String.format("%s: %s", beanName, (s != null) ? s.getClass().getName() : "<null>"));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        mLog.info("Leo2 servlet Context destroyed");
    }
}
