package org.deku.leoz.central.service.internal.sync

import com.querydsl.jpa.impl.JPADeleteClause
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.*
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.node.data.jpa.*
import org.deku.leoz.node.data.repository.master.*
import org.deku.leoz.node.data.repository.system.PropertyRepository
import org.jooq.Transaction
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.PlatformTransactionManager
import org.threeten.bp.Duration
import sx.concurrent.Service
import java.util.concurrent.ScheduledExecutorService
import javax.persistence.EntityManager

/**
 * TODO: split configuration (which tables to sync and how) from actual implementation, move config to DatabaseSyncConfiguration
 * Created by masc on 15.05.15.
 */
@javax.inject.Named
open class DatabaseSyncService
@javax.inject.Inject
constructor(
        private val exceutorService: ScheduledExecutorService,
        @Qualifier(org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER) tx: PlatformTransactionManager,
        @Qualifier(PersistenceConfiguration.QUALIFIER) txJooq: PlatformTransactionManager,
        val presets: List<Preset> = listOf()
) {
    /**
     * Embedded service class
     */
    private val service = object : Service(executorService = this.exceutorService, period = Duration.ofSeconds(4)) {
        override fun run() {
            this@DatabaseSyncService.sync(false)
        }

        fun submitTask(command: () -> Unit) {
            super.submitSupplementalTask(command)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DatabaseSyncService::class.java)
    }

    //region Events
    interface EventListener : sx.event.EventListener {
        /** Emitted when entities have been updated  */
        fun onUpdate(entityType: Class<out Any?>, currentSyncId: Long?)
    }

    private val eventDispatcher = sx.event.EventDispatcher.Companion.createThreadSafe<EventListener>()
    open val eventDelegate: sx.event.EventDelegate<EventListener>
        get() = eventDispatcher
    //endregion

    @javax.persistence.PersistenceContext
    private lateinit var entityManager: javax.persistence.EntityManager

    // Transaction helpers
    private val transaction: org.springframework.transaction.support.TransactionTemplate
    private val transactionJooq: org.springframework.transaction.support.TransactionTemplate

    // JOOQ Repositories
    @javax.inject.Inject
    private lateinit var genericJooqRepository: org.deku.leoz.central.data.repository.GenericJooqRepository
    @javax.inject.Inject
    private lateinit var syncJooqRepository: org.deku.leoz.central.data.repository.SyncJooqRepository

    init {
        transaction = org.springframework.transaction.support.TransactionTemplate(tx)
        transaction.propagationBehavior = org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW

        transactionJooq = org.springframework.transaction.support.TransactionTemplate(txJooq)
    }

    @org.springframework.transaction.annotation.Transactional(value = org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER)
    @Synchronized open fun sync(clean: Boolean) {
        val sw = com.google.common.base.Stopwatch.createStarted()

        val presets = listOf(
                SimplePreset(
                        Tables.MST_BUNDLE_VERSION,
                        Tables.MST_BUNDLE_VERSION.SYNC_ID,
                        QMstBundleVersion.mstBundleVersion,
                        QMstBundleVersion.mstBundleVersion.syncId,
                        { s ->
                            MstBundleVersion().also { d ->
                                d.id = s.id.toLong()
                                d.bundle = s.bundle
                                d.alias = s.alias
                                d.version = s.version
                                d.syncId = s.syncId
                            }
                        }
                ),

                SimplePreset(
                        Tables.TBLDEPOTLISTE,
                        Tables.TBLDEPOTLISTE.SYNC_ID,
                        QMstStation.mstStation,
                        QMstStation.mstStation.syncId,
                        { s ->
                            MstStation().also { d ->
                                d.stationId = s.id
                                d.stationNr = s.depotnr
                                d.timestamp = s.timestamp
                                d.address1 = s.firma1
                                d.address2 = s.firma2
                                d.city = s.ort
                                d.contactPerson1 = s.anprechpartner1
                                d.contactPerson2 = s.anprechpartner2
                                d.country = s.lkz
                                d.email = s.email
                                d.houseNr = s.strnr
                                d.mobile = s.mobil
                                d.phone1 = s.telefon1
                                d.phone2 = s.telefon2
                                d.posLat = s.poslat
                                d.posLong = s.poslong
                                d.sector = s.sektor
                                d.servicePhone1 = s.nottelefon1
                                d.servicePhone2 = s.nottelefon2
                                // TODO: strang? strange? ;)
                                d.strang = null
                                d.street = s.strasse
                                d.telefax = s.telefax
                                d.ustid = s.ustid
                                d.webAddress = s.webadresse
                                d.zip = s.plz
                                d.syncId = s.syncId
                                d.exportValuablesAllowed = if (s.valok == 1.toUInteger()) 1 else 0
                                d.exportValuablesWithoutBagAllowed = if (s.valokWithoutBag == 1) 1 else 0
                            }
                        }
                ),

                SimplePreset(
                        Tables.MST_COUNTRY,
                        Tables.MST_COUNTRY.SYNC_ID,
                        QMstCountry.mstCountry,
                        QMstCountry.mstCountry.syncId,
                        { s ->
                            MstCountry().also { d ->
                                d.code = s.code
                                d.timestamp = s.timestamp
                                d.routingTyp = s.routingTyp
                                d.minLen = s.minLen
                                d.maxLen = s.maxLen
                                d.zipFormat = s.zipFormat
                                d.syncId = s.syncId
                            }
                        }
                ),

                SimplePreset(
                        Tables.MST_HOLIDAYCTRL,
                        Tables.MST_HOLIDAYCTRL.SYNC_ID,
                        QMstHolidayCtrl.mstHolidayCtrl,
                        QMstHolidayCtrl.mstHolidayCtrl.syncId,
                        { s ->
                            MstHolidayCtrl().also { d ->
                                d.id = s.id.toLong()
                                d.country = s.country
                                d.ctrlPos = s.ctrlPos
                                d.description = s.description
                                d.holiday = s.holiday
                                d.timestamp = s.timestamp
                                d.syncId = s.syncId
                            }
                        }
                ),

                SimplePreset(
                        Tables.MST_ROUTE,
                        Tables.MST_ROUTE.SYNC_ID,
                        QMstRoute.mstRoute,
                        QMstRoute.mstRoute.syncId,
                        { s ->
                            MstRoute().also { d ->
                                d.id = s.id.toLong()
                                d.layer = s.layer
                                d.country = s.country
                                d.zipFrom = s.zipfrom
                                d.zipTo = s.zipto
                                d.validCrtr = s.validCtrl
                                d.validFrom = s.validfrom
                                d.validTo = s.validto
                                d.timestamp = s.timestamp
                                d.station = s.station
                                d.area = s.area
                                d.etod = s.etod
                                d.ltop = s.ltop
                                d.term = s.term
                                d.saturdayOk = s.saturdayOk
                                d.ltodsa = s.ltodsa
                                d.ltodholiday = s.ltodholiday
                                d.island = s.island
                                d.holidayCtrl = s.holidayctrl
                                d.syncId = s.syncId
                            }
                        }
                ),

                SimplePreset(
                        Tables.MST_SECTOR,
                        Tables.MST_SECTOR.SYNC_ID,
                        QMstSector.mstSector,
                        QMstSector.mstSector.syncId,
                        { s ->
                            MstSector().also { d ->
                                d.id = s.id.toLong()
                                d.sectorFrom = s.sectorfrom
                                d.sectorTo = s.sectorto
                                d.timestamp = s.timestamp
                                d.validFrom = s.validfrom
                                d.validTo = s.validto
                                d.syncId = s.syncId
                            }
                        }
                ),

                SimplePreset(
                        Tables.MST_ROUTINGLAYER,
                        Tables.MST_ROUTINGLAYER.SYNC_ID,
                        QMstRoutingLayer.mstRoutingLayer,
                        QMstRoutingLayer.mstRoutingLayer.syncId,
                        { s ->
                            MstRoutingLayer().also { d ->
                                d.layer = s.layer
                                d.services = s.services
                                d.description = s.description
                                d.timestamp = s.timestamp
                                d.syncId = s.syncId
                            }
                        }
                ),

                SimplePreset(
                        Tables.MST_STATION_SECTOR,
                        Tables.MST_STATION_SECTOR.SYNC_ID,
                        QMstStationSector.mstStationSector,
                        QMstStationSector.mstStationSector.syncId,
                        { s ->
                            MstStationSector().also { d ->
                                d.id = s.id.toLong()
                                d.stationNr = s.stationNr
                                d.sector = s.sector
                                d.routingLayer = s.routingLayer
                                d.timestamp = s.timestamp
                                d.syncId = s.syncId
                            }
                        }
                ),

                SimplePreset(
                        Tables.MST_DEBITOR,
                        Tables.MST_DEBITOR.SYNC_ID,
                        QMstDebitor.mstDebitor,
                        QMstDebitor.mstDebitor.syncId,
                        { s ->
                            MstDebitor().also { d ->
                                d.debitorId = s.debitorId
                                d.debitorNr = s.debitorNr
                                d.tsCreated = s.tsCreated
                                d.tsUpdated = s.tsUpdated
                                d.parentId = s.parentId
                                d.syncId = s.syncId

                            }
                        }
                ),

                SimplePreset(
                        Tables.MST_DEBITOR_STATION,
                        Tables.MST_DEBITOR_STATION.SYNC_ID,
                        QMstDebitorStation.mstDebitorStation,
                        QMstDebitorStation.mstDebitorStation.syncId,
                        { s ->
                            MstDebitorStation().also { d ->
                                d.id = s.id
                                d.debitorId = s.debitorId
                                d.stationId = s.stationId
                                d.tsCreated = s.tsCreated
                                d.tsUpdated = s.tsUpdated
                                d.activFrom = s.activFrom
                                d.activTo = s.activTo
                                d.syncId = s.syncId

                            }
                        }
                ),

                SimplePreset(
                        Tables.TAD_NODE_GEOPOSITION,
                        Tables.TAD_NODE_GEOPOSITION.SYNC_ID,
                        QTadNodeGeoposition.tadNodeGeoposition,
                        QTadNodeGeoposition.tadNodeGeoposition.syncId,
                        { s ->
                            TadNodeGeoposition().also { d ->
                                d.positionId = s.positionId
                                d.userId = s.userId
                                d.nodeId = s.nodeId
                                d.tsCreated = s.tsCreated
                                d.tsUpdated = s.tsUpdated
                                d.latitude = s.latitude
                                d.longitude = s.longitude
                                d.positionDatetime = s.positionDatetime
                                d.speed = s.speed
                                d.bearing = s.bearing
                                d.altitude = s.altitude
                                d.accuracy = s.accuracy
                                d.vehicleType = s.vehicleType
                                d.debitorId = s.debitorId
                                d.syncId = s.syncId
                            }
                        }
                )
        )

        presets.forEach {
            it.updateEntities(clean)
        }

        log.info("Database sync took " + sw.toString())
    }

    /**
     * Database sync preset
     */
    interface Preset {
        fun updateEntities(deleteBeforeUpdate: Boolean)
    }

    /**
     * Database sync preset
     * @param <TEntity>             Type of destiantion JPA entity
     * @param <TCentralRecord>      Type of source JOOQ record
     * @property sourceTable           JOOQ source table
     * @property sourceTableSyncIdField      JOOQ source sync id field
     * @property destQdslEntityPath    Destination QueryDSL entity table path
     * @property destQdslSyncIdPath Destination QueryDSL sync id field path
     * @property conversionFunction    Conversion function JOOQ record -> JPA entity
     */
    inner class SimplePreset<TCentralRecord : org.jooq.Record, TEntity>(
            val sourceTable: org.jooq.impl.TableImpl<TCentralRecord>,
            val sourceTableSyncIdField: org.jooq.TableField<TCentralRecord, Long>?,
            val destQdslEntityPath: com.querydsl.core.types.dsl.EntityPathBase<TEntity>,
            val destQdslSyncIdPath: com.querydsl.core.types.dsl.NumberPath<Long>?,
            val conversionFunction: (TCentralRecord) -> TEntity
    ) : Preset {
        /**
         * Generic updater for entites from jooq to jpa
         * @param deleteBeforeUpdate    Delete all records before updating
         */
        override fun updateEntities(
                deleteBeforeUpdate: Boolean) {

            // Stopwatch
            val sw = com.google.common.base.Stopwatch.createStarted()
            // Log formatter
            val lfmt = { s: String -> "[${destQdslEntityPath.type.name}] ${s} $sw" }

            entityManager.flushMode = javax.persistence.FlushModeType.COMMIT

            if (deleteBeforeUpdate || destQdslSyncIdPath == null) {
                transaction.execute<Any> { _ ->
                    log.info(lfmt("Deleting all entities"))

                    JPADeleteClause(entityManager, destQdslEntityPath)
                            .execute()

                    entityManager.flush()
                    entityManager.clear()
                }
            }

            // TODO. optimize by preparing query or at least caching the querydsl instance
            // Get latest timestamp
            var destMaxSyncId: Long? = null
            if (destQdslSyncIdPath != null) {
                // Query embedded database table for latest timestamp
                destMaxSyncId = com.querydsl.jpa.impl.JPAQuery<TEntity>(entityManager)
                        .from(destQdslEntityPath)
                        .select(destQdslSyncIdPath.max())
                        .fetchFirst()
            }

            // TODO. optimize by using jooq prepared statements
            if (destMaxSyncId != null) {
                val maxSyncId = syncJooqRepository.findSyncIdByTableName(sourceTable.name)
                if (maxSyncId == destMaxSyncId) {
                    log.info(lfmt("sync-id uptodate [${destMaxSyncId}]"))
                    return
                }
            }

            // Get newer records from central
            // masc20150530. JOOQ cursor requires an explicit transaction
            transactionJooq.execute<Any> { _ ->
                // Read source records newer than destination timestamp
                val source = genericJooqRepository.findNewerThan(
                        destMaxSyncId,
                        sourceTable,
                        sourceTableSyncIdField)

                if (source.hasNext()) {
                    // Save to destination/jpa
                    // REMARKS
                    // * saving/transaction commit gets very slow when deleting and inserting within the same transaction
                    log.info(lfmt("Outdated [${destMaxSyncId}]"))
                    var count = 0
                    transaction.execute<Any> { _ ->
                        while (source.hasNext()) {
                            // Fetch next record
                            val record = source.fetchOne()
                            // Convert to entity
                            val entity = conversionFunction(record)
                            // Store entity
                            entityManager.persist(entity)
                            // Flush every now and then (improves performance)
                            if (count++ % 100 == 0) {
                                entityManager.flush()
                                entityManager.clear()
                            }
                        }
                    }

                    // Re-query destination timestamp
                    if (destQdslSyncIdPath != null) {
                        // Query embedded database for updated latest timestamp
                        destMaxSyncId = com.querydsl.jpa.impl.JPAQuery<TEntity>(entityManager)
                                .from(destQdslEntityPath)
                                .select(destQdslSyncIdPath.max())
                                .fetchFirst()
                    }
                    log.info(lfmt("Updated ${count} entities [${destMaxSyncId}]"))

                    // Emit update event
                    eventDispatcher.emit { e ->
                        // Emit event
                        e.onUpdate(destQdslEntityPath.type, destMaxSyncId)
                    }
                } else {
                    log.info(lfmt("Uptodate [${destMaxSyncId}]"))
                }
                null
            }
        }
    }

    open fun start() {
        this.service.start()
    }

    open fun stop() {
        this.service.stop()
    }

    open fun trigger() {
        this.service.trigger()
    }

    open fun startSync(clean: Boolean) {
        this.service.submitTask {
            this@DatabaseSyncService.sync(clean)
        }
    }
}
