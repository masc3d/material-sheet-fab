package org.deku.leoz.node

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.messaging.activemq.ActiveMQContext
import org.deku.leoz.messaging.log.LogAppender
import org.slf4j.LoggerFactory
import sx.Disposable

/**
 * Log configuration
 * Created by masc on 24-Jul-15.
 */
class LogConfiguration : Disposable {
    private var log: Log = LogFactory.getLog(this.javaClass)

    companion object Singleton {
        private val instance: LogConfiguration = LogConfiguration()
        @JvmStatic fun instance(): LogConfiguration {
            return this.instance;
        }
    }

    private var rootLogger: Logger
    private var loggerContext: LoggerContext
    private var jmsLogAppender: LogAppender? = null
    private var fileAppender: RollingFileAppender<ILoggingEvent>

    /** Enable support for jms log appender */
    var jmsAppenderEnabled: Boolean = false

    /**
     * c'tor
     */
    private constructor() {
        this.rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        this.loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext

        // region File appender
        this.fileAppender = RollingFileAppender()
        this.fileAppender.context = loggerContext
        this.fileAppender.file = LocalStorage.instance().logFile.toString()

        // Encoder
        val encoder = PatternLayoutEncoder()
        encoder.context = this.loggerContext
        encoder.pattern = "%d %r %thread %level - %msg%n"
        encoder.start()
        this.fileAppender.encoder = encoder

        // Rolling policy
        val rollingPolicy = TimeBasedRollingPolicy<ILoggingEvent>()
        rollingPolicy.context = this.loggerContext
        rollingPolicy.setParent(this.fileAppender)
        rollingPolicy.maxHistory = 10
        rollingPolicy.fileNamePattern = "${this.fileAppender.rawFileProperty()}-%d{yyyy-MM-dd}"
        rollingPolicy.start()
        this.fileAppender.rollingPolicy = rollingPolicy
        this.fileAppender.triggeringPolicy = rollingPolicy
        // endregion
    }

    /**
     * Initialize logging
     */
    fun initialize() {
        // Initialize file appender
        this.fileAppender.start()
        this.rootLogger.addAppender(this.fileAppender)

        if (this.jmsAppenderEnabled) {
            // Initialize jms appender
            if (this.jmsLogAppender == null) {
                // Setup message log appender
                this.jmsLogAppender = LogAppender(ActiveMQContext.instance())
                this.jmsLogAppender!!.context = loggerContext
            }
            this.jmsLogAppender!!.start()
            this.rootLogger.addAppender(this.jmsLogAppender)
        }
    }

    /**
     * Dispose loggers
     */
    override fun dispose() {
        if (this.jmsLogAppender != null) {
            this.jmsLogAppender!!.stop()
            rootLogger.detachAppender(this.jmsLogAppender)
        }

        fileAppender.stop()
        rootLogger.detachAppender(fileAppender)
    }
}
