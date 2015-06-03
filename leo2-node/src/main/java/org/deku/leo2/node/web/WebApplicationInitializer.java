package org.deku.leo2.node.web;

import org.deku.leo2.node.MainSpringBoot;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.logging.Logger;

/**
 * Created by masc on 05.05.15.
 */
@Named("webapp-init-node")
public class WebApplicationInitializer extends SpringBootServletInitializer {
    Logger mLog = Logger.getLogger(WebApplicationInitializer.class.getName());

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.profiles(MainSpringBoot.SPRING_PROFILE_BOOT);
        return builder.sources(MainSpringBoot.class);
    }

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        mLog.info("Leo2 node web application initializer");

        // Only start if there's no application context yet.
        if (!(container.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)
                instanceof ApplicationContext)) {
            super.onStartup(container);
        }
    }
}
