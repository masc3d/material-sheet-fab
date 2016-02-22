package org.deku.leoz.central.messaging.handlers

import ch.qos.logback.classic.Level
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
import sx.jms.Converter
import sx.jms.Handler
import java.io.File
import java.util.*
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.Session

/**
 * Log message handler
 * Created by masc on 19/02/16.
 */
class LogMessageHandler : Handler<LogMessage> {
    private val log = LogFactory.getLog(this.javaClass)

    /** Loggers by node id */
    private val loggers = HashMap<String, Logger>()

    private class LoggingEvent(val logEntry: LogMessage.LogEntry) : ch.qos.logback.classic.spi.LoggingEvent() {
        val levels = mapOf(
                Level.ERROR.toString() to Level.ERROR,
                Level.DEBUG.toString() to Level.DEBUG,
                Level.INFO.toString() to Level.INFO,
                Level.WARN.toString() to Level.WARN,
                Level.TRACE.toString() to Level.TRACE)

        override fun getMessage(): String? {
            return this.logEntry.message
        }

        override fun getFormattedMessage(): String? {
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

        override fun getLevel(): Level {
            return this.levels.get(this.logEntry.level) ?: Level.ALL
        }
    }

    /**
     * Creatze new logger for node
     * @param name Name of the logger and log file
     */
    private fun createLogger(name: String): Logger {
        val logger = LoggerFactory.getLogger(name) as Logger
        logger.isAdditive = false

        val fileAppender: RollingFileAppender<ILoggingEvent> = RollingFileAppender()
        fileAppender.context = logger.loggerContext
        fileAppender.file = this.getLogFile(name).toString()

        // Encoder
        val encoder = PatternLayoutEncoder()
        encoder.context = logger.loggerContext
        encoder.pattern = "%d %thread %logger %level - %msg%n"
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
     * Get logger
     * @param name Base name
     */
    private fun getLogger(name: String): Logger {
        return this.loggers.getOrPut(name, {
            this.createLogger(name)
        })
    }

    /**
     * Remove logger and stop appenders
     * @param name Base name
     */
    private fun removeLogger(name: String) {
        val logger = this.loggers.get(name)
        if (logger != null) {
            logger.detachAndStopAllAppenders()
            this.loggers.remove(name)
        }
    }

    /**
     * Create logger name
     */
    private fun createName(nodeId: Int? = null, nodeKey: String): String {
        return "leoz-node-${if (nodeId != null) nodeId.toString() else nodeKey}"
    }

    /**
     * Get log file
     * @param baseName Base name without extension
     */
    private fun getLogFile(baseName: String): File {
        return StorageConfiguration.instance.logDirectory.resolve("nodes").resolve("${baseName}.log")
    }

    /**
     * Message handler
     */
    override fun onMessage(message: LogMessage, converter: Converter, jmsMessage: Message, session: Session, connectionFactory: ConnectionFactory) {
        try {
            log.info("Received ${message.logEntries.count()} log messages from node [${message.nodeId}] key [${message.nodeKey}]")

            if (message.logEntries.count() == 0)
                return

            var logger: Logger? = null

            val keyBasedName = this.createName(nodeKey = message.nodeKey)
            val keyBasedFile = this.getLogFile(keyBasedName)

            synchronized(loggers) {
                if (message.nodeId != null) {
                    val idBasedName = this.createName(message.nodeId, message.nodeKey)
                    val idBasedFile = this.getLogFile(idBasedName)

                    if (keyBasedFile.exists()) {
                        this.removeLogger(idBasedName)
                        this.removeLogger(keyBasedName)
                        keyBasedFile.renameTo(idBasedFile)
                    }

                    logger = this.getLogger(idBasedName)
                } else {
                    logger = this.getLogger(keyBasedName)
                }
            }

            message.logEntries.forEach {
                logger!!.callAppenders(LoggingEvent(it))
            }
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }
}