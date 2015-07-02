package org.deku.leo2.messaging.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.messaging.log.v1.LogMessage;
import sx.jms.Handler;
import sx.jms.SpringJmsListener;
import sx.jms.converters.DefaultMessageConverter;

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
public class LogListener extends SpringJmsListener implements Handler<LogMessage[]> {
    private Log mLog = LogFactory.getLog(this.getClass());
    private MessagingContext mMessagingContext;

    public LogListener(MessagingContext messagingContext) {
        super(messagingContext.getBroker().getConnectionFactory());
        mMessagingContext = messagingContext;

        this.setMessageConverter(new DefaultMessageConverter(
                DefaultMessageConverter.SerializationType.KRYO,
                DefaultMessageConverter.CompressionType.GZIP));

        this.addDelegate(LogMessage[].class, this);
    }

    @Override
    protected Destination createDestination() {
        return mMessagingContext.getCentralLogQueue();
    }

    @Override
    public void onMessage(LogMessage[] message, Message jmsMessage, Session session) throws JMSException {
        mLog.info(String.format("message id [%s] %s",
                jmsMessage.getJMSMessageID(),
                LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(
                                jmsMessage.getJMSTimestamp()), ZoneId.systemDefault())));
    }
}