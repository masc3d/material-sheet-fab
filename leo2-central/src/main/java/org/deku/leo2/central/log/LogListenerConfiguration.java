package org.deku.leo2.central.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sx.jms.embedded.Broker;
import sx.jms.embedded.activemq.ActiveMQBroker;
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

    private Broker.EventListener mBrokerEventListener = new Broker.EventListener() {
        @Override
        public void onStart() {
            mLogListener = new LogListener(ActiveMQContext.instance());
            mLogListener.start();
        }

        @Override
        public void onStop() {
            if (mLogListener != null)
                mLogListener.stop();
        }
    };

    @PostConstruct
    public void onInitialize() {
        // Register to broker start
        ActiveMQBroker.instance().getDelegate().add(mBrokerEventListener);
    }

    @PreDestroy
    public void onDestroy() {
        if (mLogListener != null)
            mLogListener.dispose();
    }
}
