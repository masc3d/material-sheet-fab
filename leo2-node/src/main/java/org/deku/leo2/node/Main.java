package org.deku.leo2.node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.node.data.sync.EntitySyncConfiguration;
import org.deku.leo2.node.messaging.BrokerConfiguration;
import org.deku.leo2.node.messaging.PeerMessageListenerConfiguration;
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
 * Spring boot main class.
 *
 * Created by masc on 28.05.15.
 */
@Configuration("node.MainSpringBoot")
@ComponentScan(lazyInit = true)
@Order(Ordered.LOWEST_PRECEDENCE)
// Auto configuraton is slow. Pulling in configurations manually as needed.
@Import({
        EmbeddedServletContainerAutoConfiguration.class,
        ServerPropertiesAutoConfiguration.class,
        ResteasyAutoConfiguration.class,

        BrokerConfiguration.class,
        EntitySyncConfiguration.class,
        PeerMessageListenerConfiguration.class
        //DataSourceAutoConfiguration.class
})
@EnableConfigurationProperties
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
                .profiles(App.instance().getProfile())
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
        App.instance().initialize();
        return builder
                .sources(Main.class)
                .profiles(App.instance().getProfile())
                .listeners(App.instance());
    }
}
