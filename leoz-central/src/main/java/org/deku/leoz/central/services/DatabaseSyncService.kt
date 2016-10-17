package org.deku.leoz.central.services

import com.google.common.base.Stopwatch
import com.querydsl.core.types.dsl.DateTimePath
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.jpa.impl.JPAQuery
import org.deku.leoz.central.data.entities.jooq.Tables
import org.deku.leoz.central.data.entities.jooq.tables.*
import org.deku.leoz.central.data.entities.jooq.tables.records.*
import org.deku.leoz.central.data.repositories.GenericJooqRepository
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.data.repositories.master.BundleVersionRepository
import org.deku.leoz.node.data.entities.*
import org.deku.leoz.node.data.entities.MstBundleVersion
import org.deku.leoz.node.data.entities.MstCountry
import org.deku.leoz.node.data.entities.MstRoute
import org.deku.leoz.node.data.entities.MstSector
import org.deku.leoz.node.data.entities.MstStation
import org.deku.leoz.node.data.entities.MstStationSector
import org.deku.leoz.node.data.repositories.master.*
import org.deku.leoz.node.data.repositories.system.PropertyRepository
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.TableImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import sx.concurrent.Service
import sx.event.EventDelegate
import sx.event.EventDispatcher
import java.sql.Timestamp
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService
import javax.inject.Inject
import javax.inject.Named
import javax.persistence.EntityManager
import javax.persistence.FlushModeType
import javax.persistence.PersistenceContext

/**
 * TODO: split configuration (which tables to sync and how) from actual implementation, move config to DatabaseSyncConfiguration
 * Created by masc on 15.05.15.
 */
