package org.deku.leoz.node.data.sync;

import org.deku.leoz.messaging.activemq.ActiveMQContext;
import org.deku.leoz.node.data.entities.master.*;
import sx.Disposable;
import sx.LazyInstance;

import javax.persistence.EntityManagerFactory;

/**
 * Application/leoz specific entity sync implementation
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
        mEntityConsumer.start();
        mEntityConsumer.request(Station.class);
        mEntityConsumer.request(Country.class);
        mEntityConsumer.request(HolidayCtrl.class);
        mEntityConsumer.request(Route.class);
        mEntityConsumer.request(Sector.class);
    }

    public void stop() {
        if (mEntityConsumer != null) {
            mEntityConsumer.dispose();
            mEntityConsumer = null;
        }
    }

    @Override
    public void dispose() {
        this.stop();
    }
}
