package org.deku.leoz.node.data.entities.system

import sx.io.serialization.Serializable

import javax.persistence.Id

/**
 * Created by JT on 29.06.15.
 */
@Serializable(uid = 0x72b4356618387dL)
class PropertyPK(
        @Id var id: Int? = null,
        @Id var station: Int? = null
) : java.io.Serializable {
    companion object {
        const val serialVersionUID = 0x72b4356618387dL
    }
}
