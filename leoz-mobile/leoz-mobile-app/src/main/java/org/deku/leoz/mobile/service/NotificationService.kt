package org.deku.leoz.mobile.service

import sx.mq.MqClient
import sx.mq.MqHandler

/**
 * Leoz mobile notification service representing the endpoint for mobile (push) notification messages
 * Created by masc on 15.05.17.
 */
class NotificationService : MqHandler<Any> {
    @MqHandler.Types()
    override fun onMessage(message: Any, replyClient: MqClient?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}