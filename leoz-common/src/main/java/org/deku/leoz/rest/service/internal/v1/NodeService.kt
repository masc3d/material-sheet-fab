package org.deku.leoz.rest.service.internal.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 05.02.16.
 */
@Path("internal/v1/node")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Node operations")
interface NodeService {

    @GET
    @Path("/entity-sync")
    @ApiOperation(value = "Trigger entity sync")
    fun entitySync()
}