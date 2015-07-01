package org.deku.leo2.node.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.Broker;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.node.messaging.auth.IdentityServiceClient;
import org.deku.leo2.node.messaging.auth.v1.AuthorizationMessage;

import java.util.concurrent.*;

/**
 * Created by masc on 01.07.15.
 */
public class Authorizer {
    Log mLog = LogFactory.getLog(this.getClass());

    /** Messaging context */
    private MessagingContext mMessagingContext;
    /** Executor service for authorization task */
    private ExecutorService mExecutorService;
    /** Authorization task */
    private Runnable mAuthorizationTask;

    private Broker.EventListener mBrokerEventListener = new Broker.EventListener() {
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
        // Define authorization task.
        // Start will be deferred until the message broker is up.
        mAuthorizationTask = () -> {
            boolean success = false;
            while (!success) {
                try {
                    IdentityServiceClient isc = new IdentityServiceClient(ActiveMQContext.instance());

                    // Request identitification
                    AuthorizationMessage authorizationMessage = isc.requestId(identity);

                    // Set id based on response
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

        // Register broker event
        mMessagingContext.getBroker().getDelegate().add(mBrokerEventListener);
        if (mMessagingContext.getBroker().isStarted())
            mExecutorService.submit(mAuthorizationTask);
    }
}
