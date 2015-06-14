package org.deku.leo2.messaging.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.messaging.log.v1.LogMessage;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import sx.jms.SpringJmsListener;

import javax.jms.*;
import java.util.ArrayList;

/**
 * Leo2 log message listener
 * Created by masc on 16.04.15.
 */
public class LogListener extends SpringJmsListener {
    private Log mLog = LogFactory.getLog(this.getClass());
    private MessagingContext mMessagingContext;

    public LogListener(MessagingContext messagingContext) {
        super(messagingContext.getConnectionFactory());
        mMessagingContext = messagingContext;
    }

    @Override
    protected Destination createDestination() {
        return mMessagingContext.createQueue(LogMessage.LOG_QUEUE_NAME);
    }

    @Override
    public void onMessage(Message message) {
        try {
            SimpleMessageConverter c = new SimpleMessageConverter();
            ArrayList<LogMessage> cMessage = (ArrayList<LogMessage>)c.fromMessage(message);
            //LogMessage[] cMessage = (LogMessage[])c.fromMessage(message);

            mLog.info(String.format("Received %d log messages", cMessage.size()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}