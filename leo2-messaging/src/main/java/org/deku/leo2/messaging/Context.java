package org.deku.leo2.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;

import javax.jms.ConnectionFactory;

/**
 * Created by masc on 16.04.15.
 */
public class Context {

    /**
     * Creates a leo2 jms connection factory
     * @param httpPort Optional http port. If null native connection will be used.
     */
    public static ConnectionFactory createConnectionFactory(String hostname, Integer httpPort) {
        PooledConnectionFactory pcf = new PooledConnectionFactory();
        pcf.setConnectionFactory(new ActiveMQConnectionFactory(Util.createActiveMqUri(hostname, httpPort, true)));
        return pcf;
    }
}
