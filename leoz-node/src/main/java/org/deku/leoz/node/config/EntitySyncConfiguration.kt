package org.deku.leoz.node.config

import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.node.Application
import org.deku.leoz.node.data.jpa.QMstBundleVersion.mstBundleVersion
import org.deku.leoz.node.data.jpa.QMstCountry.mstCountry
import org.deku.leoz.node.data.jpa.QMstDebitor.mstDebitor
import org.deku.leoz.node.data.jpa.QMstHolidayCtrl.mstHolidayCtrl
import org.deku.leoz.node.data.jpa.QMstRoute.mstRoute
import org.deku.leoz.node.data.jpa.QMstRoutingLayer.mstRoutingLayer
import org.deku.leoz.node.data.jpa.QMstSector.mstSector
import org.deku.leoz.node.data.jpa.QMstStation.mstStation
import org.deku.leoz.node.data.jpa.QMstStationContract.mstStationContract
import org.deku.leoz.node.data.repository.SyncRepository
import org.deku.leoz.node.service.internal.sync.ConsumerPreset
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
@Profile(Application.PROFILE_NODE)
@Lazy(false)
class EntitySyncConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    private lateinit var entityManagerFactory: EntityManagerFactory

    @Inject
    private lateinit var syncRepository: SyncRepository

    @Inject
    private lateinit var broker: ActiveMQBroker

    private val presets = listOf<ConsumerPreset<*>>(
            ConsumerPreset(
                    mstBundleVersion,
                    mstBundleVersion.syncId),
            ConsumerPreset(
                    mstCountry,
                    mstCountry.syncId),
            ConsumerPreset(
                    mstDebitor,
                    mstDebitor.syncId),
            ConsumerPreset(
                    mstHolidayCtrl,
                    mstHolidayCtrl.syncId),
            ConsumerPreset(
                    mstRoute,
                    mstRoute.syncId),
            ConsumerPreset(
                    mstRoutingLayer,
                    mstRoutingLayer.syncId),
            ConsumerPreset(
                    mstSector,
                    mstSector.syncId),
            ConsumerPreset(
                    mstStation,
                    mstStation.syncId),
            ConsumerPreset(
                    mstStationContract,
                    mstStationContract.syncId)
    )

    /** Entity sync consumer */
    @get:Bean
    val entityConsumer: EntityConsumer
        get() =
            EntityConsumer(
                    notificationEndpoint = JmsEndpoints.central.entitySync.topic,
                    requestEndpoint = JmsEndpoints.central.entitySync.queue,
                    entityManagerFactory = this.entityManagerFactory,
                    syncRepository = this.syncRepository,
                    listenerExecutor = this.executorService,
                    presets = this.presets
            )

    /** Broker listener  */
    private val brokerEventListener = object : MqBroker.DefaultEventListener() {
        override fun onStart() {
            log.info("Detected broker start, initializing entity sync")
            this@EntitySyncConfiguration.entityConsumer.start()
        }

        override fun onStop() {
            this@EntitySyncConfiguration.entityConsumer.stop()
        }

        override fun onConnectedToBrokerNetwork() {
            this@EntitySyncConfiguration.entityConsumer.request()
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
