package org.deku.leoz.central.messaging.handlers

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.central.data.repositories.NodeRepository
import org.deku.leoz.node.messaging.entities.AuthorizationMessage
import org.deku.leoz.node.messaging.entities.AuthorizationRequestMessage
import sx.jms.Channel
import sx.jms.Converter
import sx.jms.Handler
import sx.jms.converters.DefaultConverter
import javax.inject.Inject
import javax.inject.Named
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.Session

/**
 * Created by masc on 01.07.15.
 */
@Named
class AuthorizationRequestHandler : Handler<AuthorizationRequestMessage> {
    private val log = LogFactory.getLog(this.javaClass)
    private val converter: Converter

    @Inject
    private lateinit var nodeRepository: NodeRepository

    init {
        converter = DefaultConverter(
                DefaultConverter.SerializationType.KRYO,
                DefaultConverter.CompressionType.GZIP)
    }

    override fun onMessage(message: AuthorizationRequestMessage, converter: Converter, jmsMessage: Message, session: Session, connectionFactory: ConnectionFactory) {
        try {
            log.info(message)

            // Response message
            val am = AuthorizationMessage()
            am.key = message.key

            var record = nodeRepository.findByKey(message.key)
            if (record == null) {
                val identityKey = Identity.Key(message.key)
                val conflictingRecord = nodeRepository.findByKeyStartingWith(identityKey.short)
                if (conflictingRecord != null) {
                    // Short key conflict, reject
                    am.rejected = true
                    log.warn("Node [${message.key}] has short key conflicting with [${conflictingRecord.key}] and will be rejected")
                } else {
                    // Store new node record
                    record = nodeRepository.createNew()
                    record.key = message.key
                    record.bundle = message.name
                    record.sysInfo = message.systemInfo
                    record.store()
                }
            }

            val replyTo = jmsMessage.jmsReplyTo
            if (replyTo != null) {
                am.authorized = record != null && record.authorized != null && record.authorized !== 0

                Channel(connectionFactory = connectionFactory,
                        sessionTransacted = false,
                        destination = replyTo,
                        converter = this.converter).use { c ->
                    c.send(am)
                }

                log.info("Sent authorization [%s]".format(am))
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}
