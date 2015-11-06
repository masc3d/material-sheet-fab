package org.deku.leoz.central.messaging.handlers

import org.deku.leoz.bundle.update.entities.UpdateInfo
import org.deku.leoz.bundle.update.entities.UpdateInfoRequest
import sx.jms.Channel
import sx.jms.Converter
import sx.jms.Handler
import java.time.Duration
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
    override fun onMessage(message: UpdateInfoRequest, converter: Converter, jmsMessage: Message, session: Session) {
        val updateInfoRequest = message
        // TODO: Query bundle name/version against db
        val versionPattern = "+RELEASE"

        val channel = Channel(
                session = session,
                destination = jmsMessage.jmsReplyTo,
                converter = converter,
                jmsDeliveryMode = Channel.DeliveryMode.NonPersistent,
                jmsTtl = Duration.ofSeconds(10))

        channel.send(UpdateInfo(
                updateInfoRequest.bundleName,
                versionPattern))

        channel.commit()
    }
}