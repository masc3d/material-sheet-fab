package org.deku.leoz.central.messaging.handlers

import ch.qos.logback.classic.Logger
import org.apache.commons.logging.LogFactory
import org.deku.leoz.log.LogMessage
import org.slf4j.LoggerFactory
import sx.jms.Converter
import sx.jms.Handler
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.Session

/**
 * Created by masc on 19/02/16.
 */
class LogMessageHandler : Handler<Array<LogMessage>> {
    private val log = LogFactory.getLog(this.javaClass)

    override fun onMessage(message: Array<LogMessage>, converter: Converter, jmsMessage: Message, session: Session, connectionFactory: ConnectionFactory) {
        val timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(
                        jmsMessage.jmsTimestamp), ZoneId.systemDefault())

        val lb = LoggerFactory.getLogger("leoz-node-1") as Logger
        lb.isAdditive = false
        lb.info("Test")

        log.info("message id [${jmsMessage.jmsMessageID}] ${timestamp}")
    }
}