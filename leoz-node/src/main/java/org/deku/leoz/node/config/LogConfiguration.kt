package org.deku.leoz.node.config

import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.global
import org.deku.leoz.config.JmsConfiguration
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.log.LogMqAppender
import org.deku.leoz.node.Application
import org.deku.leoz.node.Storage
import org.slf4j.LoggerFactory
import org.slf4j.bridge.SLF4JBridgeHandler
import sx.log.slf4j.installJulBridge
import sx.mq.MqBroker
import sx.mq.jms.channel
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
    private var logMqAppender: LogMqAppender? = null

    /**
     * Enable or disable jms log appender
     * */
    var jmsAppenderEnabled: Boolean = false
        set(value: Boolean) {
            field = value
            var appender = this.logMqAppender
            if (value) {
                if (appender == null) {
                    val application: Application = Kodein.global.instance()

                    // Setup message log appender
                    appender = LogMqAppender(
                            channelSupplier = { JmsEndpoints.central.transient.kryo.channel() },
                            identitySupplier = { application.identity })
                    appender.context = loggerContext

                    this.logMqAppender = appender

                }
                appender.start()
                if (false/**ActiveMQConfiguration.broker.isStarted*/) {
                    appender.dispatcher.start()
                }
            } else {
                if (appender != null) {
                    appender.stop()
                    rootLogger.detachAppender(appender)

                    this.logMqAppender = null
                }
            }
        }

    /**
     * Broker listener, jms destination is automatically created when broker start is detected
     */
    val brokerEventListener: MqBroker.EventListener = object : MqBroker.DefaultEventListener() {
        override fun onStart() {
            val appender = this@LogConfiguration.logMqAppender
            if (appender != null && JmsConfiguration.broker.isStarted) {
                appender.dispatcher.start()
            }
        }

        override fun onStop() {
            this@LogConfiguration.logMqAppender?.stop()
        }
    }

    init {
        JmsConfiguration.broker.delegate.add(
                this.brokerEventListener)
    }

    /**
     * Initialize logging
     */
    override fun initialize() {
        super.initialize()

        // Setup jul to slf4j bridge
        log.installJulBridge()

        // Setup log file and jms appender
        val storageConfiguration: Storage = Kodein.global.instance()
        if (this.logFile == null) {
            this.logFile = storageConfiguration.logFile
        }

        val appender = this.logMqAppender
        if (appender != null) {
            appender.start()
            if (JmsConfiguration.broker.isStarted)
                appender.dispatcher.start()
            this.rootLogger.addAppender(this.logMqAppender)
        }
    }

    /**
     * Dispose loggers
     */
    override fun close() {
        super.close()
    }
}
