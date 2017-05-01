package org.deku.leoz.node.service.internal

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.Identity
import org.deku.leoz.SystemInformation
import org.deku.leoz.node.Storage
import org.deku.leoz.service.internal.AuthorizationService
import sx.jms.Channel
import sx.jms.Handler
import sx.time.Duration

/**
 * Authorization client service, performing background authorization via message bus
 * Created by masc on 01.07.15.
 */
class AuthorizationClientService(
        executorService: java.util.concurrent.ScheduledExecutorService,
        private val channelConfiguration: Channel.Configuration,
        private val identitySupplier: () -> Identity,
        private val onRejected: (identity: Identity) -> Unit)
:
        sx.concurrent.Service(executorService,
                period = Duration.ofSeconds(60)),

        // Message handler for retrieving push authorization updates
        Handler<AuthorizationService.Response>
{
    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    private val identity: Identity
        get() = identitySupplier()

    private val systemInformation: SystemInformation by Kodein.global.lazy.instance()
    private val storage: Storage by Kodein.global.lazy.instance()

    override fun run() {
        // Setup message
        val authorizationRequest = AuthorizationService.Request()
        authorizationRequest.name = identity.name
        authorizationRequest.key = identity.key

        // Serialize system info to json
        val jsonMapper = ObjectMapper()
        val systemInformationJson: String
        try {
            systemInformationJson = jsonMapper.writeValueAsString(this.systemInformation)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }

        authorizationRequest.systemInfo = systemInformationJson

        log.info("Sending ${authorizationRequest}")

        // Connection and session
        val authorizationMessage = Channel(channelConfiguration).use {
            it.sendRequest(authorizationRequest).use {
                it.receive(AuthorizationService.Response::class.java)
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

    override fun onMessage(message: AuthorizationService.Response, replyChannel: Channel?) {
        // TODO: Push authorization update handling. May revoke the node's authorization key
    }
}
