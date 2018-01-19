package org.deku.leoz.service.internal


import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.model.DekuUnitNumber
import org.deku.leoz.model.LoadinglistType
import org.deku.leoz.service.entity.ServiceError
import org.deku.leoz.service.internal.entity.Address
import sx.io.serialization.Serializable
import sx.rs.auth.ApiKey
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType


@Path("internal/v1/export")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Export service")
@ApiKey(false)
interface ExportService {

    companion object {
        const val BAG_BACK_NO = "bagback-no"
        const val STATION_NO = "station-no"
        const val LOADINGLIST_NO = "loadinglist-no"
        const val SCANCODE = "parcel-no-or-reference"
        const val BAG_ID = "bag-id"
        const val REDSEAL = "redseal"
        const val TEXT = "text"
        const val SEND_DATE = "send-date"
        const val YELLOWSEAL = "yellowseal"
        const val ID = "loading-list"
    }

    @Serializable(0x5abfa519181a30)
    data class Order(
            var orderId: Long = 0,
            var deliveryAddress: Address = Address(),
            var deliveryStation: Int = 0,
            var shipmentDate: java.sql.Date? = null,
            var parcels: List<Parcel> = listOf()

    )

    @Serializable(0xbb30fca9069776)
    data class Parcel(
            var orderId: Long = 0,
            var parcelNo: Long = 0,
            var parcelPosition: Int = 0,
            var loadinglistNo: Long? = null,
            var typeOfPackaging: Int = 0,
            var realWeight: Double = 0.0,
            var dateOfStationOut: java.sql.Date? = null,
            var cReference: String? = null
    )

    @ApiModel(description = "Bag Model")
    @Serializable(0x32028b91dda15f)
    data class Bag(
            val bagNumber: Long? = null,
            val sealNumberGreen: Long? = null,
            //var status: Int? = null,
            val status: org.deku.leoz.model.BagStatus? = null,
            val statusTimestamp: Date? = null,
            val lastStation: Int? = null,
            val sealNumberYellow: Long? = null,
            val sealNumberRed: Long? = null,
            val orderhubTodepot: Long? = null,
            val orderdepotTohub: Long? = null,
            val initStatus: Int = 0,
            val workdate: Date? = null,
            val printed: Int? = null,
            val multibag: Int = 0,
            val movepool: String? = null
    ) {
        var unitNo: Long? = null
        var unitNoBack: Long? = null
        var ordersToexport: List<Order> = listOf()
        var bagNumberLabel: String? = null
        var unitNoLabel: String? = null
        var unitBackLabel: String? = null
        var sealYellowLabel: String? = null
        var sealRedLabel: String? = null
        var sealGreenLabel: String? = null
    }

    @Serializable(0x2e5b98b7a7694f)
    data class Loadinglist(val loadinglistNo: Long, val orders: List<ExportService.Order> = listOf()) {
        constructor(loadinglistlabel: String, orders: List<ExportService.Order>) : this(DekuUnitNumber.parseLabel(loadinglistlabel).value.value.toLong(), orders) {}

        val loadinglistType by lazy {
            if (this.loadinglistNo < 100000)
                LoadinglistType.BAG
            else LoadinglistType.NORMAL
        }
        val label by lazy {
            DekuUnitNumber.parse(this.loadinglistNo.toString().padStart(11, '0')).value.label
        }
    }

    enum class ResponseMsg(val value:String){
        LlWrongCheckDigit("Loadinglist - wrong check digit"),
        LlNotValid("Loadinglist not valid"),
        LlChanged("Loadinglist changed"),
        LlWrongType("Loadinglist not of Bag-Type"),
        LlUsedForMultipleBags("Loadinglist used for multiple bags"),
        LlAlreadyUsedForAnotherBag("Loadinglist already used for another bag"),
        ValNotAllowed("Valuables not allowed"),
        ValNotAllowedWithoutBag("Valuables not allowed without bag"),
        ParcelAlreadyScanned("Parcel already scanned"),
        ParcelNotFound("Parcel not found"),
        ParcelDelivered("Parcel delivered"),
        ParcelDeleted("Parcel deleted"),
        MoreParcelsForCreference("More parcels found to this cReference"),
        OrderNotFound("Order not found"),
        NoONS("No ONS"),
        StationDismatch("Station dismatch"),//order depotnabd!=übergebene Station
        NoOrdersFound("No orders found"),
        NoParcelsFound("No parcels found"),
        NoParcelsFoundForStation("No parcels found for this station"),
        NoParcelsFoundForLL("No parcels found for this list"),
        BagIdWrongCheckDigit("BagId wrong check digit"),
        BagIdNotValid("BagId not valid"),
        BagIdNotFound("BagId not found"),
        BagIdNull("BagId null"),
        BagIdWithoutLastStation("BagId without lastStation"),
        BagIdNotInMove("BagId not found in move-state"),
        BagIdOkNoBack("BagId found - no bagback-unit found"),
        BagIdOkNoBackOrder("BagId found - no bagback-order found"),
        BagIdOkBackOrderWithoutAbd("BagId found - bagback-order without depotnrabd"),
        BagIdOkBackOrderAbdMismatch("BagId found - bagback-order station mismatch depotnrabd"),
        BagIdOkAlreadyExported("BagId found - bag already exported"),
        BagIdOkAlreadyClosed("BagId found - bag already closed - try to reopen"),
        BagIdOkAlreadyOpen("BagId found - already open"),
        BagUnitWrongCheckDigit("Bag-UnitNo wrong check digit"),
        BagUnitNotValid("Bag-UnitNo not valid"),
        BagBackUnitDismatch("Bag-BackUnitNo dismatch"),
        BagBackUnitUsedForMultipleLl("BagBackUnitNo used for multiple loadinglists"),
        BagBackUnitAlreadyUsedForAnotherLl("BagBackUnitNo already used for another loadinglist"),
        RedSealWrongDigit("Red seal - wrong check digit"),
        RedSealNotValid("Red seal not valid"),
        RedSealNotFound("Red seal not found"),
        NoStation("No station"),
        LastDepotStationMismatch("LastDepot-Station mismatch"),
        SealStatusProblem("Sealstatus-problem"),
        SealAlreadyInUse("SealNo already in use"),
        SealMismatch("Seal number mismatch"),
        YellowSealWrongCheckDigit("Yellow seal number - wrong check digit"),
        YellowSealNotValid("Yellow seal number not valid"),
        NoSealInBag("No seal number in bag"),
        WeightGreaterThanMax("Weight > max"),
        SendDateInvalid("Invalid senddate")
    }

