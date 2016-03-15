package org.deku.leoz.central.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.config.messaging.ActiveMQConfiguration
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

    /**
     * Central queue listener
     */
    val centralQueueListener: SpringJmsListener by lazy {
        object : SpringJmsListener(
                channel = { Channel(ActiveMQConfiguration.instance.centralQueue) },
                executor = this.executorService) {}
    }

    /**
     * Central log queue listener
     */
    val centralLogQueueListener: SpringJmsListener by lazy {
        object : SpringJmsListener(
                channel = { Channel(ActiveMQConfiguration.instance.centralLogQueue) },
                executor = this.executorService) {}
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
    fun onInitialize() {
        // Hook up with broker events
        ActiveMQBroker.instance.delegate.add(brokerEventListener)
        this.startIfReady()
    }

    @PreDestroy
    fun onDestroy() {
        this.centralQueueListener.close()
        this.centralLogQueueListener.close()
    }
    //endregion
}
