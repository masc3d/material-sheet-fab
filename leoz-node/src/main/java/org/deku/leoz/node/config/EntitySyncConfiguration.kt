package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.data.entities.master.*
import org.deku.leoz.node.data.sync.EntityConsumer
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit
import kotlin.properties.Delegates

/**
 * Entity synchronization configuration.
 * Sets up entity/database synchronization over the message bus.
 * Created by masc on 20.06.15.
 */
@Configuration(App.PROFILE_CLIENT_NODE)
@Profile(App.PROFILE_CLIENT_NODE)
@Lazy(false)
open class EntitySyncConfiguration {
    private val log = LogFactory.getLog(this.javaClass)

    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    private lateinit var entityManagerFactory: EntityManagerFactory

    /** Entity sync consumer */
    private var entityConsumer: EntityConsumer by Delegates.notNull()

    /** Broker listener  */
    private val brokerEventListener = object : Broker.EventListener {
        override fun onStart() {
            log.info("Detected broker start, initializing entity sync")
            entityConsumer.start()
            entityConsumer.request(Station::class.java)
            entityConsumer.request(Country::class.java)
            entityConsumer.request(HolidayCtrl::class.java)
            entityConsumer.request(Route::class.java)
            entityConsumer.request(Sector::class.java)
        }

        override fun onStop() {
            entityConsumer.stop()
        }
    }

    @PostConstruct
    fun onInitialize() {
        // Start when broker is started
        ActiveMQBroker.instance().delegate.add(brokerEventListener)
        if (ActiveMQBroker.instance().isStarted)
            brokerEventListener.onStart()

        // Entity sync consumer
        this.entityConsumer = EntityConsumer(ActiveMQConfiguration.instance, this.entityManagerFactory)
    }

    @PreDestroy
    fun onDestroy() {
        entityConsumer.dispose()
    }
}
