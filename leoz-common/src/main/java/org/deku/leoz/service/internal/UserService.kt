package org.deku.leoz.service.internal

import org.deku.leoz.service.internal.entity.User
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
        const val ID = "userid"
    }

    /**
     * Get user
     * @param email User email
     */
    @GET
    @Path("/")
    @ApiOperation(value = "Get user")
    fun get(@QueryParam(EMAIL) @ApiParam(value = "User email address") email: String? = null): User?

    @sx.rs.PATCH
    @Path("/update/")
    @ApiOperation(value = "Create or update user")
    fun update(@ApiParam(value = "user") user: User): Boolean

    /**
    @DELETE
    @Path("/delete/{${EMAIL}}")
    @ApiOperation(value = "delete user")
    fun delete(@ApiParam(value = "email") @PathParam(EMAIL) email: String): Boolean
     **/
    @DELETE
    @Path("/delete/{${ID}}")
    @ApiOperation(value = "delete user")
    fun delete(@ApiParam(value = "ID") @PathParam(ID) id: Int): Boolean
}