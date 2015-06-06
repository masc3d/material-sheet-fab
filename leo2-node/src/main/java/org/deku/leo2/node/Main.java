package org.deku.leo2.node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.activemq.BrokerImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.resteasy.autoconfigure.ResteasyAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Spring boot main class.
 *
 * Created by masc on 28.05.15.
 */
@Configuration("node.MainSpringBoot")
@Order(Ordered.LOWEST_PRECEDENCE)
@ComponentScan(lazyInit = true)
// Auto configuraton is slow. Pulling in configurations manually as needed.
@Import({
        EmbeddedServletContainerAutoConfiguration.class,
        ServerPropertiesAutoConfiguration.class,
        ResteasyAutoConfiguration.class,
        //DataSourceAutoConfiguration.class
})
public class Main extends SpringBootServletInitializer implements ApplicationListener, SpringApplicationRunListener {
    private Log mLog = LogFactory.getLog(Main.class);

    /**
     * Standalone jetty
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        Main.run(Main.class, args);
    }

    protected static void run(Class c, String[] args) {
        App.instance().initialize();
        SpringApplication.run(c);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        mLog.info("Leo2 node main.configure");
        return builder
                .sources(Main.class)
                .listeners(this);
    }

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        mLog.info("Leo2 node main.onStartup");

        // Only start if there's no application context yet.
        if (container.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)
                instanceof ApplicationContext) {
            return;
        }

        App.instance().initialize();

        super.onStartup(container);
    }

    @Override
    public final void onApplicationEvent(ApplicationEvent event) {
        mLog.info(event.toString());
        if (event instanceof EmbeddedServletContainerInitializedEvent) {
            // Post spring initialization
        }
    }

    @Override
    public void started() {
        mLog.info("SPRING started");
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        mLog.info("SPRING env prepared");
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        mLog.info("SPRING context prepared");
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        mLog.info("SPRING context loaded");
    }

    @Override
    public void finished(ConfigurableApplicationContext context, Throwable exception) {
        mLog.info("SPRING finished");
    }
}
