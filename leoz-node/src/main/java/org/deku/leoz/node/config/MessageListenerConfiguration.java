package org.deku.leoz.node.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.messaging.activemq.ActiveMQContext;
import org.deku.leoz.node.App;
import org.deku.leoz.node.auth.Identity;
import org.deku.leoz.node.messaging.MessageListener;
import org.deku.leoz.node.messaging.auth.AuthorizationMessageHandler;
import org.deku.leoz.node.messaging.auth.v1.AuthorizationMessage;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import sx.jms.embedded.Broker;
import sx.jms.embedded.activemq.ActiveMQBroker;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Message listener configuration.
 * Initializes message listener(s) and their message handlers
 * Created by masc on 20.06.15.
 */
@Profile( { App.PROFILE_CLIENT_NODE} )
@Configuration
@Lazy(false)
public class MessageListenerConfiguration {
    private Log mLog = LogFactory.getLog(MessageListenerConfiguration.class);

    MessageListener mMessageListener;

    /**
     * Broker event listener
     */
    Broker.EventListener mBrokerEventListener = new Broker.EventListener() {
        @Override
        public void onStart() {
            startIfReady();
        }

        @Override
        public void onStop() {
            stop();
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
        this.stop();

        if (this.isReadyToStart()) {
            // Configure and create listener
            mMessageListener = new MessageListener(
                    ActiveMQContext.instance(),
                    IdentityConfiguration.instance().getIdentity());

            // Add message handler delegatess
            mMessageListener.addDelegate(AuthorizationMessage.class, new AuthorizationMessageHandler());

            mMessageListener.start();
        }
    }

    private void stop() {
        if (mMessageListener != null) {
            mMessageListener.dispose();
            mMessageListener = null;
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
        if (mMessageListener != null)
            mMessageListener.stop();
    }
}
