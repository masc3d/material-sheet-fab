package org.deku.leo2.node

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leo2.messaging.activemq.ActiveMQContext
import org.deku.leo2.messaging.log.LogAppender
import org.slf4j.LoggerFactory
import sx.Disposable
import kotlin.platform.platformStatic

/**
 * Log configuration
 * Created by masc on 24-Jul-15.
 */
class LogConfiguration : Disposable {
    private var log: Log = LogFactory.getLog(this.javaClass)

    companion object Singleton {
        private val instance: LogConfiguration = LogConfiguration()
        @platformStatic fun instance(): LogConfiguration {
            return this.instance;
        }
    }

    private var rootLogger: Logger
    private var loggerContext: LoggerContext

    private var jmsLogAppender: LogAppender? = null
    private var fileAppender: RollingFileAppender<ILoggingEvent>

    /**
     * c'tor
     */
    private constructor() {
        this.rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
        this.loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext

        // region File appender
        this.fileAppender = RollingFileAppender()
        this.fileAppender.setContext(loggerContext)
        this.fileAppender.setFile(LocalStorage.instance().logFile.toString())

        // Encoder
        val encoder = PatternLayoutEncoder()
        encoder.setContext(this.loggerContext)
        encoder.setPattern("%d %r %thread %level - %msg%n")
        encoder.start()
        this.fileAppender.setEncoder(encoder)

        // Rolling policy
        val rollingPolicy = TimeBasedRollingPolicy<ILoggingEvent>()
        rollingPolicy.setContext(this.loggerContext)
        rollingPolicy.setParent(this.fileAppender)
        rollingPolicy.setMaxHistory(10)
        rollingPolicy.setFileNamePattern("${this.fileAppender.rawFileProperty()}-%d{yyyy-MM-dd}")
        rollingPolicy.start()
        this.fileAppender.setRollingPolicy(rollingPolicy)
        this.fileAppender.setTriggeringPolicy(rollingPolicy)
        // endregion

        // Jms appender
        if (App.instance().getProfile() === App.PROFILE_CLIENT_NODE) {
            // Setup message log appender
            this.jmsLogAppender = LogAppender(ActiveMQContext.instance())
            this.jmsLogAppender!!.setContext(loggerContext)
        }
    }

    /**
     * Initialize logging
     * @param withJmsAppender Initialize jms appender (defaults to true)
     */
    public fun initialize(withJmsAppender: Boolean = true) {
        // Initialize file appender
        this.fileAppender.start()
        this.rootLogger.addAppender(this.fileAppender)

        // Initialize jms appender
        if (this.jmsLogAppender != null) {
            this.jmsLogAppender!!.start()
            this.rootLogger.addAppender(this.jmsLogAppender)
        }
    }
    public fun initialize() {
        this.initialize(withJmsAppender = true)
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
