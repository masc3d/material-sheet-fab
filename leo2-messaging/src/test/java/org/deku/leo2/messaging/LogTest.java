package org.deku.leo2.messaging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.deku.leo2.messaging.activemq.ActiveMqBroker;
import org.deku.leo2.messaging.activemq.ActiveMqContext;
import org.deku.leo2.messaging.log.LogListener;
import org.deku.leo2.messaging.log.LogProducer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.omg.PortableInterceptor.ACTIVE;

import javax.jms.JMSException;

/**
 * Created by masc on 16.04.15.
 */
public class LogTest {
    private String mHttpUrl = "http://localhost:8080/leo2/jms";
    //private String mNativeUrl = "tcp://localhost:61616";
    private String mNativeUrl = "vm://localhost?create=false";

    private Broker mBroker;

    @Before
    public void setup() throws Exception {
        Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        mBroker = ActiveMqBroker.instance();
        mBroker.start();
    }

    @After
    public void tearDown() throws Exception {
        mBroker.stop();
    }

    @Test
    public void testSend() throws JMSException {
        LogProducer lp = new LogProducer(ActiveMqContext.instance());

        for (int i = 0; i < 100; i++)
            lp.send("Test!");
    }

    @Test
    public void testReceive() throws JMSException, InterruptedException {
        LogListener mListener = new LogListener(ActiveMqContext.instance());
        mListener.start();

        Thread.sleep(20000);

        mListener.stop();
    }
}

