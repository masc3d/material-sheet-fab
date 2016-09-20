package org.deku.leoz.node.config

import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.data.entities.master.*
import org.deku.leoz.node.data.sync.EntityConsumer
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import sx.jms.Broker
import sx.jms.activemq.ActiveMQBroker
import java.util.concurrent.ScheduledExecutorService
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
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
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    private lateinit var entityManagerFactory: EntityManagerFactory

    /** Entity sync consumer */
    private var entityConsumer: EntityConsumer by Delegates.notNull()

    /** Broker listener  */
    private val brokerEventListener = object : Broker.DefaultEventListener() {
        override fun onStart() {
            log.info("Detected broker start, initializing entity sync")
            entityConsumer.start()
        }

        override fun onStop() {
            entityConsumer.stop()
        }

        override fun onConnectedToBrokerNetwork() {
            this@EntitySyncConfiguration.requestEntities()
        }
    }

    /**
     * Starts entity requests for all entity types
     */
    fun requestEntities() {
        entityConsumer.request(Station::class.java)
        entityConsumer.request(Country::class.java)
        entityConsumer.request(HolidayCtrl::class.java)
        entityConsumer.request(Route::class.java)
        entityConsumer.request(RoutingLayer::class.java)
        entityConsumer.request(Sector::class.java)
    }

    @PostConstruct
    fun onInitialize() {
        // Start when broker is started
        ActiveMQBroker.instance.delegate.add(brokerEventListener)
        if (ActiveMQBroker.instance.isStarted)
            brokerEventListener.onStart()

        // Entity sync consumer
        this.entityConsumer = EntityConsumer(
                notificationChannelConfiguration = ActiveMQConfiguration.instance.entitySyncTopic,
                requestChannelConfiguration = ActiveMQConfiguration.instance.entitySyncQueue,
                entityManagerFactory = this.entityManagerFactory,
                executor = this.executorService)
    }

    @PreDestroy
    fun onDestroy() {
        entityConsumer.close()
    }
}
