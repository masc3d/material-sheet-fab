package org.deku.leoz.log;

import ch.qos.logback.classic.Logger;
import org.deku.leoz.Identity;
import org.deku.leoz.MessagingTest;
import org.deku.leoz.SystemInformation;
import org.deku.leoz.config.messaging.ActiveMQConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;

/**
 * @author masc
 */
@Ignore
public class LogTest extends MessagingTest {
    @Test
    public void testSend() throws JMSException {
        // Setup log appender
        LogAppender lAppender = new LogAppender(
                ActiveMQConfiguration.getInstance(),
                Identity.Companion.create(SystemInformation.create()));
        lAppender.start();
        Logger lRoot = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        lRoot.addAppender(lAppender);

        // Generate some log messages
        for (int i = 0; i < 100; i++)
            lRoot.info("Test!");

        // Dispose to make sure everything is flushed
        lAppender.close();
    }

    @Test
    public void testReceive() throws JMSException, InterruptedException {
        // Setup log message listener
        LogListener mListener = new LogListener(ActiveMQConfiguration.getInstance());
        mListener.start();

        // Wait for some messages to be received
        Thread.sleep(20000);

        mListener.close();
    }
}

