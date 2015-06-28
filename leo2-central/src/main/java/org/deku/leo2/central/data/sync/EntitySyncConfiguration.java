package org.deku.leo2.central.data.sync;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.activemq.ActiveMQBroker;
import org.deku.leo2.node.data.PersistenceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Created by masc on 20.06.15.
 */
@Configuration("central.EntitySyncConfiguration")
@Lazy(false)
public class EntitySyncConfiguration {
    private Log mLog = LogFactory.getLog(this.getClass());

    @PersistenceUnit(name = PersistenceConfiguration.DB_EMBEDDED)
    EntityManagerFactory mEntityManagerFactory;

    /** Broker listener */
    private ActiveMQBroker.Listener mBrokerListener = new ActiveMQBroker.Listener() {
        @Override
        public void onStart() {
            mLog.info("Detected broker start, initializing entity sync");
            EntitySync.instance().start();
        }
    };

    @PostConstruct
    public void onInitialize() {
        EntitySync.instance().setEntityManagerFactory(mEntityManagerFactory);

        // Start when broker is started
        ActiveMQBroker.instance().getListenerEventDispatcher().add(mBrokerListener);
        if (ActiveMQBroker.instance().isStarted())
            mBrokerListener.onStart();
    }
}
