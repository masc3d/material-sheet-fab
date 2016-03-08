package org.deku.leoz.node.messaging.entities

import java.io.Serializable

/**
 * Authorization message retrieved by node clients.
 * Created by masc on 30.06.15.
 */
class AuthorizationMessage : Serializable {
    companion object {
        private val serialVersionUID = 941655435886909768L
    }

    var key: String = ""
    var authorized: Boolean? = null

    override fun toString(): String {
        return "Authorization key [${key}] authorized [${authorized}]"
    }
}
