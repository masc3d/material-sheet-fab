package org.deku.leoz.central.messaging.handlers

import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.entities.UpdateInfo
import org.deku.leoz.bundle.entities.UpdateInfoRequest
import sx.jms.Channel
import sx.jms.Converter
import sx.jms.Handler
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

    override fun onMessage(message: UpdateInfoRequest, converter: Converter, jmsMessage: Message, session: Session) {
        val updateInfoRequest = message
        // TODO: Query bundle name/version against db
        val versionPattern = "+RELEASE"

        try {
            val channel = Channel(
                    session = session,
                    destination = jmsMessage.jmsReplyTo,
                    converter = converter)

            channel.send(UpdateInfo(
                    updateInfoRequest.bundleName,
                    versionPattern))

            channel.commit()
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }
}