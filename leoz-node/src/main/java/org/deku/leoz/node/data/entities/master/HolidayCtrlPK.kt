package org.deku.leoz.node.data.entities.master

import sx.io.serialization.Serializable
import java.sql.Timestamp
import javax.persistence.Id

/**
 * Created by JT on 11.05.15.
 */
@Serializable(uid = 0x0bef5f538e4ed9L)
class HolidayCtrlPK(
        @Id var holiday: Timestamp? = null,
        @Id var country: String? = null
) : java.io.Serializable {
    companion object {
        const val serialVersionUID = 0x0bef5f538e4ed9L
    }
}
