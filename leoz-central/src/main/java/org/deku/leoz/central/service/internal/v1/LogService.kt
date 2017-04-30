package org.deku.leoz.central.service.internal.v1

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import org.apache.commons.io.FilenameUtils
import org.deku.leoz.Identity
import org.deku.leoz.log.LogMessage
import org.deku.leoz.node.Storage
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import sx.jms.Channel
import sx.jms.Handler
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Named

/**
 * Log message service
 * Created by masc on 19/02/16.
 */
@Named
class LogService
:
        Handler<LogMessage> {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var storage: Storage

    /** Loggers by node id */
    private val loggers = HashMap<String, Logger>()

    // TODO: move to LogMessage.LogEntry
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
    private fun getLogger(name: String): Logger {
        return this.loggers.getOrPut(name, {
            this.createLogger(name)
        })
    }

    /**
     * Create logger name
     */
    private fun createName(key: String): String {
        return "leoz-node-${key}"
    }

    /**
     * Get log file
     * @param baseName Base name without extension
     */
    private fun getLogFile(baseName: String): File {
        return storage.logDirectory.resolve("nodes").resolve("${baseName}.log")
    }

    /**
     * Message handler
     */
    override fun onMessage(message: LogMessage, replyChannel: Channel?) {
        try {
            val identityKey = Identity.Key(message.nodeKey)
            log.debug("Received ${message.logEntries.count()} log messages from node [${identityKey}]")

            if (message.logEntries.count() == 0)
                return

            var logger: Logger? = null

            val keyBasedName = this.createName(key = identityKey.short)

            synchronized(loggers) {
                logger = this.getLogger(keyBasedName)
            }

            message.logEntries.forEach {
                logger!!.callAppenders(LoggingEvent(it))
            }
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }
}