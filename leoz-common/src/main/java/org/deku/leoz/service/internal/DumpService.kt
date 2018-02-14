package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import sx.rs.auth.ApiKey
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.StreamingOutput

/**
 * Dump service
 * Created by masc on 14.02.18.
 */
@Path("internal/v1/dump")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_PLAIN)
@Api(value = "Dump operations")
@ApiKey(false)
interface DumpService {
    companion object {
        val STATION_NO = "station-no"
    }

    /**
     * Dump stations
     */
    @GET
    @Path("/central/station")
    @ApiOperation(value = "Dump central stations")
    fun dumpCentralStations(): StreamingOutput

    /**
     * Dump routes
     */
    @GET
    @Path("/central/route")
    @ApiOperation(value = "Dump central routes")
    fun dumpCentralRoutes(): StreamingOutput
}
