package org.deku.leo2.messaging;

import sx.jms.embedded.Broker;

import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Generic messaging context interface definition
 * Created by masc on 07.06.15.
 */
public interface MessagingContext {
    /** Broker for this messaging context */
    Broker getBroker();

    /** Central message queue for lightweight message exchange */
    Queue getCentralQueue();
    /** Central message queue for entity sync */
    Queue getCentralEntitySyncQueue();
    /** Central message queue for logs */
    Queue  getCentralLogQueue();
    /** Node message queue for lightweight message exchange*/
    Queue  getNodeQueue(Integer id);
    /** Node notification topic for broadcastss */
    Topic getNodeNotificationTopic();
}
