package org.deku.leoz.service.internal

import javax.ws.rs.*
import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.service.internal.entity.Station
import org.deku.leoz.service.internal.entity.StationV2
import sx.rs.auth.ApiKey
import javax.ws.rs.core.MediaType

/**
 * Created by masc on 17.09.14.
 */
@Path("internal/v1/station")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Station operations")
interface StationService {

    companion object {
        const val STATION_NO="station number"
    }


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

    @GET
    @Path("/find/{$STATION_NO}")
    @ApiOperation(value = "get station")
    fun findByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "station number",example = "220",required = true) stationNo: Int
    ): StationV2

    @GET
    @Path("/find/station")
    @ApiOperation(value = "get station",response = StationV2::class)
    fun findByStation(
            @QueryParam("station") @ApiParam(value = "station number",example = "220",required = true) stationNo: Int
    ): StationV2

    //@GET
    //@Path("/find/stations")
    //@ApiOperation(value = "get stations",response = StationV2::class)
    //fun findByDebitorId(
    //        @QueryParam("debitorid") @ApiParam(value = "debitor id",example = "3",required = true) debitorId: Int
    //): Array<StationV2>
}
