package org.deku.leoz.node.data.entities.master

import sx.io.serialization.Serializable

import javax.persistence.*
import java.sql.Timestamp

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mst_country")
@Serializable(uid = 0x61175aa6b510b3L)
class Country {
    @Id
    var code: String? = null
    @Basic
    @Column(nullable = false)
    var timestamp: Timestamp? = null
    @Basic
    var routingTyp: Int? = null
    @Basic
    var minLen: Int? = null
    @Basic
    var maxLen: Int? = null
    @Basic
    var zipFormat: String? = null
    @Basic
    var nameStringId: Int? = null
    @Basic
    @Column(nullable = false)
    var syncId: Long? = null
}
