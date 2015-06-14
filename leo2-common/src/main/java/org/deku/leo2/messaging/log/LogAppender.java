package org.deku.leo2.messaging.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.Broker;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.messaging.log.v1.LogMessage;
import org.omg.CORBA.TIMEOUT;
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
    private Queue mQueue = null;
    private JmsTemplate mTemplate;
    private ArrayList<LogMessage> mLogEventBuffer = new ArrayList<>();

    private ScheduledExecutorService mScheduledExecutorService;

    Broker.Listener mBrokerListener = new Broker.Listener() {
        @Override
        public void onStart() {
            mTemplate.execute(session -> {
                mQueue = session.createQueue(LogMessage.LOG_QUEUE_NAME);
                return null;
            });
            mScheduledExecutorService.scheduleAtFixedRate(
                    () -> send(),
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

        mTemplate = new JmsTemplate(mMessagingContext.getConnectionFactory());
        mTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        mTemplate.setSessionTransacted(true);

        mMessagingContext.getBroker().getListenerEventDispatcher().add(mBrokerListener);
        if (mMessagingContext.getBroker().isStarted())
            mBrokerListener.onStart();

        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    private void send() {
        mLog.info(String.format("Sending %d", mLogEventBuffer.size()));
        synchronized (mLogEventBuffer) {
            try {
                mTemplate.convertAndSend(mQueue, mLogEventBuffer);
                mLogEventBuffer.clear();
            } catch(Exception e) {
                mLog.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        LoggingEvent le = (LoggingEvent)eventObject;
        synchronized(mLogEventBuffer) {
            mLogEventBuffer.add(new LogMessage(le));
        }
    }

    @Override
    public void dispose() {
        if (mScheduledExecutorService.isShutdown())
            return;

        this.stop();

        mScheduledExecutorService.schedule(() -> send(), 0, TimeUnit.SECONDS);
        mScheduledExecutorService.shutdown();
        try {
            mScheduledExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
