package org.deku.leoz.messaging;

import ch.qos.logback.classic.Logger;
import org.deku.leoz.MessagingTest;
import org.deku.leoz.messaging.activemq.ActiveMQContext;
import org.deku.leoz.messaging.log.LogAppender;
import org.deku.leoz.messaging.log.LogListener;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;

/**
 * Created by masc on 16.04.15.
 */
@Ignore
public class LogTest extends MessagingTest {
    @Test
    public void testSend() throws JMSException {
        // Setup log appender
        LogAppender lAppender = new LogAppender(ActiveMQContext.getInstance());
        lAppender.start();
        Logger lRoot = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        lRoot.addAppender(lAppender);

        // Generate some log messages
        for (int i = 0; i < 100; i++)
            lRoot.info("Test!");

        // Dispose to make sure everything is flushed
        lAppender.dispose();
    }

    @Test
    public void testReceive() throws JMSException, InterruptedException {
        // Setup log message listener
        LogListener mListener = new LogListener(ActiveMQContext.getInstance());
        mListener.start();

        // Wait for some messages to be received
        Thread.sleep(20000);

        mListener.dispose();
    }
}

