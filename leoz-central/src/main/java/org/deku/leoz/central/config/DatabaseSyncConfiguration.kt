package org.deku.leoz.central.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.services.DatabaseSyncService
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Leoz-central database sync configuration
 * Created by masc on 30.06.15.
 */
@Configuration
@Lazy(false)
open class DatabaseSyncConfiguration {
    private var log = LogFactory.getLog(this.javaClass)

    @Inject
    private lateinit var databaseSyncService: DatabaseSyncService

    /** Broker event listener  */
    private val brokerEventListener = object : Broker.DefaultEventListener() {
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
