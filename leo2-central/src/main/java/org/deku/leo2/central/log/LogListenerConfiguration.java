package org.deku.leo2.central.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.activemq.ActiveMQBroker;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.messaging.log.LogListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by masc on 29.06.15.
 */
@Configuration
@Lazy(false)
public class LogListenerConfiguration {
    Log mLog = LogFactory.getLog(this.getClass());

    /** Log listener instance */
    LogListener mLogListener;

    private ActiveMQBroker.Listener mBrokerListener = new ActiveMQBroker.Listener() {
        @Override
        public void onStart() {
            mLog.info("Detected broker start, attaching listeners");
            mLogListener = new LogListener(ActiveMQContext.instance());
            mLogListener.start();
        }
    };

    @PostConstruct
    public void onInitialize() {
        // Register to broker start
        ActiveMQBroker.instance().getDelegate().add(mBrokerListener);
    }

    @PreDestroy
    public void onDestroy() {
        if (mLogListener != null)
            mLogListener.dispose();
    }
}
