package org.deku.leo2.node.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.Broker;
import org.deku.leo2.messaging.activemq.ActiveMQBroker;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.node.App;
import org.deku.leo2.node.messaging.auth.AuthorizationResponseHandler;
import org.deku.leo2.node.messaging.auth.v1.AuthorizationResponse;
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

    Broker.Listener mBrokerListner = new ActiveMQBroker.Listener() {
        @Override
        public void onStart() {
            mMessageListener.start();
        }
    };

    @PostConstruct
    public void onInitialize() {
        mLog.info("Initializing peer message listener");

        mMessageListener = new MessageListener(
                ActiveMQContext.instance(),
                0);
        mMessageListener.addDelegate(AuthorizationResponse.class, new AuthorizationResponseHandler());

        ActiveMQBroker.instance().getListenerEventDispatcher().add(mBrokerListner);
    }

    @PreDestroy
    public void onDestroy() {
        mMessageListener.stop();
    }
}
