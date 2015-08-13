package org.deku.leoz.node.data.sync;

import com.google.common.base.Stopwatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.messaging.MessagingContext;
import org.deku.leoz.messaging.activemq.ActiveMQContext;
import org.deku.leoz.node.data.sync.v1.EntityStateMessage;
import org.deku.leoz.node.data.sync.v1.EntityUpdateMessage;
import org.eclipse.persistence.queries.ScrollableCursor;
import sx.jms.Channel;
import sx.jms.converters.DefaultConverter;
import sx.jms.listeners.SpringJmsListener;

import javax.jms.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by masc on 18.06.15.
 */
public class EntityPublisher extends SpringJmsListener {
    private Log mLog = LogFactory.getLog(this.getClass());
    /** Messaging context */
    private MessagingContext mMessagingContext;
    /** Entity manager factory */
    private EntityManagerFactory mEntityManagerFactory;
    /** Message converter */
    private DefaultConverter mConverter;

    /**
     * c'tor
     * @param messagingContext
     * @param entityManagerFactory
     */
    public EntityPublisher(MessagingContext messagingContext, EntityManagerFactory entityManagerFactory) {
        super(messagingContext.getBroker().getConnectionFactory());
        mMessagingContext = messagingContext;
        mEntityManagerFactory = entityManagerFactory;
        mConverter = this.createMessageConverter();

        this.setConverter(mConverter);
    }

    /**
     * Create message converter
     * @return
     */
    private DefaultConverter createMessageConverter() {
        return new DefaultConverter(
                DefaultConverter.SerializationType.KRYO,
                DefaultConverter.CompressionType.GZIP);
    }

    @Override
    protected Destination createDestination() {
        return mMessagingContext.getCentralEntitySyncQueue();
    }

    /**
     * Publish entity update notification
     * @param entityType
     * @param timestamp
     */
    public void publish(Class entityType, Timestamp timestamp) throws JMSException {
        Channel mc = new Channel(
                ActiveMQContext.instance().getBroker().getConnectionFactory(),
                ActiveMQContext.instance().getNodeNotificationTopic(),
                this.createMessageConverter(),
                false,
                DeliveryMode.NON_PERSISTENT,
                TimeUnit.MINUTES.toMillis(5));

        mc.send(new EntityStateMessage(entityType, timestamp));

        mc.close();
    }

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        mLog.debug(String.format("Message id [%s] %s",
                message.getJMSMessageID(),
                LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(
                                message.getJMSTimestamp()), ZoneId.systemDefault())));

        Stopwatch sw = Stopwatch.createStarted();

        // Create new message converter for this session, just for clean statistics sake
        DefaultConverter messageConverter = this.createMessageConverter();

        // Entity state message
        EntityStateMessage esMessage = (EntityStateMessage) messageConverter.fromMessage(message);
        Class entityType = esMessage.getEntityType();
        Timestamp timestamp = esMessage.getTimestamp();
        Function<String, String> lfmt = s -> "[" + entityType.getCanonicalName() + "]" + " " + s;


        EntityManager em = mEntityManagerFactory.createEntityManager();
        EntityRepository er = new EntityRepository(em, entityType);

        // Count records
        Long count = er.countNewerThan(timestamp);

        EntityUpdateMessage euMessage = new EntityUpdateMessage(count);
        mLog.debug(euMessage);

        MessageProducer mp = session.createProducer(message.getJMSReplyTo());
        mp.setPriority(1);
        mp.setTimeToLive(TimeUnit.MINUTES.toMillis(1));
        mp.send(messageConverter.toMessage(euMessage, session));

        if (count > 0) {
            // Query with cursor
            ScrollableCursor cursor = er.findNewerThan(timestamp);

            final int CHUNK_SIZE = 500;
            ArrayList buffer = new ArrayList(CHUNK_SIZE);
            mLog.info(lfmt.apply(String.format("Sending %d", count)));
            while (true) {
                Object next = null;
                if (cursor.hasNext()) {
                    next = cursor.next();
                    buffer.add(next);
                }
                if (buffer.size() >= CHUNK_SIZE || next == null) {
                    if (buffer.size() > 0) {
                        //mLog.debug(String.format("Sending %d", buffer.size()));
                        mp.send(messageConverter.toMessage(buffer.toArray(),session));
                        buffer.clear();
                    }

                    if (next == null)
                        break;
                }
            }

            // Send empty array -> EOS
            Message eosMsg = messageConverter.toMessage(new Object[0], session);
            eosMsg.setBooleanProperty(EntityUpdateMessage.EOS_PROPERTY, true);
                    mp.send(eosMsg);
        }
        session.commit();
        mLog.info(lfmt.apply(String.format("Sent %d in %s (%d bytes)", count, sw, messageConverter.getBytesWritten())));

        em.close();
    }
}
