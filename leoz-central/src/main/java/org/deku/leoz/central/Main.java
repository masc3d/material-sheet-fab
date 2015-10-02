package org.deku.leoz.central;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.central.config.DatabaseSyncConfiguration;
import org.deku.leoz.central.config.EntitySyncConfiguration;
import org.deku.leoz.central.config.MessageListenerConfiguration;
import org.deku.leoz.central.config.PersistenceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Spring boot main class.
 * Disabled auto configuraton as it's slow. Pulling in configurations manually as needed.
 * <p>
 * Created by masc on 28.05.15.
 */
@Configuration("central.MainSpringBoot")
@Order(Ordered.HIGHEST_PRECEDENCE)
@ComponentScan(lazyInit = true)
@Import({
        PersistenceConfiguration.class,
        EntitySyncConfiguration.class,
        DatabaseSyncConfiguration.class,
        MessageListenerConfiguration.class})
public class Main extends org.deku.leoz.node.Main {
    private static Log mLog = LogFactory.getLog(Main.class);

    /**
     * Standalone jetty
     * @param args
     * @throws Exception
     */
    public static void main(String[] args){
        App.inject(App::new);
        org.deku.leoz.node.Main.run(Main.class, args);
    }
}