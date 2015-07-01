package org.deku.leo2.node.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.Broker;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.node.messaging.auth.IdentityServiceClient;
import org.deku.leo2.node.messaging.auth.v1.AuthorizationMessage;
import org.joda.time.Interval;
import sx.LazyInstance;

import javax.jms.JMSException;
import java.util.concurrent.*;

/**
 * Created by masc on 01.07.15.
 */
public class Authorizer {
    Log mLog = LogFactory.getLog(this.getClass());

    private MessagingContext mMessagingContext;
    private ExecutorService mExecutorService;
    private Runnable mAuthorizationTask;

    private Broker.Listener mBrokerListener = new Broker.Listener() {
        @Override
        public void onStart() {
            if (mAuthorizationTask != null)
                mExecutorService.submit(mAuthorizationTask);
        }
    };

    public Authorizer(MessagingContext messagingContext) {
        mMessagingContext = messagingContext;
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Start authorization process
     * @param identity Identity to use to authorize
     */
    public void start(Identity identity) {
        mAuthorizationTask = () -> {
            boolean success = false;
            while (!success) {
                try {
                    IdentityServiceClient isc = new IdentityServiceClient(ActiveMQContext.instance());
                    AuthorizationMessage authorizationMessage = isc.requestId(identity);
                    identity.setId(authorizationMessage.getId());
                } catch (TimeoutException e) {
                    mLog.error(e.getMessage());
                } catch (Exception e) {
                    mLog.error(e.getMessage(), e);
                }

                if (!success) {
                    try {
                        // Sleep until retry
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e1) {
                        mLog.error(e1.getMessage(), e1);
                    }
                }
            }
        };

        mMessagingContext.getBroker().getListenerEventDispatcher().add(mBrokerListener);
        if (mMessagingContext.getBroker().isStarted())
            mExecutorService.submit(mAuthorizationTask);
    }
}
