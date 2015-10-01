package org.deku.leoz.messaging.log

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.messaging.MessagingContext
import sx.jms.Handler
import sx.jms.listeners.SpringJmsListener
import sx.jms.converters.DefaultConverter

import javax.jms.Destination
import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Log message listener
 * Created by masc on 16.04.15.
 */
class LogListener(
        /** Messaging context */
        private val messagingContext: MessagingContext) : SpringJmsListener(messagingContext.broker.connectionFactory), Handler<Array<LogMessage>> {

    init {
        this.converter = DefaultConverter(
                DefaultConverter.SerializationType.KRYO,
                DefaultConverter.CompressionType.GZIP)

        this.addDelegate(Array<LogMessage>::class.java, this)
    }

    override fun createDestination(): Destination {
        return messagingContext.centralLogQueue
    }

    override fun onMessage(message: Array<LogMessage>, jmsMessage: Message, session: Session) {
        val timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(
                        jmsMessage.jmsTimestamp), ZoneId.systemDefault())

        log.info("message id [${jmsMessage.jmsMessageID}] ${timestamp}")
    }
}