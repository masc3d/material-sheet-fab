package org.deku.leo2.node.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.activemq.ActiveMQBroker;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.node.App;
import org.deku.leo2.node.auth.Identity;
import org.deku.leo2.node.data.PersistenceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

/**
 * Created by masc on 20.06.15.
 */
@Configuration("node.PeerMessageListenerConfiguration")
@Lazy(false)
public class PeerMessageListenerConfiguration {
    private Log mLog = LogFactory.getLog(this.getClass());

    PeerMessageListener mPeerMessageListener;

    @PostConstruct
    public void onInitialize() {
        mPeerMessageListener = new PeerMessageListener(
                ActiveMQContext.instance(),
                0);
    }

    @PreDestroy
    public void onDestroy() {
    }
}
