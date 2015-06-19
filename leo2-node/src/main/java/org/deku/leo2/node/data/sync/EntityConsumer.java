package org.deku.leo2.node.data.sync;

import com.google.common.base.Stopwatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.node.data.sync.v1.EntityStateMessage;
import org.deku.leo2.node.data.sync.v1.EntityUpdateMessage;
import org.springframework.jms.core.JmsTemplate;
import sx.Disposable;

import javax.jms.DeliveryMode;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    /** Jms destination queue */
    private Queue mRequestQueue = null;
    private TemporaryQueue mReceiveQueue = null;
    /** Spring jms communication abstraction */
    private JmsTemplate mTemplate;

    ExecutorService mExecutorService;

    /**
     * c'tor
     * @param messagingContext
     */
    public EntityConsumer(MessagingContext messagingContext) {
        mMessagingContext = messagingContext;

        mTemplate = new JmsTemplate(mMessagingContext.getConnectionFactory());
        mTemplate.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        mTemplate.setSessionTransacted(true);

        // Create destinations
        mTemplate.execute(session -> {
            mRequestQueue = session.createQueue(EntityStateMessage.ENTITY_QUEUE_NAME);
            mReceiveQueue = session.createTemporaryQueue();
            return null;
        });

        mExecutorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Request entity update
     * @param entityType
     */
    public void request(Class entityType) {
        mExecutorService.submit(() -> {
            mTemplate.execute(session -> {
                mReceiveQueue = session.createTemporaryQueue();

                Stopwatch sw = Stopwatch.createStarted();
                mLog.info(String.format("Requesting entities of type [%s]", entityType.toString()));
                // Send entity state message
                mTemplate.convertAndSend(mRequestQueue,
                        new EntityStateMessage(entityType, null), message -> {
                            message.setJMSReplyTo(mReceiveQueue);
                            return message;
                        });

                try {
                    // Receive entity update message
                    EntityUpdateMessage euMessage = (EntityUpdateMessage)mTemplate.receiveAndConvert(mReceiveQueue);

                    // Receive entities
                    List entities;
                    Integer count = 0;
                    do {
                        entities = Arrays.asList((Object[])mTemplate.receiveAndConvert(mReceiveQueue));
                        count += entities.size();
                    } while (entities.size() > 0);
                    mLog.info(String.format("Received %d in %s", count, sw.toString()));
                } catch(Exception e) {
                    mLog.error(e.getMessage(), e);
                }

                return null;
            });
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
