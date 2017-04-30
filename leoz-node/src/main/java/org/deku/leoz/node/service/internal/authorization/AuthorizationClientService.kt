package org.deku.leoz.node.service.internal.authorization

import com.fasterxml.jackson.core.JsonProcessingException
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.node.Storage
import sx.jms.Channel
import sx.time.Duration

/**
 * Authorization service
 * Created by masc on 01.07.15.
 */
class AuthorizationClientService(
        executorService: java.util.concurrent.ScheduledExecutorService,
        private val channelConfiguration: sx.jms.Channel.Configuration,
        private val identitySupplier: () -> org.deku.leoz.Identity,
        private val onRejected: (identity: org.deku.leoz.Identity) -> Unit)
:
        sx.concurrent.Service(executorService,
                period = Duration.ofSeconds(60)),
        sx.jms.Handler<AuthorizationMessage>
{
    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    private val identity: org.deku.leoz.Identity
        get() = identitySupplier()

    private val storage: Storage by Kodein.global.lazy.instance()

    override fun run() {
        // Setup message
        val authorizationRequest = AuthorizationRequestMessage()
        authorizationRequest.name = identity.name
        authorizationRequest.key = identity.key

        // Serialize system info to json
        val jsonMapper = com.fasterxml.jackson.databind.ObjectMapper()
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
            identity.save(storage.identityConfigurationFile)
        }

        // Stop service when authorization process completed
        this.stop(async = true)
    }

    override fun onMessage(message: AuthorizationMessage, replyChannel: Channel?) {
        // TODO: Push authorization update handling. May revoke the node's authorization key
    }
}
