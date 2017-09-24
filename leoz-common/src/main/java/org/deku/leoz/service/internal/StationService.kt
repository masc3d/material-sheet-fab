package org.deku.leoz.service.internal

import javax.ws.rs.*
import io.swagger.annotations.*
import org.deku.leoz.service.internal.entity.Station
import sx.rs.auth.ApiKey
import javax.ws.rs.core.MediaType

/**
 * Created by masc on 17.09.14.
 */
@Path("internal/v1/station")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Station operations")
@ApiKey(false)
interface StationService {
    @GET
    @Path("/")
    @ApiOperation(value = "Get all stations", notes = "Some notes", response = Station::class)
    fun get(): Array<Station>

    @GET
    @Path("/find")
    @ApiOperation(value = "Query stations by simple substring match applied to relevant fields")
    @ApiResponses(value = *arrayOf(
            ApiResponse(code = 404, message = "No stations found")
    ))
    fun find(
            @ApiParam(value = "Query string") @QueryParam("q") query: String): Array<Station>
}
