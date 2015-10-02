package org.deku.leoz.central.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.central.App;
import org.deku.leoz.central.data.sync.DatabaseSync;
import org.deku.leoz.central.data.sync.EntitySync;
import org.deku.leoz.node.config.PersistenceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import sx.jms.embedded.Broker;
import sx.jms.embedded.activemq.ActiveMQBroker;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
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

    @Inject
    DatabaseSync mDatabaseSync;

    /** Broker listener */
    private Broker.EventListener mBrokerEventListener = new Broker.EventListener() {
        @Override
        public void onStart() {
            EntitySync.it().start();
        }

        @Override
        public void onStop() {
            EntitySync.it().dispose();
        }
    };

    @PostConstruct
    public void onInitialize() {
        EntitySync.it().setEntityManagerFactory(mEntityManagerFactory);
        EntitySync.it().setDatabaseSync(mDatabaseSync);

        // Start when broker is started
        ActiveMQBroker.instance().getDelegate().add(mBrokerEventListener);
        if (ActiveMQBroker.instance().isStarted())
            mBrokerEventListener.onStart();
    }
}
