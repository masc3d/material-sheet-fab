package org.deku.leo2.central;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.logging.Logger;

/**
 * Spring boot main class.
 * Disabled auto configuraton as it's slow. Pulling in configurations manually as needed.
 *
 * Created by masc on 28.05.15.
 */
@Configuration("central.MainSpringBoot")
@Order(Ordered.HIGHEST_PRECEDENCE)
@ComponentScan
public class Main extends org.deku.leo2.node.Main {
    private static Logger mLog = Logger.getLogger(Main.class.getName());
    /**
     * Standalone jetty
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        initializeInjection();
        org.deku.leo2.node.Main.run(Main.class, args);
    }

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        mLog.info("leo2.central.main.onStartup");
        initializeInjection();
        super.onStartup(container);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        mLog.info("leo2.central.main.configure");
        return builder
                .sources(Main.class)
                .listeners(this);
    }

    private static void initializeInjection() {
        mLog.info("injection");
        // Inject node singleton overrides
        App.inject(App::new);
    }
}