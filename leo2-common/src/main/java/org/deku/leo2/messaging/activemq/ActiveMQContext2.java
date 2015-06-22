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
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Messaging context implementation for activemq
 * Created by masc on 16.04.15.
 */
public class ActiveMQContext2 implements MessagingContext {
    /** Singleton instance */
    private static LazyInstance<ActiveMQContext2> mContext = new LazyInstance<>(ActiveMQContext2::new);

    /** Url for establishing connection to local/embedded broker */
    private URI mLocalUri;

    /** Connection factory for connecting to the embedded broker */
    private ConnectionFactory mConnectionFactory;

    public ActiveMQContext2() {
        try {
            mLocalUri = new URI("vm://localhost?create=false");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static ActiveMQContext2 instance() {
        return mContext.get();
    }

    @Override
    public Broker getBroker() {
        return ActiveMQBroker2.instance();
    }

    @Override
    public Queue createQueue(String name) {
        return new ActiveMQQueue(name);
    }

    @Override
    public Topic createTopic(String name) { return new ActiveMQTopic(name); }

    @Override
    public ConnectionFactory getConnectionFactory() {
        if (mConnectionFactory == null) {
            PooledConnectionFactory psf = new PooledConnectionFactory();
            psf.setConnectionFactory(new ActiveMQConnectionFactory(
                    Broker.USERNAME,
                    Broker.PASSWORD,
                    // Explicitly do _not_ create (another) embedded broker on connection, just in case
                    mLocalUri.toString()));
            mConnectionFactory = psf;
        }
        return mConnectionFactory;
    }

    /**
     * For (performance) testing purposes: override local/embedded connection URI.
     * Must be called before connection factory is retrieved for the first time.
     * @param uri
     */
    public void setLocalUri(URI uri) {
        mLocalUri = uri;
    }

}
