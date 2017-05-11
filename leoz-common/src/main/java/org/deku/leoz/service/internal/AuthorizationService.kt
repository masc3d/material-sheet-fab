package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiOperation
import org.deku.leoz.hashUserPassword
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
            @ApiModelProperty(value = "User email address", example = "user@deku.org", required = true)
            var email: String = "",
            @ApiModelProperty(value = "User password", example = "password", required = true)
            var password: String = ""
    )

    /**
     * Mobile device info
     */
    data class Mobile(
            /** Device model */
            @ApiModelProperty(value = "Mobile device model", example = "CT-50", required = true)
            var model: String = "",
            /** Device serial number */
            @ApiModelProperty(value = "Mobile device serial", example = "ABCDEFGH", required = true)
            var serial: String = "",
            /** Device IMEI */
            @ApiModelProperty(value = "Mobile device imei", example = "990000862471854", required = true)
            var imei: String = ""
    )

    /**
     * Mobile authorization request
     */
    data class MobileRequest(
            @ApiModelProperty(value = "User credentials", required = true)
            var user: Credentials? = null,
            @ApiModelProperty(value = "Mobile device info", required = true)
            var mobile: Mobile? = null
    )

    /**
     * Mobile authorization response
     */
    data class MobileResponse(
            var key: String = ""
    )

    /**
     * Web authorization response
     */
    data class WebResponse(
            var key: String = "",
            var debitorNo: String = ""
    )

    /**
     * Request authorization
     * @param request Authorization request
     */
    @PATCH
    @Path("/mobile")
    @ApiOperation(value = "Request mobile device authorization")
    fun authorizeMobile(request: MobileRequest): MobileResponse

    /**
     * Request authorization
     * @param request Authorization request
     */
    @PATCH
    @Path("/web")
    @ApiOperation(value = "Request web authorization")
    fun authorizeWeb(request: Credentials): WebResponse

    // Extensions
    fun Credentials.hashPassword(salt: ByteArray): String {
        return hashUserPassword(salt = salt, email = this.email, password = this.password)
    }
}