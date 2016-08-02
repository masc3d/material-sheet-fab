package org.deku.leoz.node.messaging.entities.sync;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.deku.leoz.config.messaging.ActiveMQConfiguration;
import org.deku.leoz.node.DataTest;
import org.deku.leoz.node.config.PersistenceConfiguration;
import org.deku.leoz.node.data.entities.master.Route;
import org.deku.leoz.node.data.sync.EntityConsumer;
import org.deku.leoz.node.data.sync.EntityPublisher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.concurrent.Executors;

/**
 * Created by masc on 18.06.15.
 */
//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
public class EntitySyncTest extends DataTest {
    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    private EntityManagerFactory mEntityManagerFactory;

    private EntityPublisher mListener;
    private EntityConsumer mClient;

    @Before
    public void setup() throws Exception {
        Logger l = (Logger) LoggerFactory.getLogger("org.deku.leoz.node");
        l.setLevel(Level.DEBUG);

        // Enforcing tcp connection
        //ActiveMQContext.instance().getBroker().setLocalUri(new URI("tcp://localhost:61616"));

        // Starting broker
        ActiveMQConfiguration.getInstance().getBroker().start();

        mListener = new EntityPublisher(
                ActiveMQConfiguration.getInstance(),
                mEntityManagerFactory,
                Executors.newSingleThreadExecutor());

        mClient = new EntityConsumer(
                ActiveMQConfiguration.getInstance(),
                mEntityManagerFactory,
                Executors.newSingleThreadExecutor());
    }

    @After
    public void tearDown() {
        mClient.close();
        mListener.close();
        ActiveMQConfiguration.Companion.getInstance().getBroker().close();
    }

    @Test
    public void testSync() throws Exception {
        mListener.start();
        mClient.request(Route.class, null, true);

        Thread.sleep(10000);
    }
}
