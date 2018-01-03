package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.Authorization
import org.deku.leoz.config.Rest
import sx.rs.auth.ApiKey
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Application specific functions
 * Created by masc on 09.10.15.
 */
@Path("internal/v1/application")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Application operations")
@ApiKey
interface ApplicationService {
    /**
     * Created by masc on 09.10.15.
     */
    data class Version(
            val name: String = "",
            val version: String = "")

    /**
     * Get entry by name
     * @param name name
     */
    @GET
    @Path("/version")
    @ApiOperation(value = "Get application version")
    @ApiKey(false)
    fun getVersion(): Version

    @GET
    @Path("/restart")
    @ApiOperation(value = "Restart application", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun restart()

    @GET
    @Path("/bundle-update")
    @ApiOperation(value = "Trigger application bundle udpates", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun bundleUpdate()

    @GET
    @Path("/notify-bundle-update")
    @ApiOperation(value = "Notify remote nodes about bundle update", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun notifyBundleUpdate(@ApiParam(value = "Bundle name") @QueryParam("bundle-name") bundleName: String)

    @GET
    @Path("/sync-with-remote-node")
    @ApiOperation(value = "Trigger entity sync", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun syncWithRemoteNode(
            @QueryParam("clean")
            @ApiParam(defaultValue = "false", value = "Perform clean sync (drop data prior to entity requst)")
            clean: Boolean)

    @GET
    @Path("/sync-with-central-database")
    @ApiOperation(value = "Trigger central database sync", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun syncWithCentralDatabase(@QueryParam("clean") @ApiParam(defaultValue = "false", value = "Perform clean sync (drop existing data)") clean: Boolean)
}
