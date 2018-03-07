package org.deku.leoz.central.config

import org.deku.leoz.config.JmsEndpoints
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.mq.MqBroker
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.jms.listeners.SpringJmsListener
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
class MqListenerConfiguration : org.deku.leoz.node.config.MqListenerConfiguration() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var executorService: ExecutorService
    
    @Inject
    private lateinit var broker: ActiveMQBroker

    /**
     * Central queue listener
     */
    val centralQueueListener by lazy {
        SpringJmsListener(
                endpoint = JmsEndpoints.central.main.kryo,
                executor = this.executorService)
    }

    /**
     * Central log queue listener
     */
    val centralTransientQueueListener by lazy {
        SpringJmsListener(
                endpoint = JmsEndpoints.central.transient.kryo,
                executor = this.executorService)
    }

    //region Lifecycle
    /**
     * Broker event listener
     */
    private val brokerEventListener: MqBroker.EventListener = object : MqBroker.DefaultEventListener() {
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

        if (this.broker.isStarted) {
            this.centralQueueListener.start()
            this.centralTransientQueueListener.start()
        }
    }

    /**
     * Stop message listener
     */
    private fun stop() {
        this.centralQueueListener.stop()
        this.centralTransientQueueListener.stop()
    }

    @PostConstruct
    override fun onInitialize() {
        // Hook up with broker events
        this.broker.delegate.add(brokerEventListener)
        this.startIfReady()
    }

    @PreDestroy
    override fun onDestroy() {
        this.stop()
    }
    //endregion
}
