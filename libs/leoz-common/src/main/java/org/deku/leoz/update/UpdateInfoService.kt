package org.deku.leoz.update

import sx.jms.Channel
import sx.jms.Handler
import sx.jms.converters.DefaultConverter
import java.time.Duration
import javax.jms.Message
import javax.jms.Session

/**
 * Bundle update provider service
 * Created by masc on 19.10.15.
 */
class UpdateInfoService : Handler<UpdateInfoRequest> {
    private val converter = DefaultConverter(
            DefaultConverter.SerializationType.KRYO,
            DefaultConverter.CompressionType.NONE)

    override fun onMessage(updateInfoRequest: UpdateInfoRequest, jmsMessage: Message, session: Session) {
        // Query bundle name/version against db

        val channel = Channel(
                session = session,
                destination = jmsMessage.jmsReplyTo,
                converter = converter,
                jmsSessionTransacted = false,
                jmsDeliveryMode = Channel.DeliveryMode.NonPersistent,
                jmsTtl = Duration.ofSeconds(10))

        channel.send(UpdateInfo(
                updateInfoRequest.bundleName,
                ""))
    }
}