package org.deku.leoz.node.service.internal.v1.authorization

import sx.io.serialization.Serializable

/**
 * Authorization request, sent from nodes to central
 * Created by masc on 30.06.15.
 */
@Serializable(0xfac82346eb333e)
data class AuthorizationRequestMessage(
        var key: String = "",
        var name: String = "",
        var systemInfo: String = ""
)
