package org.deku.leoz.service.internal

import org.deku.leoz.service.internal.entity.User
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import io.swagger.annotations.*
import sx.rs.PATCH

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
        const val ID = "id"
        const val EMAIL = "email"
        const val DEBITORID="debitorid"
        const val DEBITORNO="debitorno"
    }

    /**
     * Get user
     * @param email User email
     */
    @GET
    @Path("/")
    @ApiOperation(value = "Get user")
    fun get(@QueryParam(EMAIL) @ApiParam(value = "User email address") email: String? = null): User

    /**
     * Get user
     * @param id Id of user
     */
    @GET
    @Path("/{${ID}}")
    @ApiOperation(value = "get user")
    fun get(@ApiParam(value = "User ID") @PathParam(ID) id: Int): User

    /**
     * Create user
     * @param user User to create
     */
    @POST
    @Path("/")
    @ApiOperation(value = "Create user")
    fun create(@ApiParam(value = "User") user: User)

    /**
     * Update user (replaces entire user)
     * @param id Id of user to update
     * @param user User entity
     */
    @PUT
    @Path("/{$ID}")
    @ApiOperation(value = "Update user")
    fun update(@ApiParam(value = "User ID") @PathParam(ID) id: Int, @ApiParam(value = "User") user: User)

    /**
     * Delete user
     * @param id Id of user to delete
     */
    @DELETE
    @Path("/{${ID}}")
    @ApiOperation(value = "Delete user")
    fun delete(@ApiParam(value = "User ID") @PathParam(ID) id: Int)

    @GET
    @Path("/debitor/{${ID}}")
    @ApiOperation(value = "Get user of debitor-id")
    fun getUserByDebitorID(@ApiParam(value = "Debitor ID") @PathParam(ID) id: Int): List<User>


}