package org.deku.leoz.central.config

import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.central.service.internal.sync.DatabaseSyncService
import org.deku.leoz.central.service.internal.sync.NotifyPreset
import org.deku.leoz.central.service.internal.sync.Preset
import org.deku.leoz.central.service.internal.sync.SyncPreset
import org.deku.leoz.node.data.jpa.*
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.threeten.bp.Duration
import sx.mq.MqBroker
import sx.mq.jms.activemq.ActiveMQBroker
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Leoz-central database sync configuration
 * Created by masc on 30.06.15.
 */
@Configuration
@Lazy(false)
class DatabaseSyncConfiguration {
    private var log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var dbSyncService: DatabaseSyncService

    @get:Bean
    val syncPresets = listOf<Preset<*>>(
            SyncPreset(
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
                    },
                    accurateDeletes = true
            ),

            SyncPreset(
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
                    },
                    accurateDeletes = true
            ),

            SyncPreset(
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
                    },
                    accurateDeletes = true
            ),

            SyncPreset(
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
                    },
                    accurateDeletes = true
            ),

            SyncPreset(
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

            SyncPreset(
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
                    },
                    accurateDeletes = true
            ),

            SyncPreset(
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
                    },
                    accurateDeletes = true
            ),

            SyncPreset(
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
                    },
                    accurateDeletes = true
            ),

            SyncPreset(
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
                    },
                    accurateDeletes = true
            ),

            SyncPreset(
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
                    },
                    accurateDeletes = true
            ),

//            SyncPreset(
//                    Tables.TAD_NODE_GEOPOSITION,
//                    Tables.TAD_NODE_GEOPOSITION.SYNC_ID,
//                    QTadNodeGeoposition.tadNodeGeoposition,
//                    QTadNodeGeoposition.tadNodeGeoposition.syncId,
//                    { s ->
//                        TadNodeGeoposition().also { d ->
//                            d.id = s.positionId.toLong()
//                            d.userId = s.userId
//                            d.nodeId = s.nodeId
//                            d.tsCreated = s.tsCreated
//                            d.tsUpdated = s.tsUpdated
//                            d.latitude = s.latitude
//                            d.longitude = s.longitude
//                            d.positionDatetime = s.positionDatetime
//                            d.speed = s.speed
//                            d.bearing = s.bearing
//                            d.altitude = s.altitude
//                            d.accuracy = s.accuracy
//                            d.vehicleType = s.vehicleType
//                            d.debitorId = s.debitorId
//                            d.syncId = s.syncId
//                            //TODO
//                            //d.node_uid
//                        }
//                    }
//            ),

            NotifyPreset(
                    Tables.RKKOPF,
                    Tables.RKKOPF.SYNC_ID
            )
    )

    /**
     * On initialization
     */
    @PostConstruct
    fun onInitialize() {
        // Wire broker event
        ActiveMQBroker.instance.delegate.add(brokerEventListener)

        if (ActiveMQBroker.instance.isStarted)
            brokerEventListener.onStart()

        // TODO: requires capability to control sync per table to prevent constant lookups on frequently changing tables
        dbSyncService.interval = Duration.ofSeconds(5)
    }

    /** Broker event listener  */
    private val brokerEventListener = object : MqBroker.DefaultEventListener() {
        override fun onStart() {
            dbSyncService.start()
        }

        override fun onStop() {
            dbSyncService.stop()
        }
    }
}
