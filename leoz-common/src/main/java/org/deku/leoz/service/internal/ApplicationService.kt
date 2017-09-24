package org.deku.leoz.service.internal

import javax.ws.rs.*
import javax.ws.rs.core.*
import io.swagger.annotations.*
import sx.rs.auth.ApiKey

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
    @ApiOperation(value = "Restart application")
    fun restart()

    @GET
    @Path("/bundle-update")
    @ApiOperation(value = "Trigger application bundle udpates")
    fun bundleUpdate()

    @GET
    @Path("/notify-bundle-update")
    @ApiOperation(value = "Notify remote nodes about bundle update")
    fun notifyBundleUpdate(@ApiParam(value = "Bundle name") @QueryParam("bundle-name") bundleName: String)

    @GET
    @Path("/sync-with-remote-node")
    @ApiOperation(value = "Trigger entity sync")
    fun syncWithRemoteNode(
            @QueryParam("clean")
            @ApiParam(defaultValue = "false", value = "Perform clean sync (drop data prior to entity requst)")
            clean: Boolean)

    @GET
    @Path("/sync-with-central-database")
    @ApiOperation(value = "Trigger central database sync")
    fun syncWithCentralDatabase(@QueryParam("clean") @ApiParam(defaultValue = "false", value = "Perform clean sync (drop existing data)") clean: Boolean)
}
