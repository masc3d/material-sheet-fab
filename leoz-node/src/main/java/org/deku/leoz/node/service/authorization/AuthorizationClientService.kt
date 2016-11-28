package org.deku.leoz.node.service.authorization

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.Identity
import org.deku.leoz.node.config.StorageConfiguration
import org.deku.leoz.node.service.authorization.AuthorizationMessage
import org.deku.leoz.node.service.authorization.AuthorizationRequestMessage
import org.slf4j.LoggerFactory
import sx.concurrent.Service
import sx.jms.Channel
import sx.jms.Handler
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService

/**
 * Authorization service
 * Created by masc on 01.07.15.
 */
class AuthorizationClientService(
        executorService: ScheduledExecutorService,
        private val channelConfiguration: Channel.Configuration,
        private val identitySupplier: () -> Identity,
        private val onRejected: (identity: Identity) -> Unit)
:
        Service(executorService,
                period = Duration.ofSeconds(60)),
        Handler<AuthorizationMessage>
{
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val identity: Identity
        get() = identitySupplier()

    private val storageConfiguration: StorageConfiguration by Kodein.global.lazy.instance()

    override fun run() {
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
        val authorizationMessage = Channel(channelConfiguration).use {
            it.sendRequest(authorizationRequest).use {
                it.receive(AuthorizationMessage::class.java)
            }
        }

        // Set id based on response and store identity
        log.info("Received authorization [%s]".format(authorizationMessage))
        if (authorizationMessage.rejected) {
            onRejected(identity)
        } else {
            identity.save(storageConfiguration.identityConfigurationFile)
        }

        // Stop service when authorization process completed
        this.stop(async = true)
    }

    override fun onMessage(message: AuthorizationMessage, replyChannel: Channel?) {
        // TODO: Push authorization update handling. May revoke the node's authorization key
    }
}
