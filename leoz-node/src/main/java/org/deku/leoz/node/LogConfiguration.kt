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
class LogConfiguration : org.deku.leoz.LogConfiguration() {
    private var log: Log = LogFactory.getLog(this.javaClass)

    companion object Singleton {
        private val instance: LogConfiguration = LogConfiguration()
        @JvmStatic fun instance(): LogConfiguration {
            return this.instance;
        }
    }

    /** Jms log appender */
    private var jmsLogAppender: LogAppender? = null

    /**
     * Enable or disable jms log appender
     * */
    var jmsAppenderEnabled: Boolean = false
        set(value: Boolean) {
            if (value) {
                if (this.jmsLogAppender == null) {
                    // Setup message log appender
                    this.jmsLogAppender = LogAppender(ActiveMQContext.instance())
                    this.jmsLogAppender!!.context = loggerContext
                }
            } else {
                if (this.jmsLogAppender != null) {
                    this.jmsLogAppender!!.stop()
                    rootLogger.detachAppender(this.jmsLogAppender)
                    this.jmsLogAppender = null
                }
            }
        }

    init {
        this.logFile = LocalStorage.instance.logFile
    }

    /**
     * Initialize logging
     */
    override fun initialize() {
        super.initialize()

        if (this.jmsAppenderEnabled) {
            this.jmsLogAppender!!.start()
            this.rootLogger.addAppender(this.jmsLogAppender)
        }
    }

    /**
     * Dispose loggers
     */
    override fun dispose() {
        this.jmsAppenderEnabled = false
        super.dispose()
    }
}
