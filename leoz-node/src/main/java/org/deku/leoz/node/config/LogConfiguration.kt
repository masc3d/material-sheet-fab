package org.deku.leoz.node.config

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.log.LogAppender

/**
 * Log configuration.
 * Not maintained by spring, as it's required earlier during application startup
 * Created by masc on 24-Jul-15.
 */
open class LogConfiguration : org.deku.leoz.config.LogConfiguration() {
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
                    this.jmsLogAppender = LogAppender(ActiveMQConfiguration.instance)
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
        this.logFile = StorageConfiguration.instance.get().logFile
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
