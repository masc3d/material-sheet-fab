package org.deku.leo2.central;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.activemq.ActiveMQBroker;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.messaging.log.LogListener;

/**
 * Created by masc on 30.05.15.
 */
public class App extends org.deku.leo2.node.App {
    private Log mLog = LogFactory.getLog(App.class);

    public static App instance() {
        return (App)org.deku.leo2.node.App.instance();
    }

    @Override
    public void initialize() {
        // No JMS logging for leo2-central
        super.initialize(LogConfigurationType.NONE, EntitySyncConfigurationType.NONE);
    }
}
