package org.deku.leoz.central.services

import com.google.common.base.Stopwatch
import com.mysema.query.jpa.impl.JPAQuery
import com.mysema.query.types.path.DateTimePath
import com.mysema.query.types.path.EntityPathBase
import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.data.entities.jooq.Tables
import org.deku.leoz.central.data.entities.jooq.tables.*
import org.deku.leoz.central.data.entities.jooq.tables.records.*
import org.deku.leoz.central.data.repositories.GenericRepository
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.data.entities.master.*
import org.deku.leoz.node.data.repositories.master.*
import org.deku.leoz.node.data.repositories.system.PropertyRepository
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.TableImpl
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
        exceutorService: ScheduledExecutorService,
        @Qualifier(PersistenceConfiguration.QUALIFIER) tx: PlatformTransactionManager,
        @Qualifier(org.deku.leoz.central.config.PersistenceConfiguration.QUALIFIER) txJooq: PlatformTransactionManager)
:
        Service(exceutorService, period = Duration.ofMinutes(10))
{
    companion object {
        private val log = LogFactory.getLog(DatabaseSyncService::class.java)

        /**
         * Convert mysql mst_station record to jpa entity
         * @param ds
         * @return
         */
        private fun convert(ds: MstStationRecord): Station {
            val s = Station()

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
            s.setuStId(ds.ustid)
            s.webAddress = ds.webAddress
            s.zip = ds.zip
            return s
        }

        /**
         * Convert mysql country record to jpa entity
         * @param cr
         * @return
         */
        private fun convert(cr: MstCountryRecord): Country {
            val c = Country()

            c.code = cr.code
            //        c.getNameStringId(cr.getNameStringid() );
            c.timestamp = cr.timestamp
            c.routingTyp = cr.routingTyp
            c.minLen = cr.minLen
            c.maxLen = cr.maxLen
            c.zipFormat = cr.zipFormat

            return c
        }

        /**
         * Convert mysql holidayctrl record to jpa entity
         * @param cr
         * @return
         */
        private fun convert(cr: MstHolidayctrlRecord): HolidayCtrl {
            val d = HolidayCtrl()

            d.country = cr.country
            d.ctrlPos = cr.ctrlPos
            d.description = cr.description
            d.holiday = cr.holiday
            d.timestamp = cr.timestamp

            return d
        }

        /**
         * Convert mysql sector record to jpa entity
         * @param cr
         * @return
         */
        private fun convert(cr: MstSectorRecord): Sector {
            val d = Sector()

            d.sectorFrom = cr.sectorfrom
            d.sectorTo = cr.sectorto
            d.timestamp = cr.timestamp
            d.validFrom = cr.validfrom
            d.validTo = cr.validto

            return d
        }

        /**
         * Convert mysql value record to jpa entity
         * @param rs
         * @return
         */
        private fun convert(rs: MstRoutinglayerRecord): RoutingLayer {
            val d = RoutingLayer()

            d.layer = rs.layer
            d.services = rs.services
            d.description = rs.description
            d.timestamp = rs.timestamp

            return d
        }


        /**
         * Convert mysql route record to jpa entity
         * @param sr
         * @return
         */
        private fun convert(sr: MstRouteRecord): Route {
            val d = Route()

            d.id = sr.id.toLong()
            d.layer = sr.layer
            d.country = sr.country
            d.zipFrom = sr.zipfrom
            d.zipTo = sr.zipto
            d.validCRTR = sr.validCtrl
            d.validFrom = sr.validfrom
            d.validTo = sr.validto
            d.timestamp = sr.timestamp
            d.station = sr.station
            d.area = sr.area
            d.etod = sr.etod
            d.ltop = sr.ltop
            d.term = sr.term
            d.saturdayOK = sr.saturdayOk
            d.ltodsa = sr.ltodsa
            d.ltodholiday = sr.ltodholiday
            d.island = sr.island
            d.holidayCtrl = sr.holidayctrl

            return d
        }

        /**
         * Convert mysql stationsectors record to jpa entity
         * @param ss
         * @return
         */
        private fun convert(ss: MstStationSectorRecord): StationSector {
            val s = StationSector()
            s.stationNr = ss.stationNr
            s.sector = ss.sector
            s.routingLayer = ss.routingLayer
            s.timestamp = ss.timestamp
            return s
        }
    }

    //region Events
    interface EventListener : sx.event.EventListener {
        /** Emitted when entities have been updated  */
        fun onUpdate(entityType: Class<out Any?>, currentTimestamp: Timestamp?)
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
    private lateinit var syncRepository: GenericRepository
    @Inject
    private lateinit var stationRepository: StationRepository
    @Inject
    private lateinit var countryRepository: CountryRepository
    @Inject
    private lateinit var routeRepository: RouteRepository
    @Inject
    private lateinit var holidayCtrlRepository: HolidayctrlRepository
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
    override fun run() {
        this.sync(false)
    }

    @Transactional(value = PersistenceConfiguration.QUALIFIER)
    open fun sync(reload: Boolean) {
        val sw = Stopwatch.createStarted()

        val alwaysDelete = reload

        this.updateEntities<MstStationRecord, Station>(
                Tables.MST_STATION,
                MstStation.MST_STATION.TIMESTAMP,
                stationRepository,
                QStation.station,
                QStation.station.timestamp,
                { s -> convert(s) },
                alwaysDelete)

        this.updateEntities(
                Tables.MST_COUNTRY,
                MstCountry.MST_COUNTRY.TIMESTAMP,
                countryRepository,
                QCountry.country,
                QCountry.country.timestamp,
                { s -> convert(s) },
                alwaysDelete)

        this.updateEntities(
                Tables.MST_HOLIDAYCTRL,
                MstHolidayctrl.MST_HOLIDAYCTRL.TIMESTAMP,
                holidayCtrlRepository,
                QHolidayCtrl.holidayCtrl,
                QHolidayCtrl.holidayCtrl.timestamp,
                { s -> convert(s) },
                alwaysDelete)

        this.updateEntities(
                Tables.MST_ROUTE,
                MstRoute.MST_ROUTE.TIMESTAMP,
                routeRepository,
                QRoute.route,
                QRoute.route.timestamp,
                { s -> convert(s) },
                alwaysDelete)

        this.updateEntities(
                Tables.MST_SECTOR,
                MstSector.MST_SECTOR.TIMESTAMP,
                sectorRepository,
                QSector.sector,
                QSector.sector.timestamp,
                { s -> convert(s) },
                alwaysDelete)

        this.updateEntities(
                Tables.MST_ROUTINGLAYER,
                MstRoutinglayer.MST_ROUTINGLAYER.TIMESTAMP,
                routingLayerRepository,
                QRoutingLayer.routingLayer,
                QRoutingLayer.routingLayer.timestamp,
                { s -> convert(s) },
                alwaysDelete)

        this.updateEntities(
                Tables.MST_STATION_SECTOR,
                MstStationSector.MST_STATION_SECTOR.TIMESTAMP,
                stationSectorRepository,
                QStationSector.stationSector,
                QStationSector.stationSector.timestamp,
                { s -> convert(s) },
                alwaysDelete)

        log.info("Database sync took " + sw.toString())
    }


    /**
     * Generic updater for entites from jooq to jpa
     * @param sourceTable           JOOQ source table
     * @param sourceTableTimestampField      JOOQ source timestamp field
     * @param destRepository        Destination JPA repository
     * @param destQdslEntityPath    Destination QueryDSL entity table path
     * @param destQdslTimestampPath Destination QueryDSL timestamp field path
     * @param conversionFunction    Conversion function JOOQ record -> JPA entity
     * @param deleteBeforeUpdate    Delete all records before updating
     * @param <TEntity>             Type of destiantion JPA entity
     * @param <TCentralRecord>      Type of source JOOQ record
     */
    open protected fun <TCentralRecord : Record, TEntity> updateEntities(
            sourceTable: TableImpl<TCentralRecord>,
            sourceTableTimestampField: TableField<TCentralRecord, Timestamp>,
            destRepository: JpaRepository<TEntity, *>,
            destQdslEntityPath: EntityPathBase<TEntity>,
            destQdslTimestampPath: DateTimePath<Timestamp>?,
            conversionFunction: (TCentralRecord) -> TEntity,
            deleteBeforeUpdate: Boolean) {

        // Stopwatch
        val sw = Stopwatch.createStarted()
        // Log formatter
        val lfmt = { s: String -> "[${destQdslEntityPath.type.name}] ${s} ${sw.toString()}" }

        entityManager.flushMode = FlushModeType.COMMIT

        if (deleteBeforeUpdate || destQdslTimestampPath == null) {
            transaction.execute<Any> { ts ->
                log.info(lfmt("Deleting all entities"))
                destRepository.deleteAllInBatch()
                entityManager.flush()
                entityManager.clear()
                null
            }
        }

        // Get latest timestamp
        var destMaxTimestamp: Timestamp? = null
        if (destQdslTimestampPath != null) {
            // Query embedded database table for latest timestamp
            destMaxTimestamp = JPAQuery(entityManager)
                    .from(destQdslEntityPath)
                    .singleResult(destQdslTimestampPath.max())
        }

        // Get newer records from central
        // masc20150530. JOOQ cursor requires an explicit transaction
        transactionJooq.execute<Any> { tsJooq ->
            // Read source records newer than destination timestamp
            val source = syncRepository.findNewerThan(
                    destMaxTimestamp,
                    sourceTable,
                    sourceTableTimestampField)

            if (source.hasNext()) {
                // Save to destination/jpa
                // REMARKS
                // * saving/transaction commit gets very slow when deleting and inserting within the same transaction
                log.info(lfmt("Outdated [[${destMaxTimestamp}]"))
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
                if (destQdslTimestampPath != null) {
                    // Query embedded database for updated latest timestamp
                    destMaxTimestamp = JPAQuery(entityManager)
                            .from(destQdslEntityPath)
                            .singleResult(destQdslTimestampPath.max())
                }
                log.info(lfmt("Updated ${count} entities [${destMaxTimestamp}]"))

                // Emit update event
                eventDispatcher.emit { e ->
                    // Emit event
                    e.onUpdate(destQdslEntityPath.getType(), destMaxTimestamp)
                }
            } else {
                log.info(lfmt("Uptodate [${destMaxTimestamp}]"))
            }
            null
        }
    }
}
