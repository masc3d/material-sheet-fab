package org.deku.leoz.node.config

import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.global
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.log.LogAppender
import org.deku.leoz.node.Application
import org.deku.leoz.node.Storage
import org.slf4j.LoggerFactory
import org.slf4j.bridge.SLF4JBridgeHandler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

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
                            broker = ActiveMQConfiguration.broker,
                            logChannelConfiguration = ActiveMQConfiguration.centralLogQueue,
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

        // Setup jul to slf4j bridge
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        Logger.getLogger("global").setLevel(Level.FINEST);

        // Setup log file and jms appender
        val storageConfiguration: Storage = Kodein.global.instance()
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
