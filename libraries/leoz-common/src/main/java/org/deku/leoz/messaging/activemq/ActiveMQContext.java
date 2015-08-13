package org.deku.leoz.messaging.activemq;

import org.deku.leoz.messaging.MessagingContext;
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
        return this.getBroker().createQueue("leoz.central");
    }

    @Override
    public Queue getCentralEntitySyncQueue() {
        return this.getBroker().createQueue("leoz.entity-sync");
    }

    @Override
    public Queue getCentralLogQueue() {
        return this.getBroker().createQueue("leoz.log");
    }

    @Override
    public Queue getNodeQueue(Integer id) {
        return this.getBroker().createQueue("leoz.node." + id.toString());
    }

    @Override
    public Topic getNodeNotificationTopic() {
        return this.getBroker().createTopic("leoz.notifications");
    }

}
