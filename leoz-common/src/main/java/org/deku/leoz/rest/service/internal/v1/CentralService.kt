package org.deku.leoz.rest.service.internal.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 05.02.16.
 */
@Path("internal/v1/central")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Central operations")
interface CentralService {

    @GET
    @Path("/sync")
    @ApiOperation(value = "Trigger central database sync")
    fun sync(@QueryParam("clean") @ApiParam(defaultValue = "false", value = "Perform clean sync (drop existing data)") clean: Boolean)
}