package org.deku.leoz.log

import org.deku.leoz.config.MessagingConfiguration
import sx.jms.Converter
import sx.jms.Handler
import sx.jms.converters.DefaultConverter
import sx.jms.listeners.SpringJmsListener
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.jms.Destination
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
        SpringJmsListener(messagingConfiguration.broker.connectionFactory),
        Handler<Array<LogMessage>> {

    init {
        this.converter = DefaultConverter(
                DefaultConverter.SerializationType.KRYO,
                DefaultConverter.CompressionType.GZIP)

        this.addDelegate(Array<LogMessage>::class.java, this)
    }

    override fun createDestination(): Destination {
        return messagingConfiguration.centralLogQueue
    }

    override fun onMessage(message: Array<LogMessage>, converter: Converter, jmsMessage: Message, session: Session) {
        val timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(
                        jmsMessage.jmsTimestamp), ZoneId.systemDefault())

        log.info("message id [${jmsMessage.jmsMessageID}] ${timestamp}")
    }
}