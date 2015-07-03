package org.deku.leo2.messaging.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.messaging.log.v1.LogMessage;
import sx.Disposable;
import sx.jms.MessageConverter;
import sx.jms.converters.DefaultMessageConverter;
import sx.jms.embedded.Broker;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
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
    /** Message converter */
    private MessageConverter mMessageConverter;
    /** Log message buffer */
    private ArrayList<LogMessage> mLogMessageBuffer = new ArrayList<>();
    /** Flush scheduler */
    private ScheduledExecutorService mScheduledExecutorService;

    /**
     * Broker listener, jms destination is automatically created when broker start is detected
     */
    Broker.EventListener mBrokerEventListener = new Broker.EventListener() {
        @Override
        public void onStart() {
            mScheduledExecutorService.scheduleAtFixedRate(
                    () -> flush(),
                    0,
                    5,
                    TimeUnit.SECONDS);
        }

        @Override
        public void onStop() {
            dispose();
        }
    };

    /**
     * c'tor
     * @param messagingContext
     */
    public LogAppender(MessagingContext messagingContext) {
        mMessagingContext = messagingContext;

        mMessageConverter = new DefaultMessageConverter(
                DefaultMessageConverter.SerializationType.KRYO,
                DefaultMessageConverter.CompressionType.GZIP);
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
            mLog.trace(String.format("Flushing [%d]", logMessageBuffer.size()));
            try {
                Connection cn = mMessagingContext.getBroker().getConnectionFactory().createConnection();
                cn.start();
                Session session = cn.createSession(true, Session.AUTO_ACKNOWLEDGE);

                MessageProducer mp = session.createProducer(mMessagingContext.getCentralLogQueue());
                mp.setDeliveryMode(DeliveryMode.PERSISTENT);
                // Log messages live a few days before they are purged by the broker
                mp.setTimeToLive(TimeUnit.DAYS.toMillis(2));
                mp.setPriority(1);
                mp.send(mMessageConverter.toMessage(
                        logMessageBuffer.toArray(new LogMessage[0]),
                        session
                ));

                session.commit();;
            } catch(Exception e) {
                mLog.error(e.getMessage(), e);
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
    public void start() {
        if (mScheduledExecutorService != null) {
            this.stop();
        }

        mScheduledExecutorService = Executors.newScheduledThreadPool(1);

        mMessagingContext.getBroker().getDelegate().add(mBrokerEventListener);
        if (mMessagingContext.getBroker().isStarted())
            mBrokerEventListener.onStart();

        super.start();
    }

    @Override
    public void stop() {
        if (mScheduledExecutorService != null) {
            // Immediate flush and subsequent shutdown
            mScheduledExecutorService.schedule(() -> flush(), 0, TimeUnit.SECONDS);
            mScheduledExecutorService.shutdown();

            // Wait for termination
            try {
                mScheduledExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            mScheduledExecutorService = null;
        }

        super.stop();
    }

    @Override
    public void dispose() {
        this.stop();
    }
}
