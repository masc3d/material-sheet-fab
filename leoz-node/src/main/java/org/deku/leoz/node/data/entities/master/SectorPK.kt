package org.deku.leoz.node.data.entities.master

import sx.io.serialization.Serializable

import javax.persistence.Id
import java.sql.Timestamp

/**
 * Created by JT on 11.05.15.
 */
@Serializable(uid = 0xd432b301532a4aL)
class SectorPK(
        @Id var sectorFrom: String? = null,
        @Id var sectorTo: String? = null,
        @Id var validFrom: Timestamp? = null
) : java.io.Serializable {
    companion object {
        const val serialVersionUID = 0xd432b301532a4aL
    }
}
