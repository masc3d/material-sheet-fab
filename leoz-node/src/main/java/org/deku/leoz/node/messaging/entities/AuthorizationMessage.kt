package org.deku.leoz.node.messaging.entities

import java.io.Serializable

/**
 * Authorization message, sent to and consumed by nodes
 * Created by masc on 30.06.15.
 */
data class AuthorizationMessage(
        var key: String = "",
        var authorized: Boolean = false,
        /** If the key was rejected for any reason, eg. the short representation of key was a duplicate */
        var rejected: Boolean = false)
        :
        Serializable
{
    companion object {
        private val serialVersionUID = 941655435886909768L
    }
}
