package org.deku.leo2.node.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.Broker;
import org.deku.leo2.messaging.activemq.ActiveMQBroker;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.node.App;
import org.deku.leo2.node.auth.Identity;
import org.deku.leo2.node.auth.IdentityConfiguration;
import org.deku.leo2.node.messaging.auth.AuthorizationHandler;
import org.deku.leo2.node.messaging.auth.v1.AuthorizationMessage;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by masc on 20.06.15.
 */
@Profile( { App.PROFILE_CLIENT_NODE} )
@Configuration("node.PeerMessageListenerConfiguration")
@Lazy(false)
public class MessageListenerConfiguration {
    private Log mLog = LogFactory.getLog(this.getClass());

    MessageListener mMessageListener;

    /**
     * Broker event listener
     */
    Broker.Listener mBrokerEventListener = new ActiveMQBroker.Listener() {
        @Override
        public void onStart() {
            startIfReady();
        }
    };

    /**
     * Identity event listener
     */
    Identity.Listener mIdentityEventListener = new Identity.Listener() {
        @Override
        public void onIdUpdated(Identity identity) {
            startIfReady();
        }
    };

    /**
     * Indicates if message listener is ready to start (prerequisites are met)
     * @return
     */
    private boolean isReadyToStart() {
        return ActiveMQContext.instance().getBroker().isStarted() &&
                IdentityConfiguration.instance().getIdentity().getId() != null;
    }

    /**
     * Start message listener
     */
    private void startIfReady() {
        if (mMessageListener != null) {
            mMessageListener.dispose();
            mMessageListener = null;
        }

        if (this.isReadyToStart()) {
            // Configure and create listener
            mMessageListener = new MessageListener(
                    ActiveMQContext.instance(),
                    IdentityConfiguration.instance().getIdentity());

            // Add message handler delegatess
            mMessageListener.addDelegate(AuthorizationMessage.class, new AuthorizationHandler());

            mMessageListener.start();
        }
    }

    @PostConstruct
    public void onInitialize() {
        mLog.info("Initializing node message listener");

        // Register event listeners
        ActiveMQBroker.instance().getDelegate().add(mBrokerEventListener);
        IdentityConfiguration.instance().getIdentity().getDelegate().add(mIdentityEventListener);

        this.startIfReady();
    }

    @PreDestroy
    public void onDestroy() {
        mMessageListener.stop();
    }
}
