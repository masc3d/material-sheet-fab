package org.deku.leoz.central.config

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.data.repositories.NodeRepository
import org.deku.leoz.central.messaging.handler.IdentityMessageHandler
import org.deku.leoz.central.messaging.MessageListener
import org.deku.leoz.messaging.activemq.ActiveMQContext
import org.deku.leoz.node.messaging.auth.v1.IdentityMessage
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

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
    private val messageListener: MessageListener

    init {
        // Configure and create listener
        messageListener = MessageListener(ActiveMQContext.instance)
    }

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

        if (ActiveMQContext.instance.broker.isStarted) {

            // Add message handler delegatess
            messageListener.addDelegate(IdentityMessage::class.java,
                    IdentityMessageHandler(
                            nodeRepository))

            messageListener.start()
        }
    }

    /**
     * Stop message listener
     */
    private fun stop() {
        messageListener.stop()
    }

    @PostConstruct
    fun onInitialize() {
        log.info("Initializing central message listener")

        // Register event listeners
        ActiveMQBroker.instance().delegate.add(brokerEventListener)

        this.startIfReady()
    }

    @PreDestroy
    fun onDestroy() {
        messageListener.dispose()
    }
}
