package org.deku.leoz.node.config

import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.node.Application
import org.deku.leoz.node.LifecycleController
import org.deku.leoz.node.data.jpa.*
import org.deku.leoz.node.service.internal.sync.EntityConsumer
import org.deku.leoz.node.service.internal.sync.Preset
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
class EntitySyncNodeConfiguration {
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
    fun createEntityConsumer(): EntityConsumer {
        return EntityConsumer(
                notificationEndpoint = JmsEndpoints.central.entitySync.topic,
                requestEndpoint = JmsEndpoints.central.entitySync.queue,
                entityManagerFactory = this.entityManagerFactory,
                listenerExecutor = this.executorService,
                presets = listOf(
                        Preset(MstStation::class.java),
                        Preset(MstCountry::class.java),
                        Preset(MstHolidayCtrl::class.java),
                        Preset(MstRoute::class.java),
                        Preset(MstRoutingLayer::class.java),
                        Preset(MstSector::class.java),
                        Preset(MstBundleVersion::class.java),
                        Preset(MstDebitor::class.java),
                        Preset(MstDebitorStation::class.java)
                )
        )
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
            this@EntitySyncNodeConfiguration.entityConsumer.request()
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
