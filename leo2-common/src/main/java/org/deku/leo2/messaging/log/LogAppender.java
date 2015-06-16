package org.deku.leo2.messaging.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.Broker;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.messaging.log.v1.LogMessage;
import org.springframework.jms.core.JmsTemplate;
import sx.Disposable;

import javax.jms.Queue;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Log appender sending log messages via jms
 * Created by masc on 11.06.15.
 */
public class LogAppender extends AppenderBase<ILoggingEvent> implements  Disposable {
    private Log mLog = LogFactory.getLog(this.getClass());

    private MessagingContext mMessagingContext;
    /** Jms destination queue */
    private Queue mQueue = null;
    /** Spring jms communication abstraction */
    private JmsTemplate mTemplate;
    /** Log message buffer */
    private ArrayList<LogMessage> mLogMessageBuffer = new ArrayList<>();
    /** Flush scheduler */
    private ScheduledExecutorService mScheduledExecutorService;

    /**
     * Broker listener, jms destination is automatically created when broker start is detected
     */
    Broker.Listener mBrokerListener = new Broker.Listener() {
        @Override
        public void onStart() {
            mTemplate.execute(session -> {
                mQueue = session.createQueue(LogMessage.LOG_QUEUE_NAME);
                return null;
            });
            mScheduledExecutorService.scheduleAtFixedRate(
                    () -> flush(),
                    0,
                    5,
                    TimeUnit.SECONDS);
        }
    };

    /**
     * c'tor
     * @param messagingContext
     */
    public LogAppender(MessagingContext messagingContext) {
        mMessagingContext = messagingContext;

        mScheduledExecutorService = Executors.newScheduledThreadPool(1);

        mTemplate = new JmsTemplate(mMessagingContext.getConnectionFactory());
        mTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        mTemplate.setSessionTransacted(true);

        mMessagingContext.getBroker().getListenerEventDispatcher().add(mBrokerListener);
        if (mMessagingContext.getBroker().isStarted())
            mBrokerListener.onStart();
    }

    /**
     * Flush log messages to underlying jms broker
     */
    private void flush() {
        ArrayList<LogMessage> logMessageBuffer;

        synchronized(mLogMessageBuffer) {
            logMessageBuffer = new ArrayList(mLogMessageBuffer);
            mLogMessageBuffer.clear();
        }

        if (logMessageBuffer.size() > 0) {
            mLog.debug(String.format("Flushing [%d]", logMessageBuffer.size()));
            try {
                mTemplate.convertAndSend(mQueue, logMessageBuffer.toArray(new LogMessage[0]));
            } catch(Exception e) {
                // TODO: investigate why exceptions go unnoticed silently and are not
                // caught by global DefaultUncaughtExceptionHandler
                mLog.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        LoggingEvent le = (LoggingEvent)eventObject;
        synchronized(mLogMessageBuffer) {
            mLogMessageBuffer.add(new LogMessage(le));
        }
    }

    @Override
    public void dispose() {
        if (mScheduledExecutorService.isShutdown())
            return;

        this.stop();

        // Immediate flush and subsequent shutdown
        mScheduledExecutorService.schedule(() -> flush(), 0, TimeUnit.SECONDS);
        mScheduledExecutorService.shutdown();

        // Wait for termination
        try {
            mScheduledExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
