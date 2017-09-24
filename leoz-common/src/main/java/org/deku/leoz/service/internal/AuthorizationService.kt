package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiOperation
import org.deku.leoz.service.internal.UserService.User
import sx.io.serialization.Serializable
import sx.rs.PATCH
import sx.rs.auth.ApiKey
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
@ApiKey(false)
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
     * Web authorization response
     */
    data class Response(
            var key: String = "",
            var user: User? = null
    )

    /**
     * Request authorization
     * @param request Authorization request
     */
    @PATCH
    @Path("/web")
    @ApiOperation(value = "Request web authorization")
    fun authorize(request: Credentials): Response

    companion object {
        // TODO: extension methods on interface level will break feign. also this looks like it rather belongs to mst_user/leoz-central
        val User.isActive: Int
            get() = if (this.active == null || this.active == false) 0 else -1
    }
}