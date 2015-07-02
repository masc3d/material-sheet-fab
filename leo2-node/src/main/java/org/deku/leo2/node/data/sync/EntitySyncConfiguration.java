package org.deku.leo2.node.data.sync;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.node.App;
import org.deku.leo2.node.data.PersistenceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import sx.jms.embedded.Broker;
import sx.jms.embedded.activemq.ActiveMQBroker;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Created by masc on 20.06.15.
 */
@Configuration(App.PROFILE_CLIENT_NODE)
@Profile( { App.PROFILE_CLIENT_NODE} )
@Lazy(false)
public class EntitySyncConfiguration {
    private Log mLog = LogFactory.getLog(this.getClass());

    @PersistenceUnit(name = PersistenceConfiguration.DB_EMBEDDED)
    EntityManagerFactory mEntityManagerFactory;

    /** Broker listener */
    private Broker.EventListener mBrokerEventListener = new Broker.EventListener() {
        @Override
        public void onStart() {
            mLog.info("Detected broker start, initializing entity sync");
            EntitySync.instance().start();
        }

        @Override
        public void onStop() {
            EntitySync.instance().stop();
        }
    };

    @PostConstruct
    public void onInitialize() {
        // Configure entity snyc
        EntitySync.instance().setEntityManagerFactory(mEntityManagerFactory);

        // Start when broker is started
        ActiveMQBroker.instance().getDelegate().add(mBrokerEventListener);
        if (ActiveMQBroker.instance().isStarted())
            mBrokerEventListener.onStart();
    }

    @PreDestroy
    public void onDestroy() {
    }
}
