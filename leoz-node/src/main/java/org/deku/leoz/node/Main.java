package org.deku.leoz.node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.node.data.sync.EntitySyncConfiguration;
import org.deku.leoz.node.messaging.BrokerConfiguration;
import org.deku.leoz.node.messaging.MessageListenerConfiguration;
import org.deku.leoz.node.rsync.RsyncConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.resteasy.autoconfigure.ResteasyAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

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

        /** Leoz embedded broker configuration */
        BrokerConfiguration.class,
        /** Leoz entity sync configuration */
        EntitySyncConfiguration.class,
        /** Leoz message listener configuration */
        MessageListenerConfiguration.class,
        /** Rsync configuration */
        RsyncConfiguration.class
})
@EnableConfigurationProperties
public class Main {
    private static Log mLog = LogFactory.getLog(Main.class);

    /**
     * Standalone startup
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        Main.run(Main.class, args);
    }

    protected static void run(Class c, String[] args) {
        try {
            mLog.info(String.format("Main arguments [%s]", String.join(", ", args)));

            // Support for command line parameters, setup commands
            if (args != null && args.length > 0) {
                String command = args[0].toLowerCase().trim();

                Runnable rCommand = null;
                switch (command) {
                    case "install":
                        rCommand = () -> Setup.instance().install("LeoZ Service", c);
                        break;
                    case "uninstall":
                        rCommand = () -> Setup.instance().uninstall();
                        break;
                    case "start":
                        rCommand = () -> Setup.instance().start();
                        break;
                    case "stop":
                        rCommand = () -> Setup.instance().stop();
                        break;
                }

                if (rCommand != null) {
                    try {
                        LogConfiguration.instance().initialize();
                        rCommand.run();
                    } catch (Exception e) {
                        mLog.error(e.getMessage(), e);
                        throw e;
                    } finally {
                        LogConfiguration.instance().dispose();
                    }
                    System.exit(0);
                }
            }

            // Initialize and start application
            App.instance().initialize();
            new SpringApplicationBuilder()
                    .showBanner(false)
                    .sources(c)
                    .profiles(App.instance().getProfile())
                    .listeners(App.instance())
                    .run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop application
     */
    public static void stop(String args[]) {
        App.instance().shutdown();
    }
}
