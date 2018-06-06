package org.deku.leoz.central.config

import org.deku.leoz.central.data.isJooqAccessException
import org.deku.leoz.config.JmsEndpoints
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.lang.close
import sx.mq.MqBroker
import sx.mq.jms.JmsListener
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.jms.listeners.SpringJmsListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
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
    private val log = LoggerFactory.getLogger(MqListenerConfiguration::class.java)

    @Inject
    private lateinit var executorService: ExecutorService

    @Inject
    private lateinit var broker: ActiveMQBroker

    /**
     * Central queue listener
     */
    @get:Bean
    val centralMainListener
        get() =
            SpringJmsListener(
                    endpoint = JmsEndpoints.central.main.kryo,
                    executor = Executors.newSingleThreadExecutor(), //this.executorService,
                    onError = this::onError
            )

    /**
     * Central log queue listener
     */
    @get:Bean
    val centralTransientListener
        get() =
            SpringJmsListener(
                    endpoint = JmsEndpoints.central.transient.kryo,
                    executor = this.executorService,
                    onError = this::onError
            )

    @Inject
    private lateinit var listeners: List<JmsListener>

    /**
     * Central jms error handler
     */
    private fun onError(t: Throwable) {
        if (t.isJooqAccessException()) {
            log.error(t.message)
        } else
            log.error(t.message, t)
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
            this.listeners.forEach { it.start() }
        }
    }

    /**
     * Stop message listener
     */
    private fun stop() {
        this.listeners
                .filter { it.isRunning }
                .map { it as AutoCloseable }
                .close()
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
