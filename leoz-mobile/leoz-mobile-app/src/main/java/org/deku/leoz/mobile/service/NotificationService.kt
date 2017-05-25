package org.deku.leoz.mobile.service

import org.deku.leoz.service.internal.entity.update.UpdateInfo
import org.slf4j.LoggerFactory
import sx.mq.MqChannel
import sx.mq.MqHandler

/**
 * Leoz mobile notification service representing the endpoint for mobile (push) notification messages
 * Created by masc on 15.05.17.
 */
class NotificationService : MqHandler<Any> {
    val log = LoggerFactory.getLogger(this.javaClass)

    @MqHandler.Types(
            UpdateInfo::class
    )
    override fun onMessage(message: Any, replyChannel: MqChannel?) {
        log.info("Received message [${message}]")
    }
}