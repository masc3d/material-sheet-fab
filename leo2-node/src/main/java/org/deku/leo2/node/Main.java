package org.deku.leo2.node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.node.data.sync.EntitySyncConfiguration;
import org.deku.leo2.node.messaging.BrokerConfiguration;
import org.deku.leo2.node.messaging.MessageListenerConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.resteasy.autoconfigure.ResteasyAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Application main entry point
 * Created by masc on 28.05.15.
 */
@Configuration("node.MainSpringBoot")
@ComponentScan(lazyInit = true)
@Order(Ordered.LOWEST_PRECEDENCE)
// Auto configuraton is slow. Pulling in configurations manually as needed.
@Import({
        /** Setups up embedded web server and servlet container */
        EmbeddedServletContainerAutoConfiguration.class,
        /** Server properties support */
        ServerPropertiesAutoConfiguration.class,
        /**
         * Resteasy configuration. Only used for base setup, not fully autowired
         * as we currently prefer to setup the classic resteasy servlet manually
         * @link WebContextInitializer
         */
        ResteasyAutoConfiguration.class,
        /** Flyway database migration setup */
        //FlywayAutoConfiguration.class,

        /** Leo2 embedded broker configuration */
        BrokerConfiguration.class,
        /** Leo2 entity sync configuration */
        EntitySyncConfiguration.class,
        /** Leo2 message listener configuration */
        MessageListenerConfiguration.class,
})
@EnableConfigurationProperties
public class Main  {
    private static Log mLog = LogFactory.getLog(Main.class);

    /**
     * Standalone startup
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Main.run(Main.class, args);
    }

    protected static void run(Class c, String[] args) throws Exception {
        mLog.info(String.format("Main arguments [%s]", String.join(", ", args)));
        App.instance().initialize();

        new SpringApplicationBuilder()
                .sources(c)
                .profiles(App.instance().getProfile())
                .listeners(App.instance())
                .run(args);
    }

    /**
     * Stop application
     */
    public static void stop(String args[]) {
        App.instance().shutdown();
    }
}
