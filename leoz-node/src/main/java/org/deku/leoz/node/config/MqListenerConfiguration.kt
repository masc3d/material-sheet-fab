package org.deku.leoz.node.config

import org.deku.leoz.config.JmsConfiguration
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.node.Application
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import sx.mq.MqBroker
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.jms.listeners.SpringJmsListener
import java.util.concurrent.ExecutorService
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * Message listener configuration.
 * Initializes message listener(s) and their message handlers
 * Created by masc on 20.06.15.
 */
@Profile(Application.PROFILE_CLIENT_NODE)
@Configuration
@Service
@Lazy(false)
open class MqListenerConfiguration {
    private val log = LoggerFactory.getLogger(MqListenerConfiguration::class.java)

    @Inject
    private lateinit var application: Application

    @Inject
    private lateinit var executorService: ExecutorService

    @Inject
    private lateinit var mqConfiguration: JmsConfiguration

    // Listeners
    val nodeQueueListener by lazy {
        SpringJmsListener(
                JmsEndpoints.node.queue(this.application.identity.uid),
                executorService)
    }

    val nodeTopicListener by lazy {
        SpringJmsListener(
                JmsEndpoints.node.topic,
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
    private var brokerEventListener: MqBroker.EventListener = object : MqBroker.DefaultEventListener() {
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

        if (mqConfiguration.broker.isStarted) {
            this.nodeQueueListener.start()
            this.nodeTopicListener.start()
        }
    }

    /**
     * Stop message listener
     */
    @Synchronized private fun stop() {
        this.nodeQueueListener.stop()
        this.nodeTopicListener.stop()
    }
    //endregion
}
