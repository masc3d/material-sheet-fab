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
interface ConfigurationService {

    data class Configuration(
            val scope: Scope,
            val settings: Map<String, String>
    ) {
        enum class Scope {
            WEB,
            NODE,
            MOBILE
        }
    }

    data class UserConfiguration(
            val schema: Int,
            val configuration: Configuration
    )

    data class MobileDeviceConfiguration(
            val schema: Int,
            val configuration: Configuration
    )

    companion object {
        const val USER_ID = "user-id"
        const val NODE_ID = "node-id"
        const val NODE_KEY = "node-key"
        const val SCHEMA_VERSION = "schema-version"
    }

    @GET
    @Path("/user/{${USER_ID}}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get configuration of user", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getUserConfiguration(
            @PathParam(value = USER_ID) userId: Int
    ): String

    @GET
    @Path("/device")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get configuration given node", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getNodeConfiguration(
            @QueryParam(value = NODE_KEY) nodeKey: String
    ): String
}