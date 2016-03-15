package org.deku.leoz.node.services

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.config.messaging.MessagingConfiguration
import org.deku.leoz.node.config.StorageConfiguration
import org.deku.leoz.node.messaging.entities.AuthorizationMessage
import org.deku.leoz.node.messaging.entities.AuthorizationRequestMessage
import sx.concurrent.Service
import sx.jms.Channel
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService

/**
 * Authorization service
 * Created by masc on 01.07.15.
 */
class AuthorizationClientService(
        executorService: ScheduledExecutorService,
        /** Messaging context  */
        private val messagingConfiguration: MessagingConfiguration,
        private val identitySupplier: () -> Identity,
        private val onRejected: (identity: Identity) -> Unit)
:
        Service(executorService,
                period = Duration.ofSeconds(60))
{
    private val log = LogFactory.getLog(this.javaClass)

    private val identity: Identity
        get() = identitySupplier()

    override fun run() {
        var success = false

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

        // Stop service when authorization process completed
        this.stop(async = true)
    }
}
