package org.deku.leoz.node.service.internal.authorization

import sx.io.serialization.Serializable

/**
 * Authorization message, sent to and consumed by nodes
 * Created by masc on 30.06.15.
 */
@Serializable(0xde6de342d7a635)
data class Authorization(
        var key: String = "",
        var authorized: Boolean = false,
        /** If the key was rejected for any reason, eg. the short representation of key was a duplicate */
        var rejected: Boolean = false)
