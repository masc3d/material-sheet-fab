package org.deku.leo2.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.failover.FailoverTransport;
import org.apache.activemq.transport.failover.FailoverTransportFactory;
import org.deku.leo2.messaging.log.LogListener;
import org.deku.leo2.messaging.log.LogProducer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import java.io.IOException;

/**
 * Created by masc on 16.04.15.
 */
public class LogTest {
    @Before
    public void setup() throws Exception {
        Broker.getInstance().start();
        System.out.println("intialized");
    }

    @After
    public void tearDown() throws Exception {
        Broker.getInstance().stop();
    }

    @Test
    public void testSend() throws JMSException {
        LogProducer lp = new LogProducer( new ActiveMQConnectionFactory("http://localhost:8080") );

        for (int i = 0; i < 100; i++)
            lp.send("Test!");
    }

    @Test
    public void testReceive() throws JMSException, InterruptedException {
        LogListener mListener = new LogListener( new ActiveMQConnectionFactory("http://localhost:8080") );
        mListener.start();

        Thread.sleep(2000);

        mListener.stop();
    }
}

