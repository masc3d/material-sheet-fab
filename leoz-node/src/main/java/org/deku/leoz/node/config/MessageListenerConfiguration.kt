package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import sx.jms.Channel
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker
import sx.jms.listeners.SpringJmsListener
import java.util.concurrent.ExecutorService
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * Message listener configuration.
 * Initializes message listener(s) and their message handlers
 * Created by masc on 20.06.15.
 */
@Profile(App.PROFILE_CLIENT_NODE)
@Configuration
@Service
@Lazy(false)
open class MessageListenerConfiguration {
    private val log = LogFactory.getLog(MessageListenerConfiguration::class.java)

    @Inject
    private lateinit var executorService: ExecutorService

    // Listeners
    val nodeQueueListener by lazy {
        SpringJmsListener(
                { Channel(ActiveMQConfiguration.instance.nodeQueue(App.instance.identity.shortKey)) },
                executorService)
    }

    val nodeNotificationListener by lazy {
        SpringJmsListener(
                { Channel(ActiveMQConfiguration.instance.nodeNotificationTopic) },
                executorService)
    }

    //region Lifecycle
    @PostConstruct
    open fun onInitialize() {
        // Register event listeners
        ActiveMQBroker.instance.delegate.add(brokerEventListener)
        this.startIfReady()
    }

    @PreDestroy
    open fun onDestroy() {
        this.stop()
    }

    /**
     * Broker event listener
     */
    private var brokerEventListener: Broker.EventListener = object : Broker.DefaultEventListener() {
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
    @Synchronized private fun startIfReady() {
        this.stop()

        if (ActiveMQConfiguration.instance.broker.isStarted) {
            this.nodeQueueListener.start()
            this.nodeNotificationListener.start()
        }
    }

    /**
     * Stop message listener
     */
    @Synchronized private fun stop() {
        this.nodeQueueListener.stop()
        this.nodeNotificationListener.stop()
    }
    //endregion
}
