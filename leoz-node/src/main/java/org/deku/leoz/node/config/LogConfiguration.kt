package org.deku.leoz.node.config

import ch.qos.logback.classic.Level
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.eagerSingleton
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.config.JmsConfiguration
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.log.LogMqAppender
import org.deku.leoz.node.Application
import org.deku.leoz.node.Storage
import org.slf4j.LoggerFactory
import sx.log.slf4j.installJulBridge
import sx.logback.IgnoreFilter
import sx.logback.IgnoreFilterCondition
import sx.mq.MqBroker
import sx.mq.jms.channel

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

    /** List of static ignore conditions */
    private val ignoreConditions = listOf<IgnoreFilterCondition>(
            // Error thrown by resteasy-4.0 (beta) when SSE connection breaks (trivial)
            IgnoreFilterCondition(
                    level = Level.ERROR,
                    name = "org.jboss.resteasy.resteasy_jaxrs.i18n",
                    message = "RESTEASY002030: Failed to write event org.jboss.resteasy.plugins.providers.sse"
            ),
            // Error thrown by activemq on remote node connection / quality issues (trivial)
            IgnoreFilterCondition(
                    level = Level.ERROR,
                    name = "org.apache.activemq.broker.TransportConnector",
                    message = "Could not accept connection"
            ),
            // Ignore activemq link stealing warnings, as those are common for (mobile) mqtt consumers
            IgnoreFilterCondition(
                    level = Level.WARN,
                    name = "org.apache.activemq.broker.region.RegionBroker",
                    message ="Stealing link"
            )
    )

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
                if (false
                /**ActiveMQConfiguration.broker.isStarted*/
                ) {
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
                this.brokerEventListener
        )
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

        val ignoreFilter = IgnoreFilter(
                this.ignoreConditions
        )

        this.rootLogger.iteratorForAppenders().forEach {
            it.addFilter(ignoreFilter)
        }
    }

    /**
     * Dispose loggers
     */
    override fun close() {
        super.close()
    }
}
