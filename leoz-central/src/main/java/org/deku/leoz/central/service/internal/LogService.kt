package org.deku.leoz.central.service.internal

import ch.qos.logback.classic.*
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import org.apache.commons.io.FilenameUtils
import org.deku.leoz.identity.Identity
import org.deku.leoz.log.LogMessage
import org.slf4j.LoggerFactory
import sx.mq.MqChannel
import sx.mq.MqHandler

/**
 * Log message service
 * Created by masc on 19/02/16.
 */
@javax.inject.Named
class LogService
:
        MqHandler<LogMessage> {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @javax.inject.Inject
    private lateinit var storage: org.deku.leoz.node.Storage

    data class LoggerKey(
            val nodeType: String,
            val nodeUid: String)

    /** Loggers by node id */
    private val loggers = java.util.HashMap<LoggerKey, Logger>()

    // TODO: move to LogMessage.LogEntry
    private class LoggingEvent(val logEntry: LogMessage.LogEntry) :  ch.qos.logback.classic.spi.LoggingEvent() {
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

        override fun getMarker(): org.slf4j.Marker? {
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
     * Create new logger for node
     * @param name Name of the logger and log file
     */
    private fun createLogger(key: LoggerKey): Logger {
        val logger = LoggerFactory.getLogger(key.nodeUid) as Logger
        logger.isAdditive = false

        val fileAppender: RollingFileAppender<ILoggingEvent> = RollingFileAppender()
        fileAppender.context = logger.loggerContext
        fileAppender.file = this.getLogFile(key).toString()

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
        if (extension.isNotEmpty())
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
    private fun getLogger(key: LoggerKey): Logger {
        return this.loggers.getOrPut(key, {
            this.createLogger(key)
        })
    }

    /**
     * Get log file
     * @param baseName Base name without extension
     */
    private fun getLogFile(key: LoggerKey): java.io.File {
        return storage.logDirectory.resolve(key.nodeType).resolve("${key.nodeType}-${key.nodeUid}.log")
    }

    /**
     * Message handler
     */
    override fun onMessage(message: LogMessage, replyChannel: MqChannel?) {
        try {
            val identityUid = Identity.Uid(message.nodeUid)
            log.trace("Received ${message.logEntries.count()} log messages from node [${identityUid}]")

            if (message.logEntries.count() == 0)
                return

            var logger: Logger? = null

            val loggerKey = LoggerKey(
                    nodeType = message.nodeType,
                    nodeUid = identityUid.short
            )

            synchronized(loggers) {
                logger = this.getLogger(loggerKey)
            }

            message.logEntries.forEach {
                logger!!.callAppenders(LogService.LoggingEvent(it))
            }
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }
}