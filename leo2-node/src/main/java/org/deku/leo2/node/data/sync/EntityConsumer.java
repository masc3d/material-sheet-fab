package org.deku.leo2.node.data.sync;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.node.data.PersistenceUtil;
import org.deku.leo2.node.data.sync.v1.EntityStateMessage;
import org.deku.leo2.node.data.sync.v1.EntityUpdateMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import sx.Disposable;

import javax.jms.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by masc on 18.06.15.
 */
public class EntityConsumer implements Disposable {
    private Log mLog = LogFactory.getLog(this.getClass());
    /** Messaging context */
    private MessagingContext mMessagingContext;
    /** Entity manager factory */
    private EntityManagerFactory mEntityManagerFactory;
    /** Spring jms communication abstraction */
    private JmsTemplate mTemplate;
    private ObjectMessageConverter mObjectMessageConverter = new ObjectMessageConverter();

    ExecutorService mExecutorService;

    /**
     * c'tor
     * @param messagingContext
     */
    public EntityConsumer(MessagingContext messagingContext, EntityManagerFactory entityManagerFactory) {
        mMessagingContext = messagingContext;
        mEntityManagerFactory = entityManagerFactory;
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Request entity update
     * @param entityType
     */
    public void request(Class entityType) {
        mExecutorService.submit(() -> {
            try {
                Connection cn = mMessagingContext.getConnectionFactory().createConnection();
                cn.start();
                Session session = cn.createSession(false, Session.AUTO_ACKNOWLEDGE);

                Queue requestQueue = session.createQueue(EntityStateMessage.ENTITY_QUEUE_NAME);
                TemporaryQueue receiveQueue = session.createTemporaryQueue();

                Stopwatch sw = Stopwatch.createStarted();
                mLog.info(String.format("Requesting entities of type [%s]", entityType.toString()));

                // Send entity state message
                MessageProducer mp = session.createProducer(requestQueue);
                mp.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                mp.setTimeToLive(5 * 60 * 1000);
                Message msg = mObjectMessageConverter.toMessage(new EntityStateMessage(entityType, null), session);
                msg.setJMSReplyTo(receiveQueue);
                mp.send(msg);

                // Receive entity update message
                MessageConsumer mc = session.createConsumer(receiveQueue);
                msg = mc.receive();
                EntityUpdateMessage euMessage = (EntityUpdateMessage)mObjectMessageConverter.fromMessage(msg);

                mLog.debug(euMessage);

                // Receive entities
                EntityManager em = mEntityManagerFactory.createEntityManager();
                PersistenceUtil.transaction(em, () -> {
                    List entities = null;
                    long count = 0;
                    long timestamp = 0;
                    Message m;
                    do {
                        m = mc.receive();
                        if (m.getJMSTimestamp() < timestamp)
                            mLog.warn(String.format("INCONSISTENT ORDER (%d < %d)", m.getJMSTimestamp(), timestamp));

                        timestamp = m.getJMSTimestamp();

                        entities = Arrays.asList((Object[]) mObjectMessageConverter.fromMessage(m));

                        // TODO: db insert. preliminary for testing, requires timestamp support to work properly
//                        for (Object o : entities) {
//                            em.persist(o);
//                        }

                        mLog.trace(String.format("Received %d entities", entities.size()));
                        count += entities.size();
                    } while (entities.size() > 0);
                    mLog.info(String.format("Received %d in %s (%d)", count, sw.toString(), mObjectMessageConverter.getBytesRead()));
                });
            } catch (Exception e) {
                mLog.error(e.getMessage(), e);
            }

            return null;
        });
    }

    @Override
    public void dispose() {
        mExecutorService.shutdown();
        try {
            mExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {

        }
    }
}
