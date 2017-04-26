package org.deku.leoz.rest.service.internal.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.deku.leoz.rest.entity.internal.v1.ApplicationVersion
import org.deku.leoz.rest.entity.internal.v1.User
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * User service
 * Created by masc on 09.10.15.
 */
@Path("internal/v1/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "User operations")
interface UserService {
    companion object {
        const val EMAIL = "email"
    }

    /**
     * Get user
     * @param email User email
     */
    @GET
    @Path("/")
    @ApiOperation(value = "Get user", response = String::class)
    fun get(@QueryParam(EMAIL) @ApiParam(value = "User email address") email: String? = null): User
}