package org.deku.leo2.central.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.central.data.repositories.jooq.NodeJooqRepository;
import org.deku.leo2.messaging.Broker;
import org.deku.leo2.messaging.activemq.ActiveMQBroker;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.node.messaging.auth.v1.IdentityMessage;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

/**
 * Created by masc on 20.06.15.
 */
@Configuration
@Lazy(false)
public class MessageListenerConfiguration {
    private Log mLog = LogFactory.getLog(this.getClass());

    @Inject
    private NodeJooqRepository mNodeJooqRepository;

    /** Central message listener */
    MessageListener mMessageListener;

    /**
     * Broker event listener
     */
    Broker.EventListener mBrokerEventListener = new Broker.EventListener() {
        @Override
        public void onStart() {
            startIfReady();
        }
    };

    /**
     * Start message listener
     */
    private void startIfReady() {
        if (mMessageListener != null) {
            mMessageListener.dispose();
            mMessageListener = null;
        }

        if (ActiveMQContext.instance().getBroker().isStarted()) {
            // Configure and create listener
            mMessageListener = new MessageListener(ActiveMQContext.instance());

            // Add message handler delegatess
            mMessageListener.addDelegate(IdentityMessage.class,
                    new IdentityMessageHandler(
                            mNodeJooqRepository));

            mMessageListener.start();
        }
    }

    @PostConstruct
    public void onInitialize() {
        mLog.info("Initializing node message listener");

        // Register event listeners
        ActiveMQBroker.instance().getDelegate().add(mBrokerEventListener);

        this.startIfReady();
    }

    @PreDestroy
    public void onDestroy() {
        mMessageListener.stop();
    }
}
