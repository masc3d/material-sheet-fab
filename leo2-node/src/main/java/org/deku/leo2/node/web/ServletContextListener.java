package org.deku.leo2.node.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContextEvent;

/**
 * Created by masc on 17.09.14.
 */
@Named("node.ServletContextListener")
public class ServletContextListener implements javax.servlet.ServletContextListener {
    Log mLog = LogFactory.getLog(ServletContextListener.class);

    @Inject
    ConfigurableWebApplicationContext mContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        mLog.info("Leo2 node servlet context initalizer");

        // Log all bean/names in the context
        ConfigurableListableBeanFactory clbf = mContext.getBeanFactory();

        mLog.trace(String.format("Registered beans: %d", mContext.getBeanDefinitionCount()));
        for (String beanName : mContext.getBeanDefinitionNames()) {
            BeanDefinition rbd = clbf.getMergedBeanDefinition(beanName);
            Object s = clbf.getSingleton(beanName);
            mLog.trace(String.format("%s: %s, lazy %s",
                    beanName,
                    (s != null) ? s.getClass().getName() : "<null>",
                    rbd.isLazyInit()));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        mLog.info("Leo2 servlet Context destroyed");
    }
}
