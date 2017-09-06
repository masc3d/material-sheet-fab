package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiParam
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("internal/v1/configuration")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Configuration service")
interface ConfigurationServiceV1 {

    data class Configuration(
            val scope: Scope,
            val settings: Map<String, String>
    ) {
        enum class Scope {
            WEB,
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
        const val DEVICE_ID = "device-id"
        const val SCHEMA_VERSION = "schema-version"
    }

    @GET
    @Path("/user/{$USER_ID}")
    fun getUserConfiguration(
            @PathParam(value = USER_ID) @ApiParam(value = "The requested users ID", required = true) userId: Int,
            @QueryParam(value = SCHEMA_VERSION) @ApiParam(value = "The schema version of the configuration") schemaVersion: Int,
            @ApiParam(value = "Scope of the configuration") scope: Configuration.Scope
    ): UserConfiguration?

    @GET
    @Path("/device/{$DEVICE_ID}")
    fun getMobileDeviceConfiguration(
            @PathParam(value = DEVICE_ID) deviceId: String
    ): MobileDeviceConfiguration?
}