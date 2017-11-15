package org.deku.leoz.service.internal


import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import sx.io.serialization.Serializable
import sx.rs.PATCH
import sx.rs.auth.ApiKey
import java.util.*


@Path("internal/v1/export")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Export service")
@ApiKey(false)
interface ExportService {

    companion object {
        const val EVENT = 1
        const val STATION_NO = "station-no"
        const val LOADINGLIST_NO = "loadinglist-no"
        const val SCANCODE = "parcel-no"
        const val BAG_ID = "bag-id"
    }

    @GET
    @Path("/station/{$STATION_NO}")
    @ApiOperation(value = "Get parcels to export", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getParcels2ExportByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int
    ): List<ParcelServiceV1.Order2Export>

    @GET
    @Path("/station/{$STATION_NO}/bag")
    @ApiOperation(value = "Get parcels to export in Bag", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getParcels2ExportInBagByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int
    ): List<ParcelServiceV1.Order2Export>

    @GET
    @Path("/station/{$STATION_NO}/loaded")
    @ApiOperation(value = "Get loaded parcels to export", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getLoadedParcels2ExportByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int
    ): List<ParcelServiceV1.Order2Export>

    @POST
    @Path("/")
    @ApiOperation(value = "Create new loadinglist", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getNewLoadinglistNo(): LoadinglistService.Loadinglist

    @PATCH
    @Path("/")
    @ApiOperation(value = "Export parcel", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun export(
            @QueryParam(SCANCODE) @ApiParam(value = "Parcel number or creference", required = true) scanCode: String = "",
            @QueryParam(LOADINGLIST_NO) @ApiParam(value = "Loadinglist number", required = true) loadingListNo: Long,
            @QueryParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int
    ): Boolean

}