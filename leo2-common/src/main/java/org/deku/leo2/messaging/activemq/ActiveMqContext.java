package org.deku.leo2.messaging.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.deku.leo2.messaging.Broker;
import org.deku.leo2.messaging.MessagingContext;
import sx.LazyInstance;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Messaging context implementation for activemq
 * Created by masc on 16.04.15.
 */
public class ActiveMqContext implements MessagingContext {
    /** Singleton instance */
    private static LazyInstance<ActiveMqContext> mContext = new LazyInstance<>(ActiveMqContext::new);

    /** Connection factory for connecting to the embedded broker */
    private ConnectionFactory mConnectionFactory;

    public ActiveMqContext() {
        PooledConnectionFactory psf = new PooledConnectionFactory();
        psf.setConnectionFactory(new ActiveMQConnectionFactory(
                Broker.USERNAME,
                Broker.PASSWORD,
                // Explicitly do _not_ create (another) embedded broker on connection, just in case
                "vm://localhost?create=false"));
        mConnectionFactory = psf;
    }

    public static ActiveMqContext instance() {
        return mContext.get();
    }

    @Override
    public Broker getBroker() {
        return ActiveMqBroker.instance();
    }

    @Override
    public Queue createQueue(String name) {
        return new ActiveMQQueue(name);
    }

    @Override
    public Topic createTopic(String name) { return new ActiveMQTopic(name); }

    @Override
    public ConnectionFactory getConnectionFactory() {
        return mConnectionFactory;
    }

}
