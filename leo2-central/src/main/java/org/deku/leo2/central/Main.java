package org.deku.leo2.central;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Spring boot main class.
 * Disabled auto configuraton as it's slow. Pulling in configurations manually as needed.
 *
 * Created by masc on 28.05.15.
 */
@Configuration("central.MainSpringBoot")
@Order(Ordered.HIGHEST_PRECEDENCE)
@ComponentScan(lazyInit = true)
public class Main extends org.deku.leo2.node.Main {
    private static Log mLog = LogFactory.getLog(Main.class);
    /**
     * Standalone jetty
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        App.inject(App::new);
        org.deku.leo2.node.Main.run(Main.class, args);
    }

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        mLog.info("leo2.central.main.onStartup");
        super.onStartup(container);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        mLog.info("leo2.central.main.configure");
        App.inject(App::new);
        try {
            App.instance().initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return builder
                .sources(Main.class)
                .listeners(App.instance());
    }
}