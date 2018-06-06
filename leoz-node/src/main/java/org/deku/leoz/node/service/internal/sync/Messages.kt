package org.deku.leoz.node.service.internal.sync

import org.deku.leoz.service.entity.Message
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
) : Message()


/**
 * Message sent by publishers prior to the actual entity payload, containing meta information
 * eg. amount of entites to expect
 * Created by masc on 18.06.15.
 */
@Serializable(0x4823fac50e4fdf)
data class EntityUpdateMessage(
        /** Amount of entities/records to follow up */
        val amount: Long = 0,
        /** Maximum batch size */
        val batchSize: Int = 0) {
    companion object {
        /** Property set on last message to indicate end of stream  */
        val EOS_PROPERTY = "eos"
    }
}
