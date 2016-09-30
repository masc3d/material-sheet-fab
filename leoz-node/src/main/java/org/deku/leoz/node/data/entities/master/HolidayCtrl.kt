package org.deku.leoz.node.data.entities.master

import sx.io.serialization.Serializable

import javax.persistence.*
import java.sql.Timestamp

/**
 * Created by JT on 11.05.15.
 */
@Entity
@Table(name = "mst_holidayctrl")
@IdClass(HolidayCtrlPK::class)
@Serializable(uid = 0x56c4d6ff7b69dcL)
class HolidayCtrl {
    @Id
    var holiday: Timestamp? = null
    @Basic
    var ctrlPos: Int? = null
    @Id
    var country: String? = null
    @Basic
    var description: String? = null
    @Basic
    @Column(nullable = false)
    var timestamp: Timestamp? = null
    @Basic
    @Column(nullable = false)
    var syncId: Long? = null
}
