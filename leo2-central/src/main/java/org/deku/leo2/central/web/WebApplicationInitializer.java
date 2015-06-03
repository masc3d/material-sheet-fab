package org.deku.leo2.central.web;

import org.deku.leo2.node.Main;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.logging.Logger;

/**
 * Created by masc on 05.05.15.
 */
@Named("webapp-init-central")
public class WebApplicationInitializer extends SpringBootServletInitializer  {
    Logger mLog = Logger.getLogger(WebApplicationInitializer.class.getName());

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(org.deku.leo2.central.Main.class);
    }

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        mLog.info("Leo2 central web application initializer");
        super.onStartup(container);
    }
}
