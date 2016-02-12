package org.deku.leoz.node.data.sync.v1

import java.io.Serializable
import java.sql.Timestamp

/**
 * Message sent by
 * - consumers to request entity updates
 * - publishers to indicate entity updates
 * Created by masc on 18.06.15.
 *
 * @param entityType Entity type this state message refers to
 * @param timestamp Latest entity timestamp
 */
class EntityStateMessage(
        val entityType: Class<*>? = null,
        val timestamp: Timestamp? = null
) : Serializable {

    companion object {
        private val serialVersionUID = -3506609875846947166L
    }
}
