package org.deku.leoz.central.messaging.handlers

import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.data.repositories.NodeRepository
import org.deku.leoz.node.messaging.entities.AuthorizationMessage
import org.deku.leoz.node.messaging.entities.IdentityMessage
import sx.jms.Converter
import sx.jms.Handler
import sx.jms.converters.DefaultConverter
import javax.jms.DeliveryMode
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

    override fun onMessage(message: IdentityMessage, converter: Converter, jmsMessage: Message, session: Session) {
        try {
            log.info(message)

            val r = mNodeRepository.findByKeyOrCreateNew(message.key)

            r.hostname = message.hardwareAddress
            r.key = message.key
            r.sysInfo = message.systemInfo
            r.store()

            val replyTo = jmsMessage.jmsReplyTo
            if (replyTo != null) {
                val am = AuthorizationMessage()
                am.id = r.nodeId
                am.key = r.key
                am.authorized = r.authorized != null && r.authorized !== 0

                val mp = session.createProducer(replyTo)
                mp.deliveryMode = DeliveryMode.NON_PERSISTENT
                mp.timeToLive = (10 * 1000).toLong()
                mp.priority = 8
                mp.send(converter.toMessage(am, session))

                session.commit()

                log.info("Sent authorization [%s]".format(am))
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}
