package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.service.internal.entity.Station
import org.deku.leoz.service.internal.entity.StationV2
import sx.rs.auth.ApiKey
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by masc on 17.09.14.
 */
@Path("internal/v1/station")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Station operations", authorizations = arrayOf(Authorization(Rest.API_KEY)))
@ApiKey
interface StationService {

    companion object {
        const val STATION_NO = "station-no"
        const val DEBITOR_ID = "debitor-id"
    }

    @GET
    @Path("/")
    @ApiOperation(value = "Get all stations", notes = "Some notes", response = Station::class)
    fun get(): Array<Station>

    @GET
    @Path("/find")
    @ApiOperation(value = "Query stations by simple substring match applied to relevant fields")
    @ApiResponses(value = [(ApiResponse(code = 404, message = "No stations found"))])
    fun find(
            @ApiParam(value = "Query string") @QueryParam("q") query: String): Array<Station>

    @GET
    @Path("/{$STATION_NO}")
    @ApiOperation(value = "get station", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "station number", example = "220", required = true) stationNo: Int
    ): StationV2


    @GET
    @Path("/debitor/{$DEBITOR_ID}")
    @ApiOperation(value = "get stations by debitor-id")
    fun getByDebitorId(
            @PathParam(DEBITOR_ID) @ApiParam(value = "debitor id", example = "3", required = true) debitorId: Int
    ): Array<StationV2>
}
