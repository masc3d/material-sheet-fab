package org.deku.leoz.central.messaging.handlers

import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.data.repositories.NodeRepository
import org.deku.leoz.node.messaging.entities.AuthorizationMessage
import org.deku.leoz.node.messaging.entities.IdentityMessage
import sx.jms.Channel
import sx.jms.Converter
import sx.jms.Handler
import sx.jms.converters.DefaultConverter
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.Session

/**
 * Created by masc on 01.07.15.
 */
class IdentityMessageHandler(private val mNodeRepository: NodeRepository) : Handler<IdentityMessage> {
    private val log = LogFactory.getLog(this.javaClass)
    private val converter: Converter

    init {
        converter = DefaultConverter(
                DefaultConverter.SerializationType.KRYO,
                DefaultConverter.CompressionType.GZIP)
    }

    override fun onMessage(message: IdentityMessage, converter: Converter, jmsMessage: Message, session: Session, connectionFactory: ConnectionFactory) {
        try {
            log.info(message)

            var r = mNodeRepository.findByKeyOrCreateNew(message.key)

            r.hostname = message.hardwareAddress
            r.key = message.key
            r.sysInfo = message.systemInfo
            r.store()
            r = mNodeRepository.findByKeyOrCreateNew(message.key)

            val replyTo = jmsMessage.jmsReplyTo
            if (replyTo != null) {
                // Create authorization message
                val am = AuthorizationMessage()
                am.id = r.nodeId
                am.key = r.key
                am.authorized = r.authorized != null && r.authorized !== 0

                Channel(connectionFactory = connectionFactory,
                        jmsSessionTransacted = false,
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
