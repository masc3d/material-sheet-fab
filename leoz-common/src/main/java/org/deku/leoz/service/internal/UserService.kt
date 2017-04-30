package org.deku.leoz.service.internal

import org.deku.leoz.service.entity.internal.User
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import io.swagger.annotations.*

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
    fun get(@QueryParam(EMAIL) @ApiParam(value = "User email address") email: String? = null): org.deku.leoz.service.entity.internal.User
}