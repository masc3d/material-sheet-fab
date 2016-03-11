package org.deku.leoz.central.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.App
import org.deku.leoz.central.data.sync.DatabaseSync
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.data.sync.EntityPublisher
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker
import java.sql.Timestamp
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
@Configuration(App.PROFILE_CENTRAL)
@Lazy(false)
open class EntitySyncConfiguration {
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    private lateinit var entityManagerFactory: EntityManagerFactory

    @Inject
    private lateinit var databaseSync: DatabaseSync

    /** Entity publisher */
    private lateinit var entityPublisher: EntityPublisher

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
    private val databaseSyncEvent = object : DatabaseSync.EventListener {
        override fun onUpdate(entityType: Class<out Any?>, currentTimestamp: Timestamp?) {
            this@EntitySyncConfiguration.entityPublisher.publish(entityType, currentTimestamp);
        }
    }

    @PostConstruct
    fun onInitialize() {
        // Setup entity publisher
        this.entityPublisher = EntityPublisher(
                ActiveMQConfiguration.instance,
                this.entityManagerFactory,
                this.executorService)

        // Wire database sync event
        this.databaseSync.eventDelegate.add(databaseSyncEvent)

        // Wire broker event
        ActiveMQBroker.instance.delegate.add(brokerEventListener)

        if (ActiveMQBroker.instance.isStarted)
            brokerEventListener.onStart()
    }

    @PreDestroy
    fun onDestroy() {
        this.entityPublisher.close()
    }
}
