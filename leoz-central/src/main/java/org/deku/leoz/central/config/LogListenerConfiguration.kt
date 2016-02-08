package org.deku.leoz.central.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.log.LogListener
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Leoz-central log listener configuration
 * Created by masc on 29.06.15.
 */
@Configuration
@Lazy(false)
open class LogListenerConfiguration {
    internal var log = LogFactory.getLog(this.javaClass)

    /** Log listener instance  */
    private val logListener: LogListener

    init {
        logListener = LogListener(ActiveMQConfiguration.instance)
    }

    private val brokerEventListener = object : Broker.DefaultEventListener() {
        override fun onStart() {
            logListener.start()
        }

        override fun onStop() {
            logListener.stop()
        }
    }

    @PostConstruct
    fun onInitialize() {
        // Register to broker start
        ActiveMQBroker.instance().delegate.add(brokerEventListener)
    }

    @PreDestroy
    fun onDestroy() {
        logListener.close()
    }
}
