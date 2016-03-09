package org.deku.leoz.log

import org.deku.leoz.config.messaging.MessagingConfiguration
import sx.jms.Channel
import sx.jms.Handler
import sx.jms.listeners.SpringJmsListener

/**
 * Log message listener
 * Created by masc on 16.04.15.
 */
class LogListener(
        /** Messaging context */
        private val messagingConfiguration: MessagingConfiguration)
:
        SpringJmsListener({ Channel(messagingConfiguration.centralLogQueue) }),
        Handler<Array<LogMessage>>
{
    init {
        this.addDelegate(Array<LogMessage>::class.java, this)
    }

    override fun onMessage(message: Array<LogMessage>, replyChannel: Channel?) {
    }
}