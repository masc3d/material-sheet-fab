package org.deku.leoz.service.internal

import javax.ws.rs.*
import io.swagger.annotations.*
import org.deku.leoz.service.internal.entity.Station
import javax.ws.rs.core.MediaType

/**
 * Created by masc on 17.09.14.
 */
@Path("internal/v1/depot")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Depot operations")
interface StationService {
    @GET
    @Path("/")
    @ApiOperation(value = "Get all depots", notes = "Some notes", response = Station::class)
    fun get(): Array<Station>

    @GET
    @Path("/find")
    @ApiOperation(value = "Query depots by simple substring match applied to relevant fields")
    @ApiResponses(value = *arrayOf(
            ApiResponse(code = 404, message = "No depots found")
    ))
    fun find(
            @ApiParam(value = "Query string") @QueryParam("q") query: String): Array<Station>
}
