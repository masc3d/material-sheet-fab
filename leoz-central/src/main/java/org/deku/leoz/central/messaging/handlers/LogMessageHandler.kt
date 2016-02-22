package org.deku.leoz.central.messaging.handlers

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import org.apache.commons.io.FilenameUtils
import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.config.StorageConfiguration
import org.deku.leoz.log.LogMessage
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.event.Level
import sx.jms.Converter
import sx.jms.Handler
import java.util.*
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.Session

/**
 * Created by masc on 19/02/16.
 */
class LogMessageHandler : Handler<LogMessage> {
    private val log = LogFactory.getLog(this.javaClass)

    /** Loggers by node id */
    private val loggers = HashMap<Int, Logger>()

    private class LoggingEvent(val logEntry: LogMessage.LogEntry) : org.slf4j.event.LoggingEvent {
        val levels = mapOf(
                Level.ERROR.toString() to Level.ERROR,
                Level.DEBUG.toString() to Level.DEBUG,
                Level.INFO.toString() to Level.INFO,
                Level.WARN.toString() to Level.WARN,
                Level.TRACE.toString() to Level.TRACE)

        override fun getMessage(): String? {
            return this.logEntry.message
        }

        override fun getTimeStamp(): Long {
            return this.logEntry.timestamp
        }

        override fun getThreadName(): String? {
            return this.logEntry.threadName
        }

        override fun getMarker(): Marker? {
            return null
        }

        override fun getArgumentArray(): Array<out Any>? {
            return null
        }

        override fun getLoggerName(): String? {
            return this.logEntry.loggerName
        }

        override fun getThrowable(): Throwable? {
            return null
        }

        override fun getLevel(): Level? {
            return this.levels.get(this.logEntry.level)
        }
    }

    /**
     * Creatze new logger for node
     * @param nodeId Node id
     */
    private fun createLogger(nodeId: Int): Logger {
        val logger = LoggerFactory.getLogger("leoz-node-1") as Logger
        logger.isAdditive = false

        val fileAppender: RollingFileAppender<ILoggingEvent> = RollingFileAppender()
        fileAppender.context = logger.loggerContext
        fileAppender.file = StorageConfiguration.instance.logDirectory.resolve("nodes").resolve("leoz-node-${nodeId}.log").toString()

        // Encoder
        val encoder = PatternLayoutEncoder()
        encoder.context = logger.loggerContext
        encoder.pattern = "%d %r %thread %logger %level - %msg%n"
        encoder.start()
        fileAppender.encoder = encoder

        // Rolling policy
        val baseFilename = FilenameUtils.removeExtension(fileAppender.rawFileProperty())
        val extension = FilenameUtils.getExtension(fileAppender.rawFileProperty())

        val rollingPolicy = TimeBasedRollingPolicy<ILoggingEvent>()
        rollingPolicy.context = logger.loggerContext
        rollingPolicy.setParent(fileAppender)
        rollingPolicy.maxHistory = 10
        rollingPolicy.fileNamePattern = "${baseFilename}-%d{yyyy-MM-dd}"
        if (extension.length > 0)
            rollingPolicy.fileNamePattern += ".${extension}"
        rollingPolicy.start()
        fileAppender.rollingPolicy = rollingPolicy
        fileAppender.triggeringPolicy = rollingPolicy

        // Initialize file appender
        logger.addAppender(fileAppender)
        fileAppender.start()

        return logger
    }

    /**
     * Message handler
     */
    override fun onMessage(message: LogMessage, converter: Converter, jmsMessage: Message, session: Session, connectionFactory: ConnectionFactory) {
        try {
            log.info("Received ${message.logEntries.count()} log messages from node [${message.nodeId}]")

            if (message.logEntries.count() == 0)
                return

            var logger: Logger? = null
            synchronized(this.loggers) {
                logger = this.loggers.getOrPut(message.nodeId!!, {
                    this.createLogger(message.nodeId!!)
                })
            }

            message.logEntries.forEach {
                logger!!.log(LoggingEvent(it))
            }
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }
}