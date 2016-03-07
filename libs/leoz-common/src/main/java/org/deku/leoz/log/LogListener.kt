package org.deku.leoz.log

import org.deku.leoz.config.messaging.MessagingConfiguration
import sx.jms.Converter
import sx.jms.Handler
import sx.jms.listeners.SpringJmsListener
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.Session

/**
 * Log message listener
 * Created by masc on 16.04.15.
 */
class LogListener(
        /** Messaging context */
        private val messagingConfiguration: MessagingConfiguration)
:
        SpringJmsListener( { messagingConfiguration.centralLogChannel() } ),
        Handler<Array<LogMessage>>
{
    init {
        this.addDelegate(Array<LogMessage>::class.java, this)
    }

    override fun onMessage(message: Array<LogMessage>, converter: Converter, jmsMessage: Message, session: Session, connectionFactory: ConnectionFactory) {
        val timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(
                        jmsMessage.jmsTimestamp), ZoneId.systemDefault())

        log.info("message id [${jmsMessage.jmsMessageID}] ${timestamp}")
    }
}