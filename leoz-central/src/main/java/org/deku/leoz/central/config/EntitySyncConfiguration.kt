package org.deku.leoz.central.config

import org.deku.leoz.central.Application
import org.deku.leoz.central.service.internal.sync.DatabaseSyncService
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.service.internal.sync.EntityPublisher
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.mq.Broker
import sx.mq.jms.activemq.ActiveMQBroker
import java.util.concurrent.ScheduledExecutorService
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit

/**
 * Leoz-central entity sync configuration
 * Created by masc on 20.06.15.
 */
@Configuration(Application.PROFILE_CENTRAL)
@Lazy(false)
open class EntitySyncConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    private lateinit var entityManagerFactory: EntityManagerFactory

    @Inject
    private lateinit var databaseSyncService: DatabaseSyncService

    /** Entity publisher */
    private lateinit var entityPublisher: EntityPublisher

    @Inject
    private lateinit var mqConfigration: ActiveMQConfiguration
    
    /** Broker event listener  */
    private val brokerEventListener = object : Broker.DefaultEventListener() {
        override fun onStart() {
            this@EntitySyncConfiguration.entityPublisher.start()
        }

        override fun onStop() {
            this@EntitySyncConfiguration.entityPublisher.close()
        }
    }

    /** Database sync event listener */
    private val databaseSyncEvent = object : DatabaseSyncService.EventListener {
        override fun onUpdate(entityType: Class<out Any?>, currentSyncId: Long?) {
            this@EntitySyncConfiguration.entityPublisher.publish(entityType, currentSyncId);
        }
    }

    @PostConstruct
    fun onInitialize() {
        // Setup entity publisher
        this.entityPublisher = EntityPublisher(
                requestChannel = this.mqConfigration.entitySyncQueue,
                notificationChannel = this.mqConfigration.entitySyncTopic,
                entityManagerFactory = this.entityManagerFactory,
                listenerExecutor = this.executorService)

        // Wire database sync event
        this.databaseSyncService.eventDelegate.add(databaseSyncEvent)

        // Wire broker event
        this.mqConfigration.broker.delegate.add(brokerEventListener)

        if (this.mqConfigration.broker.isStarted)
            brokerEventListener.onStart()
    }

    @PreDestroy
    fun onDestroy() {
        this.entityPublisher.close()
    }
}
