package org.deku.leoz.log;

import ch.qos.logback.classic.Logger;
import org.apache.commons.logging.Log;
import org.deku.leoz.Identity;
import org.deku.leoz.MessagingTest;
import org.deku.leoz.SystemInformation;
import org.deku.leoz.bundle.Bundles;
import org.deku.leoz.config.messaging.ActiveMQConfiguration;
import org.jetbrains.annotations.Nullable;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import sx.jms.Channel;
import sx.jms.Handler;
import sx.jms.listeners.SpringJmsListener;

import javax.jms.JMSException;
import java.util.concurrent.Executors;

/**
 * @author masc
 */
@Ignore
public class LogTest extends MessagingTest {
    private Log mLog = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testSend() throws JMSException {
        // Setup log appender
        LogAppender lAppender = new LogAppender(
                ActiveMQConfiguration.getInstance(),
                () -> {
                    return Identity.Companion.create(Bundles.LEOZ_NODE.getValue(), SystemInformation.create());
                });
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
        SpringJmsListener mListener = new SpringJmsListener(
                () -> new Channel(ActiveMQConfiguration.getInstance().getCentralLogQueue()),
                Executors.newSingleThreadExecutor() ) {
        };

        mListener.addDelegate(LogMessage.class, new Handler<LogMessage>() {
            @Override
            public void onMessage(LogMessage message, @Nullable Channel replyChannel) {
                mLog.info(message);
            }
        });

        mListener.start();

        // Wait for some messages to be received
        Thread.sleep(20000);

        mListener.close();
    }
}

