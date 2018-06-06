package org.deku.leoz.central.config

import org.deku.leoz.central.service.internal.sync.DatabaseSyncService
import org.deku.leoz.config.JmsConfiguration
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.node.config.PersistenceConfiguration
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
import org.deku.leoz.node.service.internal.sync.EntityPublisher
import org.deku.leoz.node.service.internal.sync.PublisherPreset
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
class EntitySyncConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    private lateinit var entityManagerFactory: EntityManagerFactory

    @Inject
    private lateinit var syncRepository: SyncRepository

    @Inject
    private lateinit var dbsync: DatabaseSyncService

    /** Entity publisher */
    private lateinit var entityPublisher: EntityPublisher

    @Inject
    private lateinit var mqConfigration: JmsConfiguration

    /** Broker event listener  */
    private val brokerEventListener = object : MqBroker.DefaultEventListener() {
        override fun onStart() {
            this@EntitySyncConfiguration.entityPublisher.start()
        }

        override fun onStop() {
            this@EntitySyncConfiguration.entityPublisher.close()
        }
    }

    private val presets = listOf<PublisherPreset<*>>(
            PublisherPreset(
                    mstBundleVersion,
                    mstBundleVersion.syncId),
            PublisherPreset(
                    mstCountry,
                    mstCountry.syncId),
            PublisherPreset(
                    mstDebitor,
                    mstDebitor.syncId),
            PublisherPreset(
                    mstHolidayCtrl,
                    mstHolidayCtrl.syncId),
            PublisherPreset(
                    mstRoute,
                    mstRoute.syncId),
            PublisherPreset(
                    mstRoutingLayer,
                    mstRoutingLayer.syncId),
            PublisherPreset(
                    mstSector,
                    mstSector.syncId),
            PublisherPreset(
                    mstStation,
                    mstStation.syncId),
            PublisherPreset(
                    mstStationContract,
                    mstStationContract.syncId)
    )

    @PostConstruct
    fun onInitialize() {
        // Setup entity publisher
        this.entityPublisher = EntityPublisher(
                requestEndpoint = JmsEndpoints.central.entitySync.queue,
                notificationEndpoint = JmsEndpoints.central.entitySync.topic,
                entityManagerFactory = this.entityManagerFactory,
                syncRepository = this.syncRepository,
                listenerExecutor = this.executorService,
                presets = this.presets
        )

        // Wire database sync event
        this.dbsync.updates
                .subscribe {
                    entityPublisher.publish(it.entityType, it.syncId)
                }

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
