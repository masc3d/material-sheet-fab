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
@Api(value = "Configuration service", authorizations = arrayOf(Authorization(Rest.API_KEY)))
@ApiKey(true)
interface ConfigurationService {

    companion object {
        const val USER_ID = "user-id"
        const val NODE_UID = "node-uid"
    }

    @GET
    @Path("/user/{${USER_ID}}")
    @ApiOperation(value = "Get user configuration")
    fun getUserConfiguration(
            @PathParam(value = USER_ID) userId: Int
    ): String

    @GET
    @Path("/node/{${NODE_UID}}")
    @ApiOperation(value = "Get node configuration")
    fun getNodeConfiguration(
            @PathParam(value = NODE_UID) @ApiParam(value = "Full node uid") nodeUid: String
    ): String

    @PUT
    @Path("/user/{${USER_ID}}")
    @ApiOperation(value = "Store user configuration")
    fun putUserConfiguration(
            @PathParam(value = USER_ID) userId: Int,
            config: String
    )

    @PUT
    @Path("/node/{${NODE_UID}}")
    @ApiOperation(value = "Store node configuration")
    fun putNodeConfiguration(
            @PathParam(value = NODE_UID) @ApiParam(value = "Full node uid")
            nodeUid: String,
            config: String
    )
}