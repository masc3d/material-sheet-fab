package org.deku.leoz.service.internal

import sx.io.serialization.Serializable
import sx.rs.PATCH
import javax.ws.rs.Path

/**
 * Created by masc on 01.05.17.
 */
interface AuthorizationService {
    /**
     * Authorization request, sent from nodes to central
     * Created by masc on 30.06.15.
     */
    @Serializable(0xfac82346eb333e)
    data class Request(
            var key: String = "",
            var name: String = "",
            var systemInfo: String = ""
    )

    /**
     * Authorization message, sent to and consumed by nodes
     * Created by masc on 30.06.15.
     */
    @Serializable(0xde6de342d7a635)
    data class Response(
            var key: String = "",
            var authorized: Boolean = false,
            /** If the key was rejected for any reason, eg. the short representation of key was a duplicate */
            var rejected: Boolean = false)

    @PATCH
    @Path("/authorize")
    fun authorize(request: Request): Response
}