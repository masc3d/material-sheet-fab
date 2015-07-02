package org.deku.leo2.messaging.activemq;

import org.deku.leo2.messaging.MessagingContext;
import sx.jms.embedded.Broker;
import sx.jms.embedded.activemq.ActiveMQBroker;

import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Messaging context implementation for activemq
 * Created by masc on 16.04.15.
 */
public class ActiveMQContext implements MessagingContext {
    /** Singleton instance */
    private static final ActiveMQContext mInstance = new ActiveMQContext();

    private ActiveMQContext() { }

    public static ActiveMQContext instance() {
        return mInstance;
    }

    @Override
    public Broker getBroker() {
        return ActiveMQBroker.instance();
    }

    @Override
    public Queue getCentralQueue() {
        return this.getBroker().createQueue("leo2.central");
    }

    @Override
    public Queue getCentralEntitySyncQueue() {
        return this.getBroker().createQueue("leo2.entity-sync");
    }

    @Override
    public Queue getCentralLogQueue() {
        return this.getBroker().createQueue("leo2.log");
    }

    @Override
    public Queue getNodeQueue(Integer id) {
        return this.getBroker().createQueue("leo2.node." + id.toString());
    }

    @Override
    public Topic getNodeNotificationTopic() {
        return this.getBroker().createTopic("leo2.notifications");
    }

}
