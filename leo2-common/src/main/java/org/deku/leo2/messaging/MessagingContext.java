package org.deku.leo2.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Messaging context interface definition
 * Created by masc on 07.06.15.
 */
public interface MessagingContext {
    /** Broker for this messaging context */
    Broker getBroker();
    /** Create jms message queue */
    Queue createQueue(String name);
    /** Create jms message topic */
    Topic createTopic(String name);
    /** Jms connection factory */
    ConnectionFactory getConnectionFactory();
}
