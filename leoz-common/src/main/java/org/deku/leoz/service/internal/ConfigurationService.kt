package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.Authorization
import org.deku.leoz.config.Rest
import sx.rs.auth.ApiKey
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("internal/v1/configuration")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Configuration service")
@ApiKey(true)
interface ConfigurationService {

    companion object {
        const val USER_ID = "user-id"
        const val NODE_UID = "node-uid"
    }

    @GET
    @Path("/user/{${USER_ID}}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get configuration of user", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getUserConfiguration(
            @PathParam(value = USER_ID) userId: Int
    ): String

    @GET
    @Path("/node/{${NODE_UID}}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get configuration given node", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getNodeConfiguration(
            @PathParam(value = NODE_UID) @ApiParam(value = "Node UID. Needs to be the full UID, short UID is not allowed here") nodeUid: String
    ): String

}