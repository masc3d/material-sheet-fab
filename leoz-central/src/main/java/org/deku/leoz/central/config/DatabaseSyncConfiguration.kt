package org.deku.leoz.central.config

import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.MstKeyRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.MstNodeRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.MstUserRecord
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.central.service.internal.sync.*
import org.deku.leoz.node.data.jpa.*
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.threeten.bp.Duration
import sx.mq.MqBroker
import sx.mq.jms.activemq.ActiveMQBroker
import sx.security.DigestType
import sx.security.hashUUid
import sx.time.toTimestamp
import sx.util.toByteArray
import sx.util.toUUID
import java.nio.ByteBuffer
import java.sql.Timestamp
import java.util.*
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
            //region mst_bundle_version
            SyncPreset(
                    Tables.MST_BUNDLE_VERSION,
                    QMstBundleVersion.mstBundleVersion,

                    Tables.MST_BUNDLE_VERSION.SYNC_ID,
                    QMstBundleVersion.mstBundleVersion.syncId,
                    { s ->
                        MstBundleVersion().also { d ->
                            d.id = s.id.toLong()
                            d.syncId = s.syncId

                            d.bundle = s.bundle
                            d.alias = s.alias
                            d.version = s.version
                        }
                    },

                    accurateDeletes = true
            ),
            //endregion

            //region tbldepotliste
            SyncPreset(
                    Tables.TBLDEPOTLISTE,
                    QMstStation.mstStation,

                    Tables.TBLDEPOTLISTE.SYNC_ID,
                    QMstStation.mstStation.syncId,
                    { s ->
                        MstStation().also { d ->
                            d.stationId = s.id
                            d.syncId = s.syncId

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
                            d.exportValuablesAllowed = if (s.valok == 1.toUInteger()) 1 else 0
                            d.exportValuablesWithoutBagAllowed = if (s.valokWithoutBag == 1) 1 else 0
                        }
                    },

                    accurateDeletes = true
            ),
            //endregion

            //region mst_country
            SyncPreset(
                    Tables.MST_COUNTRY,
                    QMstCountry.mstCountry,

                    Tables.MST_COUNTRY.SYNC_ID,
                    QMstCountry.mstCountry.syncId,
                    { s ->
                        MstCountry().also { d ->
                            d.syncId = s.syncId

                            d.code = s.code
                            d.timestamp = s.timestamp
                            d.routingTyp = s.routingTyp
                            d.minLen = s.minLen
                            d.maxLen = s.maxLen
                            d.zipFormat = s.zipFormat
                        }
                    },

                    accurateDeletes = true
            ),
            //endregion

            //region mst_debitor
            SyncPreset(
                    Tables.MST_DEBITOR,
                    QMstDebitor.mstDebitor,

                    Tables.MST_DEBITOR.SYNC_ID,
                    QMstDebitor.mstDebitor.syncId,
                    { s ->
                        MstDebitor().also { d ->
                            d.syncId = s.syncId

                            d.debitorId = s.debitorId
                            d.debitorNr = s.debitorNr
                            d.tsCreated = s.tsCreated
                            d.tsUpdated = s.tsUpdated
                            d.parentId = s.parentId
                        }
                    },

                    accurateDeletes = true
            ),
            //endregion

            //region mst_holidayctrl
            SyncPreset(
                    Tables.MST_HOLIDAYCTRL,
                    QMstHolidayCtrl.mstHolidayCtrl,

                    Tables.MST_HOLIDAYCTRL.SYNC_ID,
                    QMstHolidayCtrl.mstHolidayCtrl.syncId,
                    { s ->
                        MstHolidayCtrl().also { d ->
                            d.id = s.id.toLong()
                            d.syncId = s.syncId

                            d.country = s.country
                            d.ctrlPos = s.ctrlPos
                            d.description = s.description
                            d.holiday = s.holiday
                            d.timestamp = s.timestamp
                        }
                    },

                    accurateDeletes = true
            ),
            //endregion

            //region mst_route
            SyncPreset(
                    Tables.MST_ROUTE,
                    QMstRoute.mstRoute,

                    Tables.MST_ROUTE.SYNC_ID,
                    QMstRoute.mstRoute.syncId,
                    { s ->
                        MstRoute().also { d ->
                            d.id = s.id.toLong()
                            d.syncId = s.syncId

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
                        }
                    }
            ),
            //endregion

            //region mst_key
            SyncPreset(
                    Tables.MST_KEY,
                    QMstKey.mstKey,

                    Tables.MST_KEY.SYNC_ID,
                    QMstKey.mstKey.syncId,
                    { s ->
                        MstKey().also { d ->
                            d.uid = s.uid.toUUID()
                            d.syncId = s.syncId

                            d.key = s.key
                            d.type = s.type
                            d.timestamp = s.timestamp
                        }
                    },

                    Tables.MST_KEY.UID,
                    QMstKey.mstKey.uid,

                    { d, s ->
                        s.uid = d.uid.toByteArray()

                        s.key = d.key
                        s.type = d.type
                        s.timestamp = d.timestamp
                    },

                    accurateDeletes = true
            ),
            //endregion

            //region mst_node
            SyncPreset(
                    Tables.MST_NODE,
                    QMstNode.mstNode,

                    Tables.MST_NODE.SYNC_ID,
                    QMstNode.mstNode.syncId,
                    { s ->
                        MstNode().also { d ->
                            d.uid = s.uid.toUUID()
                            d.syncId = s.syncId

                            d.authorized = s.authorized
                            d.bundle = s.bundle
                            d.config = s.config?.toString()
                            d.currentVersion = s.currentVersion
                            d.bundle = s.bundle
                            d.debitorId = s.debitorId
                            d.serial = s.serial
                            d.sysInfo = s.sysInfo
                            d.tsLastlogin = s.tsLastlogin
                            d.tsModified = s.timestamp
                            d.versionAlias = s.versionAlias
                        }
                    },

                    Tables.MST_NODE.UID,
                    QMstNode.mstNode.uid,
                    { d, s ->
                        s.uid = d.uid.toByteArray()

                        s.authorized = d.authorized
                        s.bundle = d.bundle
                        s.config = d.config?.toString()
                        s.currentVersion = d.currentVersion
                        s.bundle = d.bundle
                        s.debitorId = d.debitorId
                        s.serial = d.serial
                        s.sysInfo = d.sysInfo
                        s.tsLastlogin = d.tsLastlogin
                        s.timestamp = d.tsModified
                        s.versionAlias = d.versionAlias
                    },

                    accurateDeletes = true
            ),
            //endregion

            //region mst_routinglayer
            SyncPreset(
                    Tables.MST_ROUTINGLAYER,
                    QMstRoutingLayer.mstRoutingLayer,

                    Tables.MST_ROUTINGLAYER.SYNC_ID,
                    QMstRoutingLayer.mstRoutingLayer.syncId,
                    { s ->
                        MstRoutingLayer().also { d ->
                            d.syncId = s.syncId

                            d.layer = s.layer
                            d.services = s.services
                            d.description = s.description
                            d.timestamp = s.timestamp
                            d.syncId = s.syncId
                        }
                    },

                    accurateDeletes = true
            ),
            //endregion

            //region mst_sector
            SyncPreset(
                    Tables.MST_SECTOR,
                    QMstSector.mstSector,

                    Tables.MST_SECTOR.SYNC_ID,
                    QMstSector.mstSector.syncId,
                    { s ->
                        MstSector().also { d ->
                            d.id = s.id.toLong()
                            d.syncId = s.syncId

                            d.sectorFrom = s.sectorfrom
                            d.sectorTo = s.sectorto
                            d.timestamp = s.timestamp
                            d.validFrom = s.validfrom
                            d.validTo = s.validto
                        }
                    },

                    accurateDeletes = true
            ),
            //endregion

            //region mst_station_contract
            SyncPreset(
                    Tables.MST_V_STATION_CONTRACT,
                    QMstStationContract.mstStationContract,

                    Tables.MST_V_STATION_CONTRACT.SYNC_ID,
                    QMstStationContract.mstStationContract.syncId,
                    { s ->
                        MstStationContract().also { d ->
                            d.id = s.id
                            d.syncId = s.syncId

                            d.debitorId = s.debitorId
                            d.stationId = s.stationId
                            d.contractType = s.contractType
                            d.contractNo = s.contractNo
                            d.activeFrom = s.activeFrom.toTimestamp()
                            d.activeTo = s.activeTo.toTimestamp()
                        }
                    }
            ),
            //endregion

            //region mst_station_sector
            SyncPreset(
                    Tables.MST_STATION_SECTOR,
                    QMstStationSector.mstStationSector,

                    Tables.MST_STATION_SECTOR.SYNC_ID,
                    QMstStationSector.mstStationSector.syncId,
                    { s ->
                        MstStationSector().also { d ->
                            d.id = s.id.toLong()
                            d.syncId = s.syncId

                            d.stationNr = s.stationNr
                            d.sector = s.sector
                            d.routingLayer = s.routingLayer
                            d.timestamp = s.timestamp
                        }
                    },

                    accurateDeletes = true
            ),
            //endregion

            //region mst_user
            SyncPreset(
                    Tables.MST_USER,
                    QMstUser.mstUser,

                    Tables.MST_USER.SYNC_ID,
                    QMstUser.mstUser.syncId,
                    { s ->
                        MstUser().also { d ->
                            d.uid = s.uid?.toUUID()

                            d.keyUid = s.keyUid?.toUUID()
                            d.active = s.active
                            d.alias = s.alias
                            d.config = s.config?.toString()
                            d.debitorId = s.debitorId.toLong()
                            d.email = s.email
                            d.expiresOn = s.expiresOn
                            d.externalUser = s.externalUser
                            d.firstname = s.firstname
                            d.lastname = s.lastname
                            d.password = s.password
                            d.passwordExpiresOn = s.passwordExpiresOn
                            d.phone = s.phone
                            d.phoneMobile = s.phoneMobile
                            d.preferences = s.preferences?.toString()
                            d.role = s.role
                            d.syncId = s.syncId
                            d.tsCreated = s.tsCreated
                            d.tsLastlogin = s.tsLastlogin
                            d.tsUpdated = s.tsUpdated ?: Date().toTimestamp()
                        }
                    },

                    Tables.MST_USER.UID,
                    QMstUser.mstUser.uid,

                    { d, s ->
                        s.uid = d.uid.toByteArray()

                        s.keyId = 0
                        s.keyUid = d.keyUid?.toByteArray()
                        s.active = d.active
                        s.alias = d.alias
                        s.config = d.config?.toString()
                        s.debitorId = d.debitorId.toInt()
                        s.email = d.email
                        s.expiresOn = d.expiresOn
                        s.externalUser = d.externalUser
                        s.firstname = d.firstname
                        s.lastname = d.lastname
                        s.password = d.password
                        s.passwordExpiresOn = d.passwordExpiresOn
                        s.phone = d.phone
                        s.phoneMobile = d.phoneMobile
                        s.preferences = d.preferences?.toString()
                        s.role = d.role
                        s.tsCreated = d.tsCreated
                        s.tsLastlogin = d.tsLastlogin
                        s.tsUpdated = d.tsUpdated
                    },

                    accurateDeletes = true
            ),
            //endregion


            //region tad_node_geposition
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
            //endregion

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
