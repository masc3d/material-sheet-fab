package org.deku.leoz.node.service.internal.sync

import sx.io.serialization.Serializable

/**
 * Message sent by
 * - consumers to request entity updates
 * - publishers to indicate entity updates
 * Created by masc on 18.06.15.
 *
 * @param entityType Entity type this state message refers to
 * @param timestamp Latest entity timestamp
 */
@Serializable(0xd7247f46c96acc)
data class EntityStateMessage(
        val entityType: Class<*>? = null,
        val syncId: Long? = null
)
