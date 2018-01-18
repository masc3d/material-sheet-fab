package org.deku.leoz.node.service.internal.sync

import sx.io.serialization.Serializable

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
