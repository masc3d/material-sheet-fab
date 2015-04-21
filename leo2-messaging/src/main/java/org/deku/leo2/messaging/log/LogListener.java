package org.deku.leo2.messaging.log;

import sx.jms.SimpleListener;

import javax.jms.*;
import java.util.Arrays;
import java.util.List;

/**
 * Leo2 log message listener
 * Created by masc on 16.04.15.
 */
public class LogListener extends SimpleListener {
    public LogListener(ConnectionFactory factory) {
        super(factory);
    }

    @Override
    protected Session createSession(Connection connection) throws JMSException {
        return connection.createSession(true, Session.SESSION_TRANSACTED);
    }

    @Override
    protected List<Destination> createDestinations(Session session) throws JMSException {
        return Arrays.asList(session.createQueue(
                        org.deku.leo2.messaging.log.v1.LogMessage.LOG_QUEUE_NAME));
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage om = (ObjectMessage) message;

                this.getLogger().info("object type: " + om.getObject().getClass().getName());
                org.deku.leo2.messaging.log.v1.LogMessage lm = (org.deku.leo2.messaging.log.v1.LogMessage) om.getObject();
                this.getLogger().info("object received: " + lm.toString());
            } else {
                TextMessage tm = (TextMessage) message;
                this.getLogger().info("text received: " + tm.getText());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
