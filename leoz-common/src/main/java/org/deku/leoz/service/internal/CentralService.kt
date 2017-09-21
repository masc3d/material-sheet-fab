package org.deku.leoz.service.internal

import javax.ws.rs.*
import javax.ws.rs.core.*
import io.swagger.annotations.*
import sx.rs.auth.ApiKey

/**
 * Created by JT on 05.02.16.
 */
@Path("internal/v1/central")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Central operations")
@ApiKey(false)
interface CentralService {

    @GET
    @Path("/sync")
    @ApiOperation(value = "Trigger central database sync")
    fun sync(@QueryParam("clean") @ApiParam(defaultValue = "false", value = "Perform clean sync (drop existing data)") clean: Boolean)
}