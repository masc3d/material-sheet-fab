package org.deku.leo2.node.data.sync;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterators;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.node.data.entities.Station;
import org.deku.leo2.node.data.sync.v1.EntityStateMessage;
import org.deku.leo2.node.data.sync.v1.EntityUpdateMessage;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultSetType;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.springframework.jms.InvalidDestinationException;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import sx.jms.SpringJmsListener;

import javax.inject.Named;
import javax.jms.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by masc on 18.06.15.
 */
public class EntityPublisher extends SpringJmsListener {
    private Log mLog = LogFactory.getLog(this.getClass());
    /** Messaging context */
    private MessagingContext mMessagingContext;
    /** Entity manager factory */
    private EntityManagerFactory mEntityManagerFactory;

    /**
     * c'tor
     * @param messagingContext
     * @param entityManagerFactory
     */
    public EntityPublisher(MessagingContext messagingContext, EntityManagerFactory entityManagerFactory) {
        super(messagingContext.getConnectionFactory());
        mMessagingContext = messagingContext;
        mEntityManagerFactory = entityManagerFactory;
    }

    @Override
    protected Destination createDestination() {
        return mMessagingContext.createQueue(EntityStateMessage.ENTITY_QUEUE_NAME);
    }

    @Override
    protected void configure(DefaultMessageListenerContainer listenerContainer) {
        listenerContainer.setMessageConverter(new ObjectMessageConverter());
        super.configure(listenerContainer);
    }

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        mLog.debug(String.format("Message id [%s] %s",
                message.getJMSMessageID(),
                LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(
                                message.getJMSTimestamp()), ZoneId.systemDefault())));

        Stopwatch sw = Stopwatch.createStarted();

        ObjectMessageConverter messageConverter = new ObjectMessageConverter();

        // Entity state message
        EntityStateMessage esMessage = (EntityStateMessage) messageConverter.fromMessage(message);
        Class entityType = esMessage.getEntityType();
        Timestamp timestamp = esMessage.getTimestamp();

        EntityManager em = mEntityManagerFactory.createEntityManager();
        EntityRepository er = new EntityRepository(em, entityType);

        // Count records
        Long count = er.countNewerThan(timestamp);

        // Query with cursor
        ScrollableCursor cursor = er.findNewerThan(timestamp);

        try {
            EntityUpdateMessage euMessage = new EntityUpdateMessage(count);
            mLog.debug(euMessage);

            MessageProducer mp = session.createProducer(message.getJMSReplyTo());
            mp.send(messageConverter.toMessage(euMessage, session));

            final int CHUNK_SIZE = 500;
            ArrayList buffer = new ArrayList(CHUNK_SIZE);
            mLog.info(String.format("Sending %d of type %s", count, entityType));
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
            mp.send(messageConverter.toMessage(new Object[0], session));

            mLog.info(String.format("Sent %d in %s (%d)", count, sw, messageConverter.getBytesWritten()));
        } catch (InvalidDestinationException e) {
            mLog.warn("Destination invalid, removed");
        }
        em.close();
    }
}
