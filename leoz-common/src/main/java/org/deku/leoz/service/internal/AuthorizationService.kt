package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiOperation
import sx.io.serialization.Serializable
import sx.rs.PATCH
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Authorization service
 * Created by masc on 01.05.17.
 */
@Path("internal/v1/authorize")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Authorization operations")
interface AuthorizationService {
    /**
     * Authorization request, sent from nodes to central
     * Created by masc on 30.06.15.
     */
    @Serializable(0xfac82346eb333e)
    data class NodeRequest(
            /** Pre-generated key */
            var key: String = "",
            /** (Bundle) name of the instance, one of the string values of {@link BundleType} */
            var name: String = "",
            /** Opaque system info (usually json blob) */
            var systemInfo: String = ""
    )

    /**
     * Authorization message, sent to and consumed by nodes
     * Created by masc on 30.06.15.
     */
    @Serializable(0xde6de342d7a635)
    data class NodeResponse(
            var key: String = "",
            /** Key was authorized or not */
            var authorized: Boolean = false,
            /** If the key was rejected for any reason, eg. the short representation of key was a duplicate */
            var rejected: Boolean = false)

    /**
     * User credentials
     */
    data class Credentials(
            var name: String = "",
            var password: String = ""
    )

    /**
     * Mobile device info
     */
    data class Mobile(
            /** Device model */
            var model: String = "",
            /** Device serial number */
            var serial: String = "",
            /** Device IMEI */
            var imei: String = ""
    )

    /**
     * Mobile authorization request
     */
    data class MobileRequest(
            var user: Credentials? = null,
            var mobile: Mobile? = null
    )

    /**
     * Mobile authorization response
     */
    data class MobileResponse(
            var key: String = ""
    )

    /**
     * Request authorization
     * @param request Authorization request
     */
    @PATCH
    @Path("/mobile")
    @ApiOperation(value = "Request mobile device authorization")
    fun authorizeMobile(request: MobileRequest): MobileResponse
}