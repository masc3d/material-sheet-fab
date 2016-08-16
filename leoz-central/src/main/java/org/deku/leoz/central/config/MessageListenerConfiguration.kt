package org.deku.leoz.central.config

import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.Channel
import sx.jms.Broker
import sx.jms.activemq.ActiveMQBroker
import sx.jms.listeners.SpringJmsListener
import java.util.concurrent.ExecutorService
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * Leoz-central message listener configuration
 * Created by masc on 20.06.15.
 */
@Configuration
@Lazy(false)
open class MessageListenerConfiguration : org.deku.leoz.node.config.MessageListenerConfiguration() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var executorService: ExecutorService

    /**
     * Central queue listener
     */
    val centralQueueListener: SpringJmsListener by lazy {
        SpringJmsListener(
                channel = { Channel(ActiveMQConfiguration.instance.centralQueue) },
                executor = this.executorService)
    }

    /**
     * Central log queue listener
     */
    val centralLogQueueListener: SpringJmsListener by lazy {
        SpringJmsListener(
                channel = { Channel(ActiveMQConfiguration.instance.centralLogQueue) },
                executor = this.executorService)
    }

    //region Lifecycle
    /**
     * Broker event listener
     */
    private val brokerEventListener: Broker.EventListener = object : Broker.DefaultEventListener() {
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
            this.centralQueueListener.start()
            this.centralLogQueueListener.start()
        }
    }

    /**
     * Stop message listener
     */
    private fun stop() {
        this.centralQueueListener.stop()
        this.centralLogQueueListener.stop()
    }

    @PostConstruct
    override fun onInitialize() {
        // Hook up with broker events
        ActiveMQBroker.instance.delegate.add(brokerEventListener)
        this.startIfReady()
    }

    @PreDestroy
    override fun onDestroy() {
        this.stop()
    }
    //endregion
}
