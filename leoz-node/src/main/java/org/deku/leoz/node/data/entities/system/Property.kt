package org.deku.leoz.node.data.entities.system


import sx.io.serialization.Serializable

import javax.persistence.*
import java.sql.Timestamp

/**
 * Created by JT on 29.06.15.
 */
@Entity
@Table(name = "sys_property")
@IdClass(PropertyPK::class)
@Serializable(uid = 0xaa946790064006L)
class Property {
    @Id
    var id: Int? = null
    @Id
    var station: Int? = null
    @Basic
    var description: String? = null
    @Basic
    var value: String? = null
    @Basic
    var isEnabled: Boolean = false
    @Basic
    var timestamp: Timestamp? = null
}
