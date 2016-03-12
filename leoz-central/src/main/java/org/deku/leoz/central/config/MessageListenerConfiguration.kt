package org.deku.leoz.central.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.entities.UpdateInfoRequest
import org.deku.leoz.central.messaging.handlers.AuthorizationRequestHandler
import org.deku.leoz.central.messaging.handlers.LogHandler
import org.deku.leoz.central.messaging.handlers.UpdateInfoRequestHandler
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.log.LogMessage
import org.deku.leoz.node.messaging.entities.AuthorizationRequestMessage
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.Channel
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker
import sx.jms.listeners.SpringJmsListener
import java.util.concurrent.ExecutorService
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Leoz-central message listener configuration
 * Created by masc on 20.06.15.
 */
@Configuration
@Lazy(false)
open class MessageListenerConfiguration {
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    private lateinit var executorService: ExecutorService

    @Inject
    private lateinit var authorizationHandler: AuthorizationRequestHandler

    @Inject
    private lateinit var updateInfoRequestHandler: UpdateInfoRequestHandler

    @Inject
    private lateinit var logHandler: LogHandler

    /** Central message listener  */
    private var centralQueueListener: SpringJmsListener by Delegates.notNull()

    private var logListener: SpringJmsListener by Delegates.notNull()

    init {
    }

    private fun initializeListener() {

        // Add message handler delegatess
        centralQueueListener.addDelegate(
                AuthorizationRequestMessage::class.java,
                authorizationHandler)

        centralQueueListener.addDelegate(
                UpdateInfoRequest::class.java,
                updateInfoRequestHandler)

        logListener.addDelegate(
                LogMessage::class.java,
                logHandler)
    }

    //region Lifecycle
    /**
     * Broker event listener
     */
    internal var brokerEventListener: Broker.EventListener = object : Broker.DefaultEventListener() {
        override fun onStart() {
            startIfReady()
        }

        override fun onStop() {
            stop()
        }
    }

    /**
     * Start message listener
     */
    private fun startIfReady() {
        this.stop()

        if (ActiveMQConfiguration.instance.broker.isStarted) {
            this.initializeListener()
            this.centralQueueListener.start()
            this.logListener.start()
        }
    }

    /**
     * Stop message listener
     */
    private fun stop() {
        this.centralQueueListener.stop()
        this.logListener.stop()
    }

    @PostConstruct
    fun onInitialize() {
        log.info("Initializing central message listener")
        // Central queue listener
        centralQueueListener = object : SpringJmsListener(
                channel = { Channel(ActiveMQConfiguration.instance.centralQueue) },
                executor = this.executorService) {}

        // Log queue listener
        logListener = object : SpringJmsListener(
                channel = { Channel(ActiveMQConfiguration.instance.centralLogQueue) },
                executor = this.executorService) {}


        // Hook up with broker events
        ActiveMQBroker.instance.delegate.add(brokerEventListener)

        this.startIfReady()
    }

    @PreDestroy
    fun onDestroy() {
        this.centralQueueListener.close()
        this.logListener.close()
    }
    //endregion
}
