package org.deku.leoz.node.config

import org.deku.leoz.config.JmsConfiguration
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.node.Application
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import sx.lang.close
import sx.mq.MqBroker
import sx.mq.jms.JmsListener
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
@Profile(Application.PROFILE_NODE)
@Configuration
@Lazy(false)
class MqListenerConfiguration {
    private val log = LoggerFactory.getLogger(MqListenerConfiguration::class.java)

    @Inject
    private lateinit var application: Application

    @Inject
    private lateinit var executorService: ExecutorService

    @Inject
    private lateinit var mqConfiguration: JmsConfiguration

    // Listeners
    @Deprecated("Superseded by main / transient")
    @get:Bean
    val nodeQueueListener by lazy {
        SpringJmsListener(
                JmsEndpoints.node.queue(this.application.identity.uid),
                executorService)
    }

    @get:Bean
    val nodeMainListener by lazy {
        SpringJmsListener(
                JmsEndpoints.node.main(this.application.identity.uid).kryo,
                executorService)
    }

    @get:Bean
    val nodeTransientListener by lazy {
        SpringJmsListener(
                JmsEndpoints.node.transient(this.application.identity.uid).kryo,
                executorService)
    }

    @get:Bean
    val nodeBroadcastListener by lazy {
        SpringJmsListener(
                JmsEndpoints.node.broadcast,
                executorService)
    }

    @Lazy @Inject
    private lateinit var listeners: List<JmsListener>

    //region Lifecycle
    @PostConstruct
    fun onInitialize() {
        // Register event listeners
        ActiveMQBroker.instance.delegate.add(brokerEventListener)
        this.startIfReady()
    }

    @PreDestroy
    fun onDestroy() {
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
    @Synchronized
    private fun startIfReady() {
        this.stop()

        if (mqConfiguration.broker.isStarted) {
            this.listeners.forEach { it.start() }
        }
    }

    /**
     * Stop message listener
     */
    @Synchronized
    private fun stop() {
        this.listeners
                .filter { it.isRunning }
                .map { it as AutoCloseable }
                .close()
    }
    //endregion
}
