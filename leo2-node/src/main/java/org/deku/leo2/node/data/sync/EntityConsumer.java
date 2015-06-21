package org.deku.leo2.node.data.sync;

import com.google.common.base.Stopwatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.node.data.PersistenceUtil;
import org.deku.leo2.node.data.sync.v1.EntityStateMessage;
import org.deku.leo2.node.data.sync.v1.EntityUpdateMessage;
import org.junit.rules.Timeout;
import org.springframework.jms.core.JmsTemplate;
import sx.Disposable;

import javax.jms.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.lang.IllegalStateException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by masc on 18.06.15.
 */
public class EntityConsumer implements Disposable {
    private Log mLog = LogFactory.getLog(this.getClass());

    private static final int RECEIVE_TIMEOUT = 5000;
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
                mObjectMessageConverter.resetStatistics();

                EntityManager em = mEntityManagerFactory.createEntityManager();
                EntityRepository er = new EntityRepository(em, entityType);

                Timestamp timestamp = er.findMaxTimestamp();

                Connection cn = mMessagingContext.getConnectionFactory().createConnection();
                cn.start();
                Session session = cn.createSession(false, Session.AUTO_ACKNOWLEDGE);

                Stopwatch sw = Stopwatch.createStarted();

                Queue requestQueue = session.createQueue(EntityStateMessage.ENTITY_QUEUE_NAME);
                TemporaryQueue receiveQueue = session.createTemporaryQueue();

                mLog.info(String.format("Requesting entities of type [%s]", entityType.toString()));

                // Send entity state message
                MessageProducer mp = session.createProducer(requestQueue);
                mp.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                mp.setTimeToLive(5 * 60 * 1000);
                Message msg = mObjectMessageConverter.toMessage(
                        new EntityStateMessage(entityType, timestamp),
                        session);
                msg.setJMSReplyTo(receiveQueue);
                mp.send(msg);

                // Receive entity update message
                MessageConsumer mc = session.createConsumer(receiveQueue);
                msg = mc.receive(RECEIVE_TIMEOUT);
                if (msg == null)
                    throw new TimeoutException("Timeout while waiting for entity update message");
                EntityUpdateMessage euMessage = (EntityUpdateMessage) mObjectMessageConverter.fromMessage(msg);

                mLog.debug(euMessage);
                final long[] count = {0};

                if (euMessage.getAmount() > 0) {
                    PersistenceUtil.transaction(em, () -> {
                        if (!er.hasTimestampAttribute()) {
                            mLog.debug("No timestamp attribtue found -> removing all entities");
                            er.removeAll();
                        }
                        // Receive entities
                        List entities = null;
                        long lastJmsTimestamp = 0;
                        Message m;
                        do {
                            m = mc.receive(RECEIVE_TIMEOUT);
                            if (m == null)
                                throw new TimeoutException("Timeout while waiting for next entities chunk");

                            // Verify message order
                            if (m.getJMSTimestamp() < lastJmsTimestamp)
                                throw new IllegalStateException(
                                        String.format("Inconsistent message order (%d < %d)", m.getJMSTimestamp(), timestamp));

                            // Store last timestamp
                            lastJmsTimestamp = m.getJMSTimestamp();

                            // Deserialize entities
                            entities = Arrays.asList((Object[]) mObjectMessageConverter.fromMessage(m));

                            // Merge entities
                            if (timestamp != null) {
                                mLog.trace("Removing existing entities");
                                // If there's already entities, clean out existing first.
                                // it's much faster than merging everything
                                for (Object o : entities) {
                                    Object o2 = em.find(entityType,
                                            em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(o));

                                    if (o2 != null) {
                                        em.remove(o2);
                                    }
                                }
                                em.flush();
                                em.clear();
                            }

                            // Persist entities
                            for (Object o : entities) {
                                em.persist(o);
                            }
                            em.flush();
                            em.clear();

                            mLog.trace(String.format("Received %d entities", entities.size()));
                            count[0] += entities.size();
                        } while (entities.size() > 0);
                    });
                }
                mLog.info(String.format("Received and stored %d in %s (%d bytes)", count[0], sw.toString(), mObjectMessageConverter.getBytesRead()));

                em.close();
            } catch( TimeoutException e) {
                mLog.error(e.getMessage());
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
