package org.deku.leoz.config

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import org.apache.commons.io.FilenameUtils
import org.slf4j.LoggerFactory
import sx.Disposable
import java.io.File

/**
 * Base class for leoz Log configuration
 * Created by masc on 24-Jul-15.
 */
abstract class LogConfiguration protected constructor() : Disposable {
    /** Root logger */
    protected var rootLogger: Logger
    /** Logger context */
    protected var loggerContext: LoggerContext
    /** File appender */
    protected var fileAppender: RollingFileAppender<ILoggingEvent>? = null

    /** Log file. Set to enable file logging */
    var logFile: File? = null
        set(value: File?) {
            if (this.fileAppender != null) {
                this.fileAppender!!.stop()
                rootLogger.detachAppender(this.fileAppender)
                this.fileAppender = null
            }

            if (value != null) {
                val fileAppender: RollingFileAppender<ILoggingEvent> = RollingFileAppender()

                fileAppender.context = loggerContext
                fileAppender.file = value.toString()

                // Encoder
                val encoder = PatternLayoutEncoder()
                encoder.context = this.loggerContext
                encoder.pattern = "%d %r %thread %logger %level - %msg%n"
                encoder.start()
                fileAppender.encoder = encoder

                // Rolling policy
                val baseFilename = FilenameUtils.removeExtension(fileAppender.rawFileProperty())
                val extension = FilenameUtils.getExtension(fileAppender.rawFileProperty())

                val rollingPolicy = TimeBasedRollingPolicy<ILoggingEvent>()
                rollingPolicy.context = this.loggerContext
                rollingPolicy.setParent(fileAppender)
                rollingPolicy.maxHistory = 10
                rollingPolicy.fileNamePattern = "${baseFilename}-%d{yyyy-MM-dd}"
                if (extension.isNotEmpty())
                    rollingPolicy.fileNamePattern += ".${extension}"
                rollingPolicy.start()
                fileAppender.rollingPolicy = rollingPolicy
                fileAppender.triggeringPolicy = rollingPolicy

                // Initialize file appender
                this.rootLogger.addAppender(fileAppender)
                fileAppender.start()

                this.fileAppender = fileAppender
            }

            field = value
        }

    /** Root logger level */
    var logLevel: Level
        get() = this.rootLogger.level
        set(v: Level) { this.rootLogger.level = v }

    /**
     * Initialize logging. This method is supposed to also reinitialize all configured loggers.
     */
    open fun initialize() {
        val fileAppender = this.fileAppender
        if (fileAppender != null) {
            this.rootLogger.addAppender(fileAppender)
            fileAppender.rollingPolicy.start()
            fileAppender.triggeringPolicy.start()
            fileAppender.start()
        }
    }

    /**
     * Add appender to log configuration
     */
    fun addAppender(appender: AppenderBase<ILoggingEvent>) {
        appender.context = this.loggerContext
        appender.start()
        this.rootLogger.addAppender(appender)
    }

    /**
     * Dispose loggers
     */
    override fun close() {
        this.logFile = null
    }

    init {
        this.rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        this.loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    }
}
