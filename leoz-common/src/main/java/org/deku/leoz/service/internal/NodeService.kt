package org.deku.leoz.service.internal

import io.swagger.annotations.*
import sx.rs.auth.ApiKey
import javax.ws.rs.*
import javax.ws.rs.core.*

/**
 * Created by JT on 05.02.16.
 */
@Path("internal/v1/node")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(value = "Node operations")
@ApiKey(false)
interface NodeService {

    @GET
    @Path("/entity-sync")
    @ApiOperation(value = "Trigger entity sync")
    fun sync(
            @QueryParam("clean")
            @ApiParam(defaultValue = "false", value = "Perform clean sync (drop data prior to entity requst)")
            clean: Boolean)
}