package org.deku.leoz.node.data.sync.v1

import java.io.Serializable

/**
 * Message sent by publishers prior to the actual entity payload, containing meta information
 * eg. amount of entites to expect
 * @param amount Amount of entities/records to follow
 * Created by masc on 18.06.15.
 */
class EntityUpdateMessage(
        val amount: Long = 0)
: Serializable {
    override fun toString(): String {
        return String.format("%s amount [%d]", this.javaClass.simpleName, this.amount)
    }

    companion object {
        private val serialVersionUID = -8032738544698874536L

        /** Property set on last message to indicate end of stream  */
        val EOS_PROPERTY = "eos"
    }
}
