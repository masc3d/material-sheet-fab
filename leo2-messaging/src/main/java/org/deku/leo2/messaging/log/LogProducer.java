package org.deku.leo2.messaging.log;

import org.deku.leo2.messaging.Context;

import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import sx.jms.Producer;

/**
 * Leo2 log message producer
 * Created by masc on 16.04.15.
 */
public class LogProducer extends Producer {
    Session mSession;
    Queue mQueue;
    MessageProducer mProducer;

    public LogProducer(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    protected Session createSession() throws JMSException {
        return this.getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    protected Destination createDestination() throws JMSException {
        return this.getSession().createQueue(org.deku.leo2.messaging.log.v1.LogMessage.LOG_QUEUE_NAME);
    }

    public synchronized void send(String message) throws JMSException {
        ObjectMessage om = this.getSession().createObjectMessage();
        om.setObject(new org.deku.leo2.messaging.log.v1.LogMessage(new LogRecord(Level.INFO, message)));

        this.getProducer().send(om);
    }
}
