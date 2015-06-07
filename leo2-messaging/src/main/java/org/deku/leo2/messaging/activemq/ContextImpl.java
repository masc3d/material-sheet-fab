package org.deku.leo2.messaging.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.deku.leo2.messaging.Broker;
import org.deku.leo2.messaging.Context;
import sx.LazyInstance;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Messaging context implementation for activemq
 * Created by masc on 16.04.15.
 */
public class ContextImpl implements Context {
    private static LazyInstance<ContextImpl> mContext = new LazyInstance<>(ContextImpl::new);

    private LazyInstance<BrokerImpl> mBroker = new LazyInstance<>(BrokerImpl::new);
    private ConnectionFactory mConnectionFactory;

    public ContextImpl() {
        PooledConnectionFactory psf = new PooledConnectionFactory();
        psf.setConnectionFactory(new ActiveMQConnectionFactory(Broker.USERNAME, Broker.PASSWORD, "vm://" + Broker.NAME));
        mConnectionFactory = psf;
    }

    public static ContextImpl instance() {
        return mContext.get();
    }

    @Override
    public BrokerImpl getBroker() {
        return mBroker.get();
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
