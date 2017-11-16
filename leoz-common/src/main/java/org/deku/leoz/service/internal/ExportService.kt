package org.deku.leoz.service.internal


import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.service.entity.ServiceError
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
        const val REDSEAL = "redseal"
        const val TEXT = "text"
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

    @GET
    @Path("/bag/{$BAG_ID}")
    @ApiOperation(value = "Get parcels in Bag", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getParcelsFilledInBagByBagID(
            @PathParam(BAG_ID) @ApiParam(value = "Bag ID", example = "700100000008", required = true) bagId: Long
    ):List<ParcelServiceV1.Order2Export>

    @GET
    @Path("/loadinglist/{$LOADINGLIST_NO}")
    @ApiOperation(value = "Get parcels by loadinglist", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getParcels2ExportByLoadingList(
            @PathParam(LOADINGLIST_NO) @ApiParam(value = "Loadinglist number", example = "300005", required = true) loadinglistNo: Long
    ): List<ParcelServiceV1.Order2Export>

    @POST
    @Path("/loadinglist")
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

    @GET
    @Path("/station/{$STATION_NO}/send-back")
    @ApiOperation("Count bags to send back", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun getCount2SendBackByStation(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int

    ): Int

    @PATCH
    @Path("/bag/{$BAG_ID}/setRedSeal")
    @ApiOperation(value = "Set red seal", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun setBagStationExportRedSeal(
            @ApiParam(value = "Bag-ID", example = "700100000008") @PathParam(BAG_ID) bagID: Long,
            @QueryParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @QueryParam(REDSEAL) @ApiParam(value = "Red seal number", example = "900200000001", required = true) redSeal: Long,
            @QueryParam(TEXT) @ApiParam(value = "Text", required = true) text: String
    )

    @PATCH
    @Path("/bag/{$BAG_ID}/reopen")
    @ApiOperation(value = "Reopen bag", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun reopenBagStationExport(
            @ApiParam(value = "Bag-ID", example = "700100000008") @PathParam(BAG_ID) bagID: Long,
            @QueryParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int

    )

    @PATCH
    @Path("/bag/{$BAG_ID}/fill")
    @ApiOperation(value = "Fill bag in station-export", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun fillBagStationExport(
            @ApiParam(value = "Bag-ID", example = "700100000008") @PathParam(BAG_ID) bagID: Long,
            @QueryParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @QueryParam(SCANCODE) @ApiParam(value = "unit", example = "123456789877", required = true) unitNo: String?
    )

    @PATCH
    @Path("/bag/{$BAG_ID}/close")
    @ApiOperation(value = "Close bag in station-export", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun closeBagStationExport(
            @ApiParam(value = "Bag-ID", example = "700100000008") @PathParam(BAG_ID) bagID: Long,
            @QueryParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int
    )

    @POST
    @Path("/bag/loadinglist")
    @ApiOperation(value = "Create new loadinglist for bag", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getNewBagLoadinglistNo(): LoadinglistService.Loadinglist

}