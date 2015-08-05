package org.deku.leo2.central;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.central.data.sync.DatabaseSyncConfiguration;
import org.deku.leo2.central.data.sync.EntitySyncConfiguration;
import org.deku.leo2.central.messaging.MessageListenerConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Spring boot main class.
 * Disabled auto configuraton as it's slow. Pulling in configurations manually as needed.
 * <p>
 * Created by masc on 28.05.15.
 */
@Configuration("central.MainSpringBoot")
@Order(Ordered.HIGHEST_PRECEDENCE)
@ComponentScan(lazyInit = true)
@Import({EntitySyncConfiguration.class,
        DatabaseSyncConfiguration.class,
        MessageListenerConfiguration.class})
public class Main extends org.deku.leo2.node.Main {
    private static Log mLog = LogFactory.getLog(Main.class);

    /**
     * Standalone jetty
     * @param args
     * @throws Exception
     */
    public static void main(String[] args){
        App.inject(App::new);
        org.deku.leo2.node.Main.run(Main.class, args);
    }
}