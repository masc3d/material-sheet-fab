package org.deku.leo2.messaging.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.messaging.log.v1.LogMessage;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import sx.jms.SpringJmsListener;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Leo2 log message listener
 * Created by masc on 16.04.15.
 */
public class LogListener extends SpringJmsListener {
    private Log mLog = LogFactory.getLog(this.getClass());
    private MessagingContext mMessagingContext;
    private SimpleMessageConverter mMessageConverter = new SimpleMessageConverter();

    public LogListener(MessagingContext messagingContext) {
        super(messagingContext.getBroker().getConnectionFactory());
        mMessagingContext = messagingContext;
    }

    @Override
    protected Destination createDestination() {
        return mMessagingContext.getCentralLogQueue();
    }

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        try {
            mLog.trace(String.format("message id [%s] %s",
                    message.getJMSMessageID(),
                    LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(
                                    message.getJMSTimestamp()), ZoneId.systemDefault())));

            LogMessage[] cMessage = (LogMessage[])mMessageConverter.fromMessage(message);

            mLog.trace(String.format("Received %d log messages", cMessage.length));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}