@Named
open class DatabaseSyncService
@Inject
constructor(
        private val exceutorService: ScheduledExecutorService,
        @Qualifier(PersistenceConfiguration.QUALIFIER) tx: PlatformTransactionManager,
        @Qualifier(org.deku.leoz.central.config.PersistenceConfiguration.QUALIFIER) txJooq: PlatformTransactionManager) {
    /**
     * Embedded service class
     */
    private val service = object : Service(executorService = this.exceutorService, period = Duration.ofMinutes(1)) {
        override fun run() {
            this@DatabaseSyncService.sync(false)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DatabaseSyncService::class.java)

        /**
         * Convert mysql mst_bundle_version record
         */
        private fun convert(ds: MstBundleVersionRecord): MstBundleVersion {
            val s = MstBundleVersion()

            s.id = ds.id
            s.bundle = ds.bundle
            s.alias = ds.alias
            s.version = ds.version

            return s
        }

        /**
         * Convert mysql mst_station record to jpa entity
         * @param ds
         * @return
         */
        private fun convert(ds: MstStationRecord): MstStation {
            val s = MstStation()

            s.stationNr = ds.stationNr
            s.timestamp = ds.timestamp
            s.address1 = ds.address1
            s.address2 = ds.address2
            s.billingAddress1 = ds.billingAddress1
            s.billingAddress2 = ds.billingAddress2
            s.billingCity = ds.billingCity
            s.billingCountry = ds.billingCountry
            s.billingHouseNr = ds.billingHouseNr
            s.billingStreet = ds.billingStreet
            s.billingZip = ds.billingZip
            s.city = ds.city
            s.contactPerson1 = ds.contactPerson1
            s.contactPerson2 = ds.contactPerson2
            s.country = ds.country
            s.email = ds.email
            s.houseNr = ds.houseNr
            s.mobile = ds.mobile
            s.phone1 = ds.phone1
            s.phone2 = ds.phone2
            s.posLat = ds.poslat
            s.posLong = ds.poslong
            s.sector = ds.sectors
            s.servicePhone1 = ds.servicePhone1
            s.servicePhone2 = ds.servicePhone2
            // TODO: strang? strange? ;)
            s.strang = null
            s.street = ds.street
            s.telefax = ds.telefax
            s.ustid = ds.ustid
            s.webAddress = ds.webAddress
            s.zip = ds.zip
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

    private val eventDispatcher = EventDispatcher.createThreadSafe<EventListener>()
    open val eventDelegate: EventDelegate<EventListener>
        get() = eventDispatcher
    //endregion

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    // Transaction helpers
    private val transaction: TransactionTemplate
    private val transactionJooq: TransactionTemplate

    // Repositories
    @Inject
    private lateinit var genericJooqRepository: GenericJooqRepository
    @Inject
    private lateinit var bundleVersionRepository: BundleVersionRepository
    @Inject
    private lateinit var stationRepository: StationRepository
    @Inject
    private lateinit var countryRepository: CountryRepository
    @Inject
    private lateinit var routeRepository: RouteRepository
    @Inject
    private lateinit var holidayCtrlRepository: HolidayCtrlRepository
    @Inject
    private lateinit var sectorRepository: SectorRepository
    @Inject
    private lateinit var propertyRepository: PropertyRepository
    @Inject
    private lateinit var routingLayerRepository: RoutingLayerRepository
    @Inject
    private lateinit var stationSectorRepository: StationSectorRepository

    init {
        transaction = TransactionTemplate(tx)
        transaction.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW

        transactionJooq = TransactionTemplate(txJooq)
    }

    @Transactional(value = PersistenceConfiguration.QUALIFIER)
    open fun sync(reload: Boolean) {
        val sw = Stopwatch.createStarted()

        val alwaysDelete = reload

        this.updateEntities<MstBundleVersionRecord, MstBundleVersion>(
                Tables.MST_BUNDLE_VERSION,
                null,
                bundleVersionRepository,
                QMstBundleVersion.mstBundleVersion,
                null,
                { s -> convert(s) },
                alwaysDelete)

        this.updateEntities<MstStationRecord, MstStation>(
                Tables.MST_STATION,
                org.deku.leoz.central.data.entities.jooq.tables.MstStation.MST_STATION.SYNC_ID,
                stationRepository,
                QMstStation.mstStation,
                QMstStation.mstStation.syncId,
                { s -> convert(s) },
                alwaysDelete)

        this.updateEntities(
                Tables.MST_COUNTRY,
                org.deku.leoz.central.data.entities.jooq.tables.MstCountry.MST_COUNTRY.SYNC_ID,
                countryRepository,
                QMstCountry.mstCountry,
                QMstCountry.mstCountry.syncId,
                { s -> convert(s) },
                alwaysDelete)

        this.updateEntities(
                Tables.MST_HOLIDAYCTRL,
                org.deku.leoz.central.data.entities.jooq.tables.MstHolidayctrl.MST_HOLIDAYCTRL.SYNC_ID,
                holidayCtrlRepository,
                QMstHolidayCtrl.mstHolidayCtrl,
                QMstHolidayCtrl.mstHolidayCtrl.syncId,
                { s -> convert(s) },
                alwaysDelete)

        this.updateEntities(
                Tables.MST_ROUTE,
                org.deku.leoz.central.data.entities.jooq.tables.MstRoute.MST_ROUTE.SYNC_ID,
                routeRepository,
                QMstRoute.mstRoute,
                QMstRoute.mstRoute.syncId,
                { s -> convert(s) },
                alwaysDelete)

        this.updateEntities(
                Tables.MST_SECTOR,
                org.deku.leoz.central.data.entities.jooq.tables.MstSector.MST_SECTOR.SYNC_ID,
                sectorRepository,
                QMstSector.mstSector,
                QMstSector.mstSector.syncId,
                { s -> convert(s) },
                alwaysDelete)

        this.updateEntities(
                Tables.MST_ROUTINGLAYER,
                org.deku.leoz.central.data.entities.jooq.tables.MstRoutinglayer.MST_ROUTINGLAYER.SYNC_ID,
                routingLayerRepository,
                QMstRoutingLayer.mstRoutingLayer,
                QMstRoutingLayer.mstRoutingLayer.syncId,
                { s -> convert(s) },
                alwaysDelete)

        this.updateEntities(
                Tables.MST_STATION_SECTOR,
                org.deku.leoz.central.data.entities.jooq.tables.MstStationSector.MST_STATION_SECTOR.SYNC_ID,
                stationSectorRepository,
                QMstStationSector.mstStationSector,
                QMstStationSector.mstStationSector.syncId,
                { s -> convert(s) },
                alwaysDelete)

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
    open protected fun <TCentralRecord : Record, TEntity> updateEntities(
            sourceTable: TableImpl<TCentralRecord>,
            sourceTableSyncIdField: TableField<TCentralRecord, Long>?,
            destRepository: JpaRepository<TEntity, *>,
            destQdslEntityPath: EntityPathBase<TEntity>,
            destQdslSyncIdPath: NumberPath<Long>?,
            conversionFunction: (TCentralRecord) -> TEntity,
            deleteBeforeUpdate: Boolean) {

        // Stopwatch
        val sw = Stopwatch.createStarted()
        // Log formatter
        val lfmt = { s: String -> "[${destQdslEntityPath.type.name}] ${s} ${sw.toString()}" }

        entityManager.flushMode = FlushModeType.COMMIT

        if (deleteBeforeUpdate || destQdslSyncIdPath == null) {
            transaction.execute<Any> { ts ->
                log.info(lfmt("Deleting all entities"))
                destRepository.deleteAllInBatch()
                entityManager.flush()
                entityManager.clear()
                null
            }
        }

        // Get latest timestamp
        var destMaxSyncId: Long? = null
        if (destQdslSyncIdPath != null) {
            // Query embedded database table for latest timestamp
            destMaxSyncId = JPAQuery<TEntity>(entityManager)
                    .from(destQdslEntityPath)
                    .select(destQdslSyncIdPath.max())
                    .fetchFirst()
        }

        // Get newer records from central
        // masc20150530. JOOQ cursor requires an explicit transaction
        transactionJooq.execute<Any> { tsJooq ->
            // Read source records newer than destination timestamp
            val source = genericJooqRepository.findNewerThan(
                    destMaxSyncId,
                    sourceTable,
                    sourceTableSyncIdField)

            if (source.hasNext()) {
                // Save to destination/jpa
                // REMARKS
                // * saving/transaction commit gets very slow when deleting and inserting within the same transaction
                log.info(lfmt("Outdated [[${destMaxSyncId}]"))
                var count = 0
                transaction.execute<Any> { ts ->
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
                    destMaxSyncId = JPAQuery<TEntity>(entityManager)
                            .from(destQdslEntityPath)
                            .select(destQdslSyncIdPath.max())
                            .fetchFirst()
                }
                log.info(lfmt("Updated ${count} entities [${destMaxSyncId}]"))

                // Emit update event
                eventDispatcher.emit { e ->
                    // Emit event
                    e.onUpdate(destQdslEntityPath.getType(), destMaxSyncId)
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
}
