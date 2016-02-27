package org.deku.leoz.node.messaging.entities

import java.io.Serializable

/**
 * Identity information message sent from node clients to central.
 * Created by masc on 30.06.15.
 */
class IdentityMessage : Serializable {
    companion object {
        private val serialVersionUID = -6588650210003644996L
    }

    var id: Int? = null
    var key: String = ""
    var name: String = ""
    var hardwareAddress: String = ""
    var systemInfo: String = ""

    override fun toString(): String {
        return "Identity message node id [${id}] name [${name}] key [${key}] hw address [${hardwareAddress}] system info [${systemInfo}]"
    }
}
