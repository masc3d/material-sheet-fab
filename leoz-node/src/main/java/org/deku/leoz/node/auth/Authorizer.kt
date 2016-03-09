package org.deku.leoz.node.auth

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.config.messaging.MessagingConfiguration
import org.deku.leoz.node.config.StorageConfiguration
import org.deku.leoz.node.messaging.entities.AuthorizationMessage
import org.deku.leoz.node.messaging.entities.AuthorizationRequestMessage
import sx.Disposable
import sx.Dispose
import sx.jms.Channel
import sx.jms.embedded.Broker
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Authorizer
 * Created by masc on 01.07.15.
 */
class Authorizer(
        /** Messaging context  */
        private val messagingConfiguration: MessagingConfiguration)
:
        Disposable
{
    private val log = LogFactory.getLog(this.javaClass)
    /** Executor service for authorization task  */
    private var executorService: ExecutorService? = null
    /** Authorization task  */
    private var authorizationTask: Runnable? = null

    /** c'or */
    init {
    }

    /** Broker event listener */
    private val brokerEventListener = object : Broker.DefaultEventListener() {
        override fun onStart() {
            if (authorizationTask != null)
                this@Authorizer.executorService!!.submit(authorizationTask)
        }

        override fun onStop() {
            Dispose.safely(this@Authorizer)
        }
    }

    /**
     * Start authorization process.
     * @param identity Identity to use to authorize
     * @param onRejected Rejection callback
     */
    @Synchronized fun start(identity: Identity, onRejected: (identity: Identity) -> Unit = {}) {
        this.stop()
        this.executorService = Executors.newSingleThreadExecutor()

        // Define authorization task.
        // Start will be deferred until the message broker is up.
        authorizationTask = Runnable {
            var success = false
            val executorService = this.executorService!!
            while (!success && !executorService.isShutdown) {
                try {
                    // Setup message
                    val authorizationRequest = AuthorizationRequestMessage()
                    authorizationRequest.name = identity.name
                    authorizationRequest.key = identity.key

                    // Serialize system info to json
                    val jsonMapper = ObjectMapper()
                    val systemInformationJson: String
                    try {
                        systemInformationJson = jsonMapper.writeValueAsString(identity.systemInformation)
                    } catch (e: JsonProcessingException) {
                        throw RuntimeException(e)
                    }

                    authorizationRequest.systemInfo = systemInformationJson

                    log.info("Sending ${authorizationRequest}")

                    // Connection and session
                    val authorizationMessage = Channel(messagingConfiguration.centralQueue).use {
                        it.sendRequest(authorizationRequest).use {
                            it.receive(AuthorizationMessage::class.java)
                        }
                    }

                    // Set id based on response and store identity
                    log.info("Received authorization [%s]".format(authorizationMessage))
                    if (authorizationMessage.rejected) {
                        onRejected(identity)
                    } else {
                        identity.save(StorageConfiguration.instance.identityConfigurationFile)
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
        messagingConfiguration.broker.delegate.add(brokerEventListener)
        if (messagingConfiguration.broker.isStarted)
            executorService!!.submit(authorizationTask)
    }

    /**
     * Stop executor/authorizer and wait for shutdown
     */
    @Synchronized private fun stop() {
        val executorService = this.executorService
        if (executorService != null) {
            executorService.shutdownNow()
            try {
                executorService.awaitTermination(java.lang.Long.MAX_VALUE, TimeUnit.SECONDS)
            } catch (e: InterruptedException) { }
            this.executorService = null
        }
    }

    override fun close() {
        this.stop()
    }
}
