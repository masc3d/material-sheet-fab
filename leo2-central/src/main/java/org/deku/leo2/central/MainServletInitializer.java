package org.deku.leo2.central;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.node.App;
import org.deku.leo2.node.Main;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Servlet main entry point
 * Created by n3 on 22-Jul-15.
 */
public class MainServletInitializer extends SpringBootServletInitializer {
    private static Log mLog = LogFactory.getLog(org.deku.leo2.node.Main.class);

    /**
     * Tomcat/servlet container startup
     * @param container
     * @throws ServletException
     */
    @Override
    public void onStartup(ServletContext container) throws ServletException {
        mLog.info("leo2.central.main.onStartup");
        super.onStartup(container);
    }

    /**
     * Tomcat/servlet container context setup
     * @param builder
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        mLog.info("leo2.central.main.configure");
        App.inject(org.deku.leo2.central.App::new);

        App.instance().initialize();
        return builder
                .sources(Main.class)
                .profiles(App.instance().getProfile())
                .listeners(App.instance());
    }
}
