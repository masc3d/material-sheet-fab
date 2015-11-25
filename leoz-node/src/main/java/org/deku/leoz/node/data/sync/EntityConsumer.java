package org.deku.leoz.node.data.sync;

import com.google.common.base.Stopwatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.config.messaging.MessagingConfiguration;
import org.deku.leoz.node.data.PersistenceUtil;
import org.deku.leoz.node.data.repositories.EntityRepository;
import org.deku.leoz.node.data.sync.v1.EntityStateMessage;
import org.deku.leoz.node.data.sync.v1.EntityUpdateMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.jms.core.JmsTemplate;
import sx.jms.Converter;
import sx.jms.Handler;
import sx.jms.converters.DefaultConverter;
import sx.jms.listeners.SpringJmsListener;

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
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Entity consumer
 * Created by masc on 18.06.15.
 */
public class EntityConsumer extends SpringJmsListener implements Handler<EntityStateMessage> {
    private Log mLog = LogFactory.getLog(this.getClass());

    private static final int RECEIVE_TIMEOUT = 5000;
    /** Messaging context */
    private MessagingConfiguration mMessagingConfiguration;
    /** Entity manager factory */
    private EntityManagerFactory mEntityManagerFactory;
    /** Spring jms communication abstraction */
    private JmsTemplate mTemplate;
    private DefaultConverter mConverter = new DefaultConverter(
            DefaultConverter.SerializationType.KRYO,
            DefaultConverter.CompressionType.GZIP);

    ExecutorService mExecutorService;

    /**
     * c'tor
     * @param messagingConfiguration
     */
    public EntityConsumer(MessagingConfiguration messagingConfiguration, EntityManagerFactory entityManagerFactory) {
        super(
                messagingConfiguration.getBroker().getConnectionFactory(),
                messagingConfiguration::getNodeEntitySyncTopic,
                new DefaultConverter(DefaultConverter.SerializationType.KRYO, DefaultConverter.CompressionType.GZIP));

        this.addDelegate(EntityStateMessage.class, this);

        mMessagingConfiguration = messagingConfiguration;
        mEntityManagerFactory = entityManagerFactory;
        mExecutorService = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        });
    }

    @Override
    public void onMessage(EntityStateMessage message, @NotNull Converter converter, @NotNull Message jmsMessage, @NotNull Session session) {
        this.request(message.getEntityType(), message.getTimestamp());
    }

    /**
     * Request entity update
     * @param entityType Entity type
     * @param remoteTimestamp Optional remote timestamp, usually provided via notification
     */
    public void request(final Class entityType, final Timestamp remoteTimestamp) {
        mExecutorService.submit(() -> {
            // Log formatting with entity type
            Function<String, String> lfmt = s -> "[" + entityType.getCanonicalName() + "]" + " " + s;

            TemporaryQueue receiveQueue = null;
            try {
                mConverter.resetStatistics();

                EntityManager em = mEntityManagerFactory.createEntityManager();
                EntityRepository er = new EntityRepository(em, entityType);

                Timestamp timestamp = er.findMaxTimestamp();
                if (timestamp != null && remoteTimestamp != null && !remoteTimestamp.after(timestamp)) {
                    mLog.debug(lfmt.apply("Entities uptodate"));
                    return null;
                }

                Connection cn = mMessagingConfiguration.getBroker().getConnectionFactory().createConnection();
                cn.start();
                Session session = cn.createSession(false, Session.AUTO_ACKNOWLEDGE);

                Stopwatch sw = Stopwatch.createStarted();

                Queue requestQueue = mMessagingConfiguration.getCentralEntitySyncQueue();
                receiveQueue = session.createTemporaryQueue();

                mLog.info(lfmt.apply(String.format("Requesting entities")));

                // Send entity state message
                MessageProducer mp = session.createProducer(requestQueue);
                mp.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                mp.setTimeToLive(TimeUnit.MINUTES.toMillis(3));
                Message msg = mConverter.toMessage(
                        new EntityStateMessage(entityType, timestamp),
                        session);
                msg.setJMSReplyTo(receiveQueue);
                mp.send(msg);

                // Receive entity update message
                MessageConsumer mc = session.createConsumer(receiveQueue);
                msg = mc.receive(RECEIVE_TIMEOUT);
                if (msg == null)
                    throw new TimeoutException("Timeout while waiting for entity update message");
                EntityUpdateMessage euMessage = (EntityUpdateMessage) mConverter.fromMessage(msg);

                mLog.debug(lfmt.apply(euMessage.toString()));
                AtomicLong count = new AtomicLong();

                // Transaction processing thread pool
                ExecutorService executorService = Executors.newFixedThreadPool(
                        Runtime.getRuntime().availableProcessors());

                if (euMessage.getAmount() > 0) {
                    PersistenceUtil.transaction(em, () -> {
                        if (!er.hasTimestampAttribute()) {
                            mLog.debug(lfmt.apply("No timestamp attribute found -> removing all entities"));
                            er.removeAll();
                        }

                        // Receive entities
                        boolean eos = false;
                        long lastJmsTimestamp = 0;
                        do {
                            Message tMsg = mc.receive(RECEIVE_TIMEOUT);
                            if (tMsg == null)
                                throw new TimeoutException("Timeout while waiting for next entities chunk");

                            // Verify message order
                            if (tMsg.getJMSTimestamp() < lastJmsTimestamp)
                                throw new IllegalStateException(
                                        String.format("Inconsistent message order (%d < %d)", tMsg.getJMSTimestamp(), timestamp));

                            // Store last timestamp
                            lastJmsTimestamp = tMsg.getJMSTimestamp();

                            eos = tMsg.propertyExists(EntityUpdateMessage.EOS_PROPERTY);

                            if (!eos) {
                                // Deserialize entities
                                List entities = Arrays.asList((Object[]) mConverter.fromMessage(tMsg));

                                // TODO: exceptions within transactions behave in a strange way.
                                // data of transactions that were committed may not be there and h2 may report
                                // cache level state nio exceptions.
                                // Data seems to remain consistent though

                                if (timestamp != null) {
                                    mLog.trace(lfmt.apply("Removing existing entities"));
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
                                count.getAndUpdate(c -> c + entities.size());
                            }
                        } while (!eos);
                    });

                }
                mLog.trace(lfmt.apply("Joining transaction threads"));
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                mLog.info(lfmt.apply(String.format("Received and stored %d in %s (%d bytes)", count.get(), sw.toString(), mConverter.getBytesRead())));

                em.close();
            } catch (TimeoutException e) {
                mLog.error(lfmt.apply(e.getMessage()));
            } catch (Exception e) {
                mLog.error(lfmt.apply(e.getMessage()), e);
            }

            return null;
        });
    }

    /**
     * Request entity update
     * @param entityType Entity type
     */
    public void request(Class entityType) {
        this.request(entityType, null);
    }

    @Override
    public void close() {
        mExecutorService.shutdown();
        try {
            mExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Interruption is ok.
        }
        super.close();
    }
}
