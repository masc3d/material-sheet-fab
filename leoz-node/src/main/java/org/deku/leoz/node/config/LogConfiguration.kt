package org.deku.leoz.node.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.log.LogAppender
import org.deku.leoz.node.Application
import org.slf4j.LoggerFactory

/**
 * Log configuration.
 * Not maintained by spring, as it's required earlier during application startup
 * Created by masc on 24-Jul-15.
 */
open class LogConfiguration : org.deku.leoz.config.LogConfiguration() {
    private var log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val module = Kodein.Module {
            bind<LogConfiguration>() with eagerSingleton {
                LogConfiguration()
            }
        }
    }

    /** Jms log appender */
    private var jmsLogAppender: LogAppender? = null

    /**
     * Enable or disable jms log appender
     * */
    var jmsAppenderEnabled: Boolean = false
        set(value: Boolean) {
            field = value
            if (value) {
                if (this.jmsLogAppender == null) {
                    val application: Application = Kodein.global.instance()
                    // Setup message log appender
                    this.jmsLogAppender = LogAppender(
                            broker = ActiveMQConfiguration.instance.broker,
                            logChannelConfiguration = ActiveMQConfiguration.instance.centralLogQueue,
                            identitySupplier = { application.identity })
                    this.jmsLogAppender!!.context = loggerContext
                    this.jmsLogAppender!!.start()
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
    }

    /**
     * Initialize logging
     */
    override fun initialize() {
        super.initialize()

        val storageConfiguration: StorageConfiguration = Kodein.global.instance()
        if (this.logFile == null) {
            this.logFile = storageConfiguration.logFile
        }

        if (this.jmsAppenderEnabled) {
            this.jmsLogAppender!!.start()
            this.rootLogger.addAppender(this.jmsLogAppender)
        }
    }

    /**
     * Dispose loggers
     */
    override fun close() {
        super.close()
    }
}
