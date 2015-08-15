package org.deku.leoz.messaging.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.messaging.MessagingContext;
import org.deku.leoz.messaging.log.v1.LogMessage;
import sx.jms.Handler;
import sx.jms.listeners.SpringJmsListener;
import sx.jms.converters.DefaultConverter;

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

        this.setConverter(new DefaultConverter(
                DefaultConverter.SerializationType.KRYO,
                DefaultConverter.CompressionType.GZIP));

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