package org.deku.leoz.node.messaging

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.config.messaging.MessagingConfiguration
import org.deku.leoz.node.messaging.entities.AuthorizationMessage
import org.deku.leoz.node.messaging.entities.IdentityMessage
import sx.jms.converters.DefaultConverter

/**
 * Created by masc on 01.07.15.
 * @pram mMessagingConfiguration Messaging configuration
 */
class IdentityPublisher(
        private val messagingConfiguration: MessagingConfiguration) {
    private val log = LogFactory.getLog(this.javaClass)

    private val converter = DefaultConverter(
            DefaultConverter.SerializationType.KRYO,
            DefaultConverter.CompressionType.GZIP)

    /**
     * Publishes client node idenity to the central system
     * @param identity
     */
    @Throws(Exception::class)
    fun publish(identity: Identity) {
        this.sendAndReceive(identity, false)
    }

    /**
     * Request client node id from central system
     * @param identity
     */
    @Throws(Exception::class)
    fun requestId(identity: Identity): AuthorizationMessage {
        return this.sendAndReceive(identity, true)!!
    }

    /**

     * @param identity
     * *
     * @param receive
     */
    @Throws(Exception::class)
    private fun sendAndReceive(
            identity: Identity,
            receive: Boolean): AuthorizationMessage? {

        // Connection and session
        val centralChannel = messagingConfiguration.centralQueueChannel()

        // Setup message
        val identityMessage = IdentityMessage()
        identityMessage.id = identity.id
        identityMessage.name = identity.name
        identityMessage.key = identity.key

        // Serialize system info to json
        val jsonMapper = ObjectMapper()
        val systemInformationJson: String
        try {
            systemInformationJson = jsonMapper.writeValueAsString(identity.systemInformation)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }

        identityMessage.systemInfo = systemInformationJson

        // Convert and send
        if (receive) {
            centralChannel.sendRequest(identityMessage).use {
                return it.receive(AuthorizationMessage::class.java)
            }
        } else {
            centralChannel.send(identityMessage)
            return null
        }
    }
}
