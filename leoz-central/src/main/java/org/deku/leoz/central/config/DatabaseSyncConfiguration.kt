package org.deku.leoz.central.config

import org.deku.leoz.central.service.internal.sync.DatabaseSyncService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.mq.MqBroker
import sx.mq.jms.activemq.ActiveMQBroker
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Leoz-central database sync configuration
 * Created by masc on 30.06.15.
 */
@Configuration
@Lazy(false)
open class DatabaseSyncConfiguration {
    private var log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var databaseSyncService: DatabaseSyncService

    /** Broker event listener  */
    private val brokerEventListener = object : MqBroker.DefaultEventListener() {
        override fun onStart() {
            databaseSyncService.start()
        }

        override fun onStop() {
            databaseSyncService.stop()
        }
    }

    /**
     * On initialization
     */
    @PostConstruct
    fun onInitialize() {
        // Wire broker event
        ActiveMQBroker.instance.delegate.add(brokerEventListener)

        if (ActiveMQBroker.instance.isStarted)
            brokerEventListener.onStart()
    }
}
