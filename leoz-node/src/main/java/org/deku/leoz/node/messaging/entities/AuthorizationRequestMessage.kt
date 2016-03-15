package org.deku.leoz.node.messaging.entities

import java.io.Serializable

/**
 * Authorization request, sent from nodes to central
 * Created by masc on 30.06.15.
 */
data class AuthorizationRequestMessage(
        var key: String = "",
        var name: String = "",
        var systemInfo: String = ""
) : Serializable {
    companion object {
        private val serialVersionUID = -6588650210003644996L
    }
}
