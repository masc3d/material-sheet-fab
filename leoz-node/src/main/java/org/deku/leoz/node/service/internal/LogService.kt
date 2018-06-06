package org.deku.leoz.node.service.internal

import ch.qos.logback.classic.*
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import org.apache.commons.io.FilenameUtils
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.identity.Identity
import org.deku.leoz.log.LogMessage
import org.slf4j.LoggerFactory
import sx.log.slf4j.trace
import sx.mq.MqChannel
import sx.mq.MqHandler
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.core.Response

/**
 * Log message service
 * Created by masc on 19/02/16.
 */
@Named
class LogService
    :
        org.deku.leoz.service.internal.LogService,
        MqHandler<LogMessage> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var storage: org.deku.leoz.node.Storage

    data class Source(
            val nodeType: String,
            val nodeUid: String
    )

    /** Loggers by node id */
    private val loggers = java.util.HashMap<Source, Logger>()

    // TODO: move to LogMessage.LogEntry
    private class LoggingEvent(
            val entry: LogMessage.LogEntry
    ) : ch.qos.logback.classic.spi.LoggingEvent() {

        val levels = mapOf(
                Level.ERROR.toString() to Level.ERROR,
                Level.DEBUG.toString() to Level.DEBUG,
                Level.INFO.toString() to Level.INFO,
                Level.WARN.toString() to Level.WARN,
                Level.TRACE.toString() to Level.TRACE)

        override fun getMessage(): String? {
            return this.entry.message
        }

        override fun getFormattedMessage(): String? {
            return this.entry.message
        }

        override fun getTimeStamp(): Long {
            return this.entry.timestamp
        }

        override fun getThreadName(): String? {
            return this.entry.threadName
        }

        override fun getMarker(): org.slf4j.Marker? {
            return null
        }

        override fun getArgumentArray(): Array<out Any>? {
            return null
        }

        override fun getLoggerName(): String? {
            return this.entry.loggerName
        }

        override fun getLevel(): Level {
            return this.levels.get(this.entry.level) ?: Level.ALL
        }
    }

    private fun LogMessage.LogEntry.toLoggingEvent(): ch.qos.logback.classic.spi.LoggingEvent = LoggingEvent(this)

    /**
     * Get logger
     * @param source logging source
     */
    private fun logger(source: Source): Logger {
        return this.loggers.getOrPut(source, {
            val logger = LoggerFactory.getLogger(source.nodeUid) as Logger
            logger.isAdditive = false

            val fileAppender: RollingFileAppender<ILoggingEvent> = RollingFileAppender()
            fileAppender.context = logger.loggerContext
            fileAppender.file = this.logFile(source).toString()

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

            logger
        })
    }

    /**
     * Get log file
     * @param source logging source
     */
    private fun logFile(source: Source): java.io.File {
        return storage.logDirectory
                .resolve(source.nodeType)
                .resolve("${source.nodeType}-${source.nodeUid}.log")
    }

    //region Service
    override fun download(bundleType: BundleType?, userId: Long?, nodeUid: String?): Response {
        TODO("Not supported yet")
    }
    //endregion

    /**
     * Message handler
     */
    override fun onMessage(message: LogMessage, replyChannel: MqChannel?) {
        try {
            val identityUid = Identity.Uid(message.nodeUid)
            log.trace { "Received ${message.logEntries.count()} log messages from node [${identityUid}]" }

            if (message.logEntries.count() == 0)
                return

            val source = Source(
                    nodeType = message.nodeType,
                    nodeUid = identityUid.short
            )

            val logger: Logger = synchronized(loggers) {
                this.logger(source)
            }

            message.logEntries.forEach {
                logger.callAppenders(it.toLoggingEvent())
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}