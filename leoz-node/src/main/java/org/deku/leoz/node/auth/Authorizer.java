package org.deku.leoz.node.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.messaging.MessagingContext;
import org.deku.leoz.messaging.activemq.ActiveMQContext;
import org.deku.leoz.node.LocalStorage;
import org.deku.leoz.node.messaging.auth.IdentityPublisher;
import org.deku.leoz.node.messaging.auth.v1.AuthorizationMessage;
import sx.Disposable;
import sx.Dispose;
import sx.jms.embedded.Broker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
            Dispose.safely(Authorizer.this);
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
            while (true) {
                try {
                    IdentityPublisher isc = new IdentityPublisher(ActiveMQContext.instance());

                    if (identity.hasId()) {
                        // Simply publish id
                        mLog.info(String.format("Publishing [%s]", identity));

                        isc.publish(identity);
                    } else {
                        // Synchronous request for id
                        mLog.info(String.format("Request id for [%s]", identity));
                        AuthorizationMessage authorizationMessage = isc.requestId(identity);

                        // Set id based on response and store identity
                        mLog.info(String.format("Received authorization update [%s]", authorizationMessage));
                        identity.setId(authorizationMessage.getId());
                        identity.store(LocalStorage.instance().getIdentityConfigurationFile());
                    }
                    success = true;
                } catch (TimeoutException e) {
                    mLog.error(e.getMessage());
                } catch (Exception e) {
                    mLog.error(e.getMessage(), e);
                }

                if (success)
                    break;
                else {
                    if (mExecutorService.isShutdown())
                        break;

                    // Retry delay
                    try {
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
        mExecutorService.shutdownNow();
        try {
            mExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            mLog.error(e.getMessage(), e);
        }
    }
}
