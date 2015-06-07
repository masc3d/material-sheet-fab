package org.deku.leo2.messaging;

import org.deku.leo2.messaging.activemq.BrokerImpl;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Messaging context interface definition
 * Created by masc on 07.06.15.
 */
public interface Context {
    /** Embedded jms broker instance */
    BrokerImpl getBroker();
    /** Create jms message queue */
    Queue createQueue(String name);
    /** Create jms message topic */
    Topic createTopic(String name);
    /** Jms connection factory */
    ConnectionFactory getConnectionFactory();
}
