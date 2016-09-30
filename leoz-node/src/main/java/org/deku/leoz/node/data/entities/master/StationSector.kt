package org.deku.leoz.node.data.entities.master

import sx.io.serialization.Serializable

import javax.persistence.*
import java.sql.Timestamp

/**
 * Created by JT on 29.06.15.
 */
@Entity
@Table(name = "mst_station_sector")
@IdClass(StationSectorPK::class)
@Serializable(uid = 0x0d1eaebfd81899L)
class StationSector {
    @Id
    var stationNr: Int? = null
    @Id
    var sector: String? = null
    @Basic
    var routingLayer: Int? = null
    @Basic
    var timestamp: Timestamp? = null
    @Basic
    @Column(nullable = false)
    var syncId: Long? = null
}

