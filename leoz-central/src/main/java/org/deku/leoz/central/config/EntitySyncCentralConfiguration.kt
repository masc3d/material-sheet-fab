package org.deku.leoz.central.config

import org.deku.leoz.central.Application
import org.deku.leoz.central.service.internal.sync.DatabaseSyncService
import org.deku.leoz.config.JmsConfiguration
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.service.internal.sync.EntityPublisher
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.mq.MqBroker
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
@Configuration
@Lazy(false)
open class EntitySyncCentralConfiguration {
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
    private lateinit var mqConfigration: JmsConfiguration

    /** Broker event listener  */
    private val brokerEventListener = object : MqBroker.DefaultEventListener() {
        override fun onStart() {
            this@EntitySyncCentralConfiguration.entityPublisher.start()
        }

        override fun onStop() {
            this@EntitySyncCentralConfiguration.entityPublisher.close()
        }
    }

    /** Database sync event listener */
    private val databaseSyncEvent = object : DatabaseSyncService.EventListener {
        override fun onUpdate(entityType: Class<out Any?>, currentSyncId: Long?) {
            this@EntitySyncCentralConfiguration.entityPublisher.publish(entityType, currentSyncId);
        }
    }

    @PostConstruct
    fun onInitialize() {
        // Setup entity publisher
        this.entityPublisher = EntityPublisher(
                requestEndpoint = JmsEndpoints.central.entitySync.queue,
                notificationEndpoint = JmsEndpoints.central.entitySync.topic,
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
