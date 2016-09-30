package org.deku.leoz.node.data.entities.master

import org.eclipse.persistence.annotations.CacheIndex
import sx.io.serialization.Serializable

import javax.persistence.*
import java.sql.Timestamp

/**
 * Created by JT on 22.06.15.
 */
@Entity
@Table(name = "mst_routinglayer")
@Serializable(uid = 0x92ed6a2fc3f79fL)
class RoutingLayer {
    @Id
    var layer: Int? = null
    @CacheIndex
    @Basic
    var services: Int? = null
    @Basic
    var description: String? = null
    @Basic
    @Column(nullable = false)
    var timestamp: Timestamp? = null
    @Basic
    @Column(nullable = false)
    var syncId: Long? = null
}
