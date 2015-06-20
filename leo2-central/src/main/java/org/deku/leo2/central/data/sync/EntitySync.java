package org.deku.leo2.central.data.sync;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.node.data.PersistenceContext;
import org.deku.leo2.node.data.entities.Route;
import org.deku.leo2.node.data.sync.EntityPublisher;
import org.jboss.resteasy.client.core.extractors.EntityExtractorFactory;
import sx.Disposable;
import sx.LazyInstance;

import javax.inject.Named;
import javax.jms.JMSException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Supervising sync class
 * Created by masc on 19.06.15.
 */
public class EntitySync implements Disposable {
    Log mLog = LogFactory.getLog(this.getClass());
    private static final LazyInstance<EntitySync> mInstance = new LazyInstance(EntitySync::new);

    public static EntitySync instance() { return mInstance.get(); };

    private EntityPublisher mEntityPublisher;

    EntityManagerFactory mEntityManagerFactory;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        mEntityManagerFactory = entityManagerFactory;
    }

    private EntitySync() {
    }

    public void start() {
        mEntityPublisher = new EntityPublisher(ActiveMQContext.instance(), mEntityManagerFactory);
        mEntityPublisher.start();
    }

    @Override
    public void dispose() {
        mEntityPublisher.stop();
    }
}
