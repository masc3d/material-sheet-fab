package org.deku.leo2.messaging.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.deku.leo2.messaging.Broker;
import org.deku.leo2.messaging.MessagingContext;
import sx.LazyInstance;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Messaging context implementation for activemq
 * Created by masc on 16.04.15.
 */
public class ActiveMQContext implements MessagingContext {
    /** Singleton instance */
    private static LazyInstance<ActiveMQContext> mContext = new LazyInstance<>(ActiveMQContext::new);


    public ActiveMQContext() {

    }

    public static ActiveMQContext instance() {
        return mContext.get();
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
