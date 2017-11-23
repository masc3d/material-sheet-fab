package org.deku.leoz.node.config

import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.node.Application
import org.deku.leoz.node.LifecycleController
import org.deku.leoz.node.data.jpa.*
import org.deku.leoz.node.service.internal.sync.EntityConsumer
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import sx.mq.MqBroker
import sx.mq.jms.activemq.ActiveMQBroker
import java.util.concurrent.ScheduledExecutorService
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit

/**
 * Entity synchronization configuration.
 * Sets up entity/database synchronization over the message bus.
 * Created by masc on 20.06.15.
 */
@Configuration
@Profile(Application.PROFILE_CLIENT_NODE)
@Lazy(false)
open class EntitySyncNodeConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    private lateinit var entityManagerFactory: EntityManagerFactory

    @Inject
    private lateinit var lifecycleController: LifecycleController

    @Inject
    private lateinit var broker: ActiveMQBroker

    /** Entity sync consumer */
    @Bean
    open fun createEntityConsumer(): EntityConsumer {
        return EntityConsumer(
                notificationEndpoint = JmsEndpoints.central.entitySync.topic,
                requestEndpoint = JmsEndpoints.central.entitySync.queue,
                entityManagerFactory = this.entityManagerFactory,
                listenerExecutor = this.executorService)
    }

    @Inject
    private lateinit var entityConsumer: EntityConsumer

    /** Broker listener  */
    private val brokerEventListener = object : MqBroker.DefaultEventListener() {
        override fun onStart() {
            log.info("Detected broker start, initializing entity sync")
            this@EntitySyncNodeConfiguration.entityConsumer.start()
        }

        override fun onStop() {
            this@EntitySyncNodeConfiguration.entityConsumer.stop()
        }

        override fun onConnectedToBrokerNetwork() {
            this@EntitySyncNodeConfiguration.requestEntities()
        }
    }

    /**
     * List of masterdata entities to synchronize
     */
    private val masterdataEntities = listOf(
            MstStation::class.java,
            MstCountry::class.java,
            MstHolidayCtrl::class.java,
            MstRoute::class.java,
            MstRoutingLayer::class.java,
            MstSector::class.java,
            MstBundleVersion::class.java,
            MstDebitor::class.java,
            MstDebitorStation::class.java
    )

    /**
     * Starts entity requests for all entity types
     */
    fun requestEntities(clean: Boolean = false) {
        masterdataEntities.forEach {
            entityConsumer.request(it, clean)
        }
    }

    @PostConstruct
    fun onInitialize() {
        // Start when broker is started
        this.broker.delegate.add(brokerEventListener)
        if (this.broker.isStarted)
            brokerEventListener.onStart()
    }

    @PreDestroy
    fun onDestroy() {
        entityConsumer.close()
    }
}
