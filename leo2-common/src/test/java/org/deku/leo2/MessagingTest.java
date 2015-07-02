package org.deku.leo2;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.junit.After;
import org.junit.Before;
import sx.jms.embedded.Broker;
import sx.jms.embedded.activemq.ActiveMQBroker;

/**
 * Created by masc on 16.06.15.
 */
public abstract class MessagingTest {
    private String mHttpUrl = "http://localhost:8080/leo2/jms";
    //private String mNativeUrl = "tcp://localhost:61616";
    private String mNativeUrl = "vm://localhost?create=false";

    private Broker mBroker;

    @Before
    public void setup() throws Exception {
        // Log levels
        Logger root = (Logger) org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        Logger lMessaging = (Logger)org.slf4j.LoggerFactory.getLogger("org.deku.leo2.messaging");
        lMessaging.setLevel(Level.TRACE);

        // Start broker
        mBroker = ActiveMQBroker.instance();
        mBroker.start();
    }

    @After
    public void tearDown() throws Exception {
        mBroker.stop();
    }
}