    @GET
    @Path("/station/{$STATION_NO}/order")
    @ApiOperation(value = "Get parcels to export", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getParcelsToExportByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @QueryParam(SEND_DATE) @ApiParam(value = "Send date", example = "08/09/2017", required = false) sendDate: Date? = null
    ): List<Order>

    @GET
    @Path("/station/{$STATION_NO}/bag/order")
    @ApiOperation(value = "Get parcels to export in Bag", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getParcelsToExportInBagByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @QueryParam(SEND_DATE) @ApiParam(value = "Send date", example = "08/09/2017", required = false) sendDate: Date? = null
    ): List<Order>

    @GET
    @Path("/station/{$STATION_NO}/bag/{$BAG_ID}")
    @ApiOperation(value = "Get bag by bagId", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getBag(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @PathParam(BAG_ID) @ApiParam(value = "Bag ID", example = "700100000008", required = true) bagID: String
    ): Bag

    @GET
    @Path("/station/{$STATION_NO}/loaded/order")
    @ApiOperation(value = "Get loaded parcels to export", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getLoadedParcelsToExportByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @QueryParam(SEND_DATE) @ApiParam(value = "Send date", example = "08/09/2017", required = false) sendDate: Date? = null
    ): List<Order>

    @GET
    @Path("/loadinglist/{$LOADINGLIST_NO}/order")
    @ApiOperation(value = "Get parcels by loadinglist", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getParcelsToExportByLoadingList(
            @PathParam(LOADINGLIST_NO) @ApiParam(value = "Loadinglist number", example = "300005", required = true) loadinglistNo: String
    ): List<Order>

    @POST
    @Path("/loadinglist")
    @ApiOperation(value = "Create new loadinglist", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getNewLoadinglistNo(): ExportService.Loadinglist

    @PATCH
    @Path("/")
    @ApiOperation(value = "Export parcel", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun export(
            @QueryParam(SCANCODE) @ApiParam(value = "Parcel number or creference", required = true) scanCode: String = "",
            @QueryParam(LOADINGLIST_NO) @ApiParam(value = "Loadinglist number", required = true) loadingListNo: String,
            @QueryParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int
    ): String

    @GET
    @Path("/station/{$STATION_NO}/send-back-count")
    @ApiOperation("Count bags to send back", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun getCountToSendBackByStation(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int

    ): Int

    @PATCH
    @Path("/bag/{$BAG_ID}/setRedSeal")
    @ApiOperation(value = "Set red seal", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun setBagStationExportRedSeal(
            @ApiParam(value = "Bag-ID", example = "700100000008") @PathParam(BAG_ID) bagID: String,
            @QueryParam(BAG_BACK_NO) @ApiParam(value = "BagBackunit number", example = "100720000004", required = true) bagBackUnitNo: String,
            @QueryParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @QueryParam(REDSEAL) @ApiParam(value = "Red seal number", example = "900200000001", required = true) redSeal: String,
            @QueryParam(TEXT) @ApiParam(value = "Text", required = true) text: String
    )

    @PATCH
    @Path("/bag/{$BAG_ID}/reopen")
    @ApiOperation(value = "Reopen bag", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun reopenBagStationExport(
            @ApiParam(value = "Bag-ID", example = "700100000008") @PathParam(BAG_ID) bagID: String,
            @QueryParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int

    )

    @PATCH
    @Path("/bag/{$BAG_ID}/fill")
    @ApiOperation(value = "Fill bag in station-export", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun fillBagStationExport(
            @ApiParam(value = "Bag-ID", example = "700100000008") @PathParam(BAG_ID) bagID: String,
            @QueryParam(BAG_BACK_NO) @ApiParam(value = "BagBackunit number", example = "100720000004", required = true) bagBackUnitNo: String,
            @QueryParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @QueryParam(SCANCODE) @ApiParam(value = "Unit number or cReference", example = "123456789877", required = true) unitNo: String,
            @QueryParam(LOADINGLIST_NO) @ApiParam(value = "Bag loadinglist number", required = true) loadingListNo: String,
            @QueryParam(YELLOWSEAL) @ApiParam(value = "Yellow seal", example = "900200000001", required = true) yellowSealNo: String
    ): String

    @PATCH
    @Path("/bag/{$BAG_ID}/close")
    @ApiOperation(value = "Close bag in station-export", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun closeBagStationExport(
            @ApiParam(value = "Bag-ID", example = "700100000008") @PathParam(BAG_ID) bagID: String,
            @QueryParam(BAG_BACK_NO) @ApiParam(value = "BagBackunit number", example = "100720000004", required = true) bagBackUnitNo: String,
            @QueryParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @QueryParam(LOADINGLIST_NO) @ApiParam(value = "Bag loadinglist number", required = true) loadingListNo: String
    )

    @POST
    @Path("/bag/loadinglist")
    @ApiOperation(value = "Create new loadinglist for bag", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getNewBagLoadinglistNo(): ExportService.Loadinglist


}