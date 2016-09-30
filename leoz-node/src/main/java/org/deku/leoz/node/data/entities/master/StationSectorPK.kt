package org.deku.leoz.node.data.entities.master

import sx.io.serialization.Serializable

import javax.persistence.Id

/**
 * Created by JT on 29.06.15.
 */
@Serializable(uid = 0x4a81cc447bdc43L)
class StationSectorPK(
        @Id var stationNr: Int? = null,
        @Id var sector: String? = null
) : java.io.Serializable {
    companion object {
        const val serialVersionUID = 0x4a81cc447bdc43L
    }
}
