package org.deku.leo2.node.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.Broker;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.messaging.activemq.ActiveMQContext;
import org.deku.leo2.node.LocalStorage;
import org.deku.leo2.node.messaging.auth.IdentityServiceClient;
import org.deku.leo2.node.messaging.auth.v1.AuthorizationMessage;
import sx.Disposable;

import java.util.concurrent.*;

/**
 * Created by masc on 01.07.15.
 */
public class Authorizer implements Disposable {
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

        @Override
        public void onStop() {
            dispose();
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

                    if (identity.hasId()) {
                        // Simply publish id
                        isc.publish(identity);
                    } else {
                        // Synchronous request for id
                        AuthorizationMessage authorizationMessage = isc.requestId(identity);

                        // Set id based on response and store identity
                        identity.setId(authorizationMessage.getId());
                        identity.store(LocalStorage.instance().getIdentityConfigurationFile());
                    }
                    success = true;
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
            mExecutorService.shutdown();
        };

        // Register broker event
        mMessagingContext.getBroker().getDelegate().add(mBrokerEventListener);
        if (mMessagingContext.getBroker().isStarted())
            mExecutorService.submit(mAuthorizationTask);
    }

    @Override
    public void dispose() {
        mExecutorService.shutdown();
        try {
            mExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            mLog.error(e.getMessage(), e);
        }
    }
}
