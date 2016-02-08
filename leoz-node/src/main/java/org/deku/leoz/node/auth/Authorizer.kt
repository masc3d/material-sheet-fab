package org.deku.leoz.node.auth

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.config.messaging.MessagingConfiguration
import org.deku.leoz.node.config.StorageConfiguration
import org.deku.leoz.node.messaging.IdentityPublisher
import sx.Disposable
import sx.Dispose
import sx.jms.embedded.Broker
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.properties.Delegates

/**
 * Authorizer
 * Created by masc on 01.07.15.
 */
class Authorizer(
        /** Messaging context  */
        private val mMessagingConfiguration: MessagingConfiguration)
:
        Disposable {
    private val log = LogFactory.getLog(this.javaClass)
    /** Executor service for authorization task  */
    // TODO: change back to val once kotlin bug complaining about uninitialized val (even though it's initialized in init) is resolved
    private var executorService: ExecutorService by Delegates.notNull()
    /** Authorization task  */
    private var authorizationTask: Runnable? = null

    /** Broker event listener */
    private val brokerEventListener = object : Broker.DefaultEventListener() {
        override fun onStart() {
            if (authorizationTask != null)
                executorService.submit(authorizationTask)
        }

        override fun onStop() {
            Dispose.safely(this@Authorizer)
        }
    }

    /** c'or */
    init {
        executorService = Executors.newSingleThreadExecutor()
    }

    /**
     * Start authorization process
     * @param identity Identity to use to authorize
     */
    fun start(identity: Identity) {
        // Define authorization task.
        // Start will be deferred until the message broker is up.
        authorizationTask = Runnable {
            var success = false
            while (!success && !executorService.isShutdown) {
                try {
                    val isc = IdentityPublisher(ActiveMQConfiguration.instance)

                    if (identity.hasId()) {
                        // Simply publish id
                        log.info("Publishing [%s]".format(identity))

                        isc.publish(identity)
                    } else {
                        // Synchronous request for id
                        log.info("Requesting id for [%s]".format(identity))
                        val authorizationMessage = isc.requestId(identity)

                        // Set id based on response and store identity
                        log.info("Received authorization update [%s]".format(authorizationMessage))
                        identity.id = authorizationMessage.id
                        identity.store(StorageConfiguration.instance.identityConfigurationFile)
                    }
                    success = true
                } catch (e: TimeoutException) {
                    log.error(e.message)
                } catch (e: Exception) {
                    log.error(e.message, e)
                }

                if (success)
                    break
                else {
                    if (executorService.isShutdown)
                        break

                    // Retry delay
                    try {
                        TimeUnit.MINUTES.sleep(1)
                    } catch (e1: InterruptedException) {
                        log.error(e1.message, e1)
                    }

                }
            }
            executorService.shutdown()
        }

        // Register broker event
        mMessagingConfiguration.broker.delegate.add(brokerEventListener)
        if (mMessagingConfiguration.broker.isStarted)
            executorService.submit(authorizationTask)
    }

    override fun close() {
        executorService.shutdownNow()
        try {
            executorService.awaitTermination(java.lang.Long.MAX_VALUE, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            log.error(e.message, e)
        }
    }
}
