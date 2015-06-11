package org.deku.leo2.messaging.log;

import org.deku.leo2.messaging.Context;
import org.deku.leo2.messaging.log.v1.LogMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Leo2 log message producer
 * Created by masc on 16.04.15.
 */
public class LogProducer /*extends Producer*/ {
    Queue mQueue;
    Context mContext;
    JmsTemplate mTemplate;

    public LogProducer(Context context) {
        mContext = context;

        //super(connectionFactory);
        mTemplate = new JmsTemplate(mContext.getConnectionFactory());
        mTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        mTemplate.setSessionTransacted(false);

        mQueue = mContext.createQueue(LogMessage.LOG_QUEUE_NAME);
    }

    public synchronized void send(String message) throws JMSException {
        mTemplate.send(mQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage om = session.createObjectMessage();
                om.setObject(new org.deku.leo2.messaging.log.v1.LogMessage(new LogRecord(Level.INFO, message)));
                return om;
            }
        });
    }
}
