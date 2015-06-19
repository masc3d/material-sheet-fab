package org.deku.leo2.node.data.sync;

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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import sx.jms.SpringJmsListener;

import javax.inject.Named;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by masc on 18.06.15.
 */
@Named
public class EntityPublisher extends SpringJmsListener {
    private Log mLog = LogFactory.getLog(this.getClass());
    /** Messaging context */
    private MessagingContext mMessagingContext;
    /** Entity manager factory */
    private EntityManagerFactory mEntityManagerFactory;
    /** Message converter */
    private SimpleMessageConverter mMessageConverter = new SimpleMessageConverter();
    /** Jms template */
    private JmsTemplate mTemplate;

    /**
     * c'tor
     * @param messagingContext
     * @param entityManagerFactory
     */
    public EntityPublisher(MessagingContext messagingContext, EntityManagerFactory entityManagerFactory) {
        super(messagingContext.getConnectionFactory());
        mMessagingContext = messagingContext;
        mEntityManagerFactory = entityManagerFactory;
        mTemplate = new JmsTemplate(messagingContext.getConnectionFactory());
    }

    @Override
    protected Destination createDestination() {
        return mMessagingContext.createQueue(EntityStateMessage.ENTITY_QUEUE_NAME);
    }

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        mLog.debug(String.format("message id [%s] %s",
                message.getJMSMessageID(),
                LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(
                                message.getJMSTimestamp()), ZoneId.systemDefault())));

        // Entity state message
        EntityStateMessage esMessage = (EntityStateMessage) mMessageConverter.fromMessage(message);
        Class entityType = esMessage.getEntityType();

        EntityManager em = mEntityManagerFactory.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // Query count
        CriteriaQuery<Long> cqCount = cb.createQuery(Long.class);
        cqCount.select(cb.count(cqCount.from(entityType)));
        Long count = em.createQuery(cqCount).getSingleResult();

        // Query entities
        CriteriaQuery cq = cb.createQuery(entityType);
        cq.select(cq.from(entityType));

        Query jq = em.createQuery(cq)
                // Eclipselink specific hints for enabling cursor support, will change result of query to cursor
                .setHint(QueryHints.RESULT_SET_TYPE, ResultSetType.ForwardOnly)
                .setHint(QueryHints.SCROLLABLE_CURSOR, true);

        ScrollableCursor cursor = (ScrollableCursor) jq.getSingleResult();
        try {
            mTemplate.convertAndSend(message.getJMSReplyTo(), new EntityUpdateMessage(count));

            final int CHUNK_SIZE = 100;
            ArrayList buffer = new ArrayList();
            mLog.debug("sending");
            while (cursor.hasNext()) {
                buffer.add(cursor.next());
                if (buffer.size() >= CHUNK_SIZE) {
                    mTemplate.convertAndSend(message.getJMSReplyTo(), buffer.toArray());
                    buffer.clear();
                }
            }

            // Send empty array -> EOS
            mTemplate.convertAndSend(message.getJMSReplyTo(), new Object[0]);

            mLog.debug("done");
        } catch (InvalidDestinationException e) {
            mLog.warn("Destination invalid, removed");
        }
        em.close();
    }
}
