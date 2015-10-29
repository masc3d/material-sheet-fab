package org.deku.leoz.central.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.data.repositories.NodeRepository
import org.deku.leoz.central.messaging.handlers.IdentityMessageHandler
import org.deku.leoz.central.messaging.handlers.UpdateInfoRequestHandler
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.node.messaging.entities.IdentityMessage
import org.deku.leoz.update.UpdateInfoRequest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.converters.DefaultConverter
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker
import sx.jms.listeners.SpringJmsListener
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
import javax.jms.Destination

/**
 * Leoz-central message listener configuration
 * Created by masc on 20.06.15.
 */
@Configuration
@Lazy(false)
open class MessageListenerConfiguration {
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    private lateinit var nodeRepository: NodeRepository

    /** Central message listener  */
    private val centralQueueListener: SpringJmsListener

    init {
        // Configure and create listeners
        centralQueueListener = object : SpringJmsListener(ActiveMQConfiguration.instance.broker.connectionFactory) {
            init {
                this.converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP)
            }

            override fun createDestination(): Destination? {
                return ActiveMQConfiguration.instance.centralQueue
            }
        }
    }

    private fun initializeListener() {
        // Add message handler delegatess
        centralQueueListener.addDelegate(
                IdentityMessage::class.java,
                IdentityMessageHandler(nodeRepository))

        centralQueueListener.addDelegate(
                UpdateInfoRequest::class.java,
                UpdateInfoRequestHandler())
    }

    //region Lifecycle
    /**
     * Broker event listener
     */
    internal var brokerEventListener: Broker.EventListener = object : Broker.EventListener {
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
            centralQueueListener.start()
        }
    }

    /**
     * Stop message listener
     */
    private fun stop() {
        centralQueueListener.stop()
    }

    @PostConstruct
    fun onInitialize() {
        log.info("Initializing central message listener")

        // Hook up with broker events
        ActiveMQBroker.instance().delegate.add(brokerEventListener)

        this.startIfReady()
    }

    @PreDestroy
    fun onDestroy() {
        centralQueueListener.dispose()
    }
    //endregion
}
