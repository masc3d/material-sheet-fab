package org.deku.leo2.central.data.sync;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.central.App;
import sx.jms.embedded.Broker;
import sx.jms.embedded.activemq.ActiveMQBroker;
import org.deku.leo2.node.data.PersistenceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Created by masc on 20.06.15.
 */
@Configuration(App.PROFILE_CENTRAL)
@Lazy(false)
public class EntitySyncConfiguration {
    private Log mLog = LogFactory.getLog(this.getClass());

    @PersistenceUnit(name = PersistenceConfiguration.DB_EMBEDDED)
    EntityManagerFactory mEntityManagerFactory;

    /** Broker listener */
    private Broker.EventListener mBrokerEventListener = new Broker.EventListener() {
        @Override
        public void onStart() {
            EntitySync.instance().start();
        }

        @Override
        public void onStop() {
            EntitySync.instance().dispose();
        }
    };

    @PostConstruct
    public void onInitialize() {
        EntitySync.instance().setEntityManagerFactory(mEntityManagerFactory);

        // Start when broker is started
        ActiveMQBroker.instance().getDelegate().add(mBrokerEventListener);
        if (ActiveMQBroker.instance().isStarted())
            mBrokerEventListener.onStart();
    }
}
