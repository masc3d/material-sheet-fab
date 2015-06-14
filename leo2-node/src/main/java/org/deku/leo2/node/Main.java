package org.deku.leo2.node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.springframework.core.io.support.SpringFactoriesLoader;
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
public class Main extends SpringBootServletInitializer {
    private Log mLog = LogFactory.getLog(Main.class);

    /**
     * Standalone startup
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Main.run(Main.class, args);
    }

    protected static void run(Class c, String[] args) throws Exception {
        App.instance().initialize();

        new SpringApplicationBuilder()
                .sources(c)
                .listeners(App.instance())
                .run(args);
    }

    /**
     * Tomcat/servlet container startup
     * @param container
     * @throws ServletException
     */
    @Override
    public void onStartup(ServletContext container) throws ServletException {
        mLog.info("leo2.node.main.onStartup");
        super.onStartup(container);
    }

    /**
     * Tomcat/servlet container context setup
     * @param builder
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        mLog.info("leo2.node.main.configure");
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
