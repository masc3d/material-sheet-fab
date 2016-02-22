package org.deku.leoz.central.messaging.handlers

import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.entities.UpdateInfo
import org.deku.leoz.bundle.entities.UpdateInfoRequest
import sx.jms.Channel
import sx.jms.Converter
import sx.jms.Handler
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.Session

/**
 * Update info service, providing version pattern information to clients
 * Created by masc on 19.10.15.
 */
class UpdateInfoRequestHandler
:
        Handler<UpdateInfoRequest>
{
    private val log = LogFactory.getLog(this.javaClass)

    override fun onMessage(message: UpdateInfoRequest, converter: Converter, jmsMessage: Message, session: Session, connectionFactory: ConnectionFactory) {
        val updateInfoRequest = message
        // TODO: Query bundle name/version against db
        val versionPattern = "+RELEASE"

        try {
            Channel(
                    connectionFactory = connectionFactory,
                    jmsSessionTransacted = false,
                    destination = jmsMessage.jmsReplyTo,
                    converter = converter).use {

                it.send(UpdateInfo(
                        updateInfoRequest.bundleName,
                        versionPattern))

            }
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }
}