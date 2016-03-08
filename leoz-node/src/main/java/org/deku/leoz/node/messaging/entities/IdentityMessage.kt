package org.deku.leoz.node.messaging.entities

import java.io.Serializable

/**
 * Identity information message sent from node clients to central.
 * Created by masc on 30.06.15.
 */
data class IdentityMessage(
        var key: String = "",
        var name: String = "",
        var systemInfo: String = ""
) : Serializable {
    companion object {
        private val serialVersionUID = -6588650210003644996L
    }
}
