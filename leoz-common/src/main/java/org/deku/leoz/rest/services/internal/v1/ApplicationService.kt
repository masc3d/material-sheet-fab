package org.deku.leoz.rest.services.internal.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.deku.leoz.rest.entities.internal.v1.ApplicationVersion
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by masc on 09.10.15.
 */
@Path("internal/v1/application")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Application operations")
interface ApplicationService {
    /**
     * Get entry by name
     * @param name name
     */
    @GET
    @Path("/version")
    @ApiOperation(value = "Get application version", response = String::class)
    fun getVersion(): ApplicationVersion

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
}