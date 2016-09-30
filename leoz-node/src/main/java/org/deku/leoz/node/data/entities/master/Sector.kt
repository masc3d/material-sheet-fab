package org.deku.leoz.node.data.entities.master

import sx.io.serialization.Serializable

import javax.persistence.*
import java.sql.Timestamp

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mst_sector")
@IdClass(SectorPK::class)
@Serializable(uid = 0xf1dec2bb66db87L)
class Sector {
    @Id
    var sectorFrom: String? = null
    @Id
    var sectorTo: String? = null
    @Id
    var validFrom: Timestamp? = null
    @Basic
    var validTo: Timestamp? = null
    @Basic
    var via: String? = null
    @Basic
    @Column(nullable = false)
    var timestamp: Timestamp? = null
    @Basic
    @Column(nullable = false)
    var syncId: Long? = null
}
