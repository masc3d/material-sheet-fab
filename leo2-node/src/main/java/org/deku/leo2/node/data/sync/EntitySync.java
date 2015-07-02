package org.deku.leo2.node.data.sync;

import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.node.data.entities.master.*;
import sx.Disposable;
import sx.LazyInstance;

import javax.persistence.EntityManagerFactory;

/**
 * Supervising sync class
 * Created by masc on 19.06.15.
 */
public class EntitySync implements Disposable {
    private static final LazyInstance<EntitySync> mInstance = new LazyInstance(EntitySync::new);

    public static EntitySync instance() { return mInstance.get(); };

    private EntityConsumer mEntityConsumer;
    private EntityManagerFactory mEntityManagerFactory;

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        mEntityManagerFactory = entityManagerFactory;
    }

    /** c'tor */
    private EntitySync() { }

    public void start() {
        mEntityConsumer = new EntityConsumer(ActiveMQContext.instance(), mEntityManagerFactory);
        mEntityConsumer.request(Station.class);
        mEntityConsumer.request(Country.class);
        mEntityConsumer.request(HolidayCtrl.class);
        mEntityConsumer.request(Route.class);
        mEntityConsumer.request(Sector.class);
    }

    public void stop() {
        mEntityConsumer.dispose();
    }

    @Override
    public void dispose() {
        this.stop();
    }
}
