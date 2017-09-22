package org.deku.leoz.central.service.internal.sync

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.*
import org.deku.leoz.node.data.jpa.*
import org.deku.leoz.node.data.repository.master.*
import org.deku.leoz.node.data.repository.system.PropertyRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.PlatformTransactionManager
import org.threeten.bp.Duration
import sx.concurrent.Service
import java.util.concurrent.ScheduledExecutorService

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
        @Qualifier(PersistenceConfiguration.QUALIFIER) txJooq: PlatformTransactionManager) {
    /**
     * Embedded service class
     */
    private val service = object : Service(executorService = this.exceutorService, period = Duration.ofMinutes(1)) {
        override fun run() {
            this@DatabaseSyncService.sync(false)
        }

        fun submitTask(command: () -> Unit) {
            super.submitSupplementalTask(command)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DatabaseSyncService::class.java)

        /**
         * Convert mysql mst_bundle_version record
         */
        private fun convert(ds: MstBundleVersionRecord): MstBundleVersion {
            val s = MstBundleVersion()

            s.id = ds.id.toLong()
            s.bundle = ds.bundle
            s.alias = ds.alias
            s.version = ds.version
            s.syncId = ds.syncId

            return s
        }

        /**
         * Convert mysql mst_station record to jpa entity
         * @param ds
         * @return
         */
        private fun convert(ds: TbldepotlisteRecord): MstStation {
            val s = MstStation()

            s.stationNr = ds.depotnr
            s.timestamp = ds.timestamp
            s.address1 = ds.firma1
            s.address2 = ds.firma2
            s.city = ds.ort
            s.contactPerson1 = ds.anprechpartner1
            s.contactPerson2 = ds.anprechpartner2
            s.country = ds.lkz
            s.email = ds.email
            s.houseNr = ds.strnr
            s.mobile = ds.mobil
            s.phone1 = ds.telefon1
            s.phone2 = ds.telefon2
            s.posLat = ds.poslat
            s.posLong = ds.poslong
            s.sector = ds.sektor
            s.servicePhone1 = ds.nottelefon1
            s.servicePhone2 = ds.nottelefon2
            // TODO: strang? strange? ;)
            s.strang = null
            s.street = ds.strasse
            s.telefax = ds.telefax
            s.ustid = ds.ustid
            s.webAddress = ds.webadresse
            s.zip = ds.plz
            s.syncId = ds.syncId
            return s
        }

        /**
         * Convert mysql country record to jpa entity
         * @param cr
         * @return
         */
        private fun convert(cr: MstCountryRecord): MstCountry {
            val c = MstCountry()

            c.code = cr.code
            //        c.getNameStringId(cr.getNameStringid() );
            c.timestamp = cr.timestamp
            c.routingTyp = cr.routingTyp
            c.minLen = cr.minLen
            c.maxLen = cr.maxLen
            c.zipFormat = cr.zipFormat
            c.syncId = cr.syncId

            return c
        }

        /**
         * Convert mysql holidayctrl record to jpa entity
         * @param cr
         * @return
         */
        private fun convert(cr: MstHolidayctrlRecord): MstHolidayCtrl {
            val d = MstHolidayCtrl()

            d.id = cr.id.toLong()
            d.country = cr.country
            d.ctrlPos = cr.ctrlPos
            d.description = cr.description
            d.holiday = cr.holiday
            d.timestamp = cr.timestamp
            d.syncId = cr.syncId

            return d
        }

        /**
         * Convert mysql sector record to jpa entity
         * @param cr
         * @return
         */
        private fun convert(cr: MstSectorRecord): MstSector {
            val d = MstSector()

            d.id = cr.id.toLong()
            d.sectorFrom = cr.sectorfrom
            d.sectorTo = cr.sectorto
            d.timestamp = cr.timestamp
            d.validFrom = cr.validfrom
            d.validTo = cr.validto
            d.syncId = cr.syncId

            return d
        }

        /**
         * Convert mysql value record to jpa entity
         * @param rs
         * @return
         */
        private fun convert(rs: MstRoutinglayerRecord): MstRoutingLayer {
            val d = MstRoutingLayer()

            d.layer = rs.layer
            d.services = rs.services
            d.description = rs.description
            d.timestamp = rs.timestamp
            d.syncId = rs.syncId

            return d
        }


        /**
         * Convert mysql route record to jpa entity
         * @param sr
         * @return
         */
        private fun convert(sr: MstRouteRecord): MstRoute {
            val d = MstRoute()

            d.id = sr.id.toLong()
            d.layer = sr.layer
            d.country = sr.country
            d.zipFrom = sr.zipfrom
            d.zipTo = sr.zipto
            d.validCrtr = sr.validCtrl
            d.validFrom = sr.validfrom
            d.validTo = sr.validto
            d.timestamp = sr.timestamp
            d.station = sr.station
            d.area = sr.area
            d.etod = sr.etod
            d.ltop = sr.ltop
            d.term = sr.term
            d.saturdayOk = sr.saturdayOk
            d.ltodsa = sr.ltodsa
            d.ltodholiday = sr.ltodholiday
            d.island = sr.island
            d.holidayCtrl = sr.holidayctrl
            d.syncId = sr.syncId

            return d
        }

        /**
         * Convert mysql stationsectors record to jpa entity
         * @param ss
         * @return
         */
        private fun convert(ss: MstStationSectorRecord): MstStationSector {
            val s = MstStationSector()
            s.id = ss.id.toLong()
            s.stationNr = ss.stationNr
            s.sector = ss.sector
            s.routingLayer = ss.routingLayer
            s.timestamp = ss.timestamp
            s.syncId = ss.syncId
            return s
        }
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

    // JPA Repositories
    @javax.inject.Inject
    private lateinit var bundleVersionRepository: BundleVersionRepository
    @javax.inject.Inject
    private lateinit var stationRepository: StationRepository
    @javax.inject.Inject
    private lateinit var countryRepository: CountryRepository
    @javax.inject.Inject
    private lateinit var routeRepository: RouteRepository
    @javax.inject.Inject
    private lateinit var holidayCtrlRepository: HolidayCtrlRepository
    @javax.inject.Inject
    private lateinit var sectorRepository: SectorRepository
    @javax.inject.Inject
    private lateinit var propertyRepository: PropertyRepository
    @javax.inject.Inject
    private lateinit var routingLayerRepository: RoutingLayerRepository
    @javax.inject.Inject
    private lateinit var stationSectorRepository: StationSectorRepository

    init {
        transaction = org.springframework.transaction.support.TransactionTemplate(tx)
        transaction.propagationBehavior = org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW

        transactionJooq = org.springframework.transaction.support.TransactionTemplate(txJooq)
    }

    @org.springframework.transaction.annotation.Transactional(value = org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER)
    @Synchronized open fun sync(clean: Boolean) {
        val sw = com.google.common.base.Stopwatch.createStarted()

        this.updateEntities<MstBundleVersionRecord, MstBundleVersion>(
                Tables.MST_BUNDLE_VERSION,
                org.deku.leoz.central.data.jooq.tables.MstBundleVersion.MST_BUNDLE_VERSION.SYNC_ID,
                bundleVersionRepository,
                QMstBundleVersion.mstBundleVersion,
                QMstBundleVersion.mstBundleVersion.syncId,
                { s -> convert(s) },
                clean)

        this.updateEntities<TbldepotlisteRecord, MstStation>(
                Tables.TBLDEPOTLISTE,
                org.deku.leoz.central.data.jooq.tables.Tbldepotliste.TBLDEPOTLISTE.SYNC_ID,
                stationRepository,
                QMstStation.mstStation,
                QMstStation.mstStation.syncId,
                { s -> convert(s) },
                clean)

        this.updateEntities(
                Tables.MST_COUNTRY,
                org.deku.leoz.central.data.jooq.tables.MstCountry.MST_COUNTRY.SYNC_ID,
                countryRepository,
                QMstCountry.mstCountry,
                QMstCountry.mstCountry.syncId,
                { s -> convert(s) },
                clean)

        this.updateEntities(
                Tables.MST_HOLIDAYCTRL,
                org.deku.leoz.central.data.jooq.tables.MstHolidayctrl.MST_HOLIDAYCTRL.SYNC_ID,
                holidayCtrlRepository,
                QMstHolidayCtrl.mstHolidayCtrl,
                QMstHolidayCtrl.mstHolidayCtrl.syncId,
                { s -> convert(s) },
                clean)

        this.updateEntities(
                Tables.MST_ROUTE,
                org.deku.leoz.central.data.jooq.tables.MstRoute.MST_ROUTE.SYNC_ID,
                routeRepository,
                QMstRoute.mstRoute,
                QMstRoute.mstRoute.syncId,
                { s -> convert(s) },
                clean)

        this.updateEntities(
                org.deku.leoz.central.data.jooq.Tables.MST_SECTOR,
                org.deku.leoz.central.data.jooq.tables.MstSector.MST_SECTOR.SYNC_ID,
                sectorRepository,
                QMstSector.mstSector,
                QMstSector.mstSector.syncId,
                { s -> convert(s) },
                clean)

        this.updateEntities(
                org.deku.leoz.central.data.jooq.Tables.MST_ROUTINGLAYER,
                org.deku.leoz.central.data.jooq.tables.MstRoutinglayer.MST_ROUTINGLAYER.SYNC_ID,
                routingLayerRepository,
                QMstRoutingLayer.mstRoutingLayer,
                QMstRoutingLayer.mstRoutingLayer.syncId,
                { s -> convert(s) },
                clean)

        this.updateEntities(
                org.deku.leoz.central.data.jooq.Tables.MST_STATION_SECTOR,
                org.deku.leoz.central.data.jooq.tables.MstStationSector.MST_STATION_SECTOR.SYNC_ID,
                stationSectorRepository,
                QMstStationSector.mstStationSector,
                QMstStationSector.mstStationSector.syncId,
                { s -> convert(s) },
                clean)

        log.info("Database sync took " + sw.toString())
    }


    /**
     * Generic updater for entites from jooq to jpa
     * @param sourceTable           JOOQ source table
     * @param sourceTableSyncIdField      JOOQ source sync id field
     * @param destRepository        Destination JPA repository
     * @param destQdslEntityPath    Destination QueryDSL entity table path
     * @param destQdslSyncIdPath Destination QueryDSL sync id field path
     * @param conversionFunction    Conversion function JOOQ record -> JPA entity
     * @param deleteBeforeUpdate    Delete all records before updating
     * @param <TEntity>             Type of destiantion JPA entity
     * @param <TCentralRecord>      Type of source JOOQ record
     */
    open protected fun <TCentralRecord : org.jooq.Record, TEntity> updateEntities(
            sourceTable: org.jooq.impl.TableImpl<TCentralRecord>,
            sourceTableSyncIdField: org.jooq.TableField<TCentralRecord, Long>?,
            destRepository: org.springframework.data.jpa.repository.JpaRepository<TEntity, *>,
            destQdslEntityPath: com.querydsl.core.types.dsl.EntityPathBase<TEntity>,
            destQdslSyncIdPath: com.querydsl.core.types.dsl.NumberPath<Long>?,
            conversionFunction: (TCentralRecord) -> TEntity,
            deleteBeforeUpdate: Boolean) {

        // Stopwatch
        val sw = com.google.common.base.Stopwatch.createStarted()
        // Log formatter
        val lfmt = { s: String -> "[${destQdslEntityPath.type.name}] ${s} $sw" }

        entityManager.flushMode = javax.persistence.FlushModeType.COMMIT

        if (deleteBeforeUpdate || destQdslSyncIdPath == null) {
            transaction.execute<Any> { _ ->
                log.info(lfmt("Deleting all entities"))
                destRepository.deleteAllInBatch()
                entityManager.flush()
                entityManager.clear()
                null
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
                        destRepository.save(entity)
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
