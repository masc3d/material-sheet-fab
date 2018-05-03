package org.deku.leoz.service.internal


import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.model.DekuUnitNumber
import org.deku.leoz.model.LoadinglistType
import org.deku.leoz.rest.RestrictStation
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
@Api(value = "Export service", authorizations = arrayOf(Authorization(Rest.API_KEY)))
@ApiKey
interface ExportService {

    companion object {
        const val BAG_BACK_NO = "bagback-no"
        const val STATION_NO = "station-no"
        const val LOADINGLIST_NO = "loadinglist-no"
        const val SCANCODE = "scancode"
        const val BAG_ID = "bag-id"
        const val REDSEAL = "redseal"
        const val TEXT = "text"
        const val SEND_DATE = "send-date"
        const val YELLOWSEAL = "yellowseal"
        const val ID = "loading-list"
    }

    @ApiModel(value = "Exportorder", description = "Exportorder Model")
    @Serializable(0x5abfa519181a30)
    data class Order(
            @get:ApiModelProperty(value = "OrderId")
            var orderId: Long = 0,
            @ApiModelProperty(value = "Delivery address")
            var deliveryAddress: Address = Address(),
            @get:ApiModelProperty(value = "Delivery Station")
            var deliveryStation: Int = 0,
            @get:ApiModelProperty(value = "Shipment date")
            var shipmentDate: java.sql.Date? = null,
            @get:ApiModelProperty(value = "Parcels")
            var parcels: List<Parcel> = listOf()

    )

    @ApiModel(value = "Exportparcel", description = "Parcel within Order")
    @Serializable(0xbb30fca9069776)
    data class Parcel(
            @get:ApiModelProperty(value = "OrderId")
            var orderId: Long = 0,
            @get:ApiModelProperty(value = "Parcel no")
            var parcelNo: Long = 0,
            @get:ApiModelProperty(value = "Parcel position")
            var parcelPosition: Int = 0,
            @get:ApiModelProperty(value = "Loadinglist no")
            var loadinglistNo: Long? = null,
            @get:ApiModelProperty(value = "Type of packaging")
            var typeOfPackaging: Int = 0,
            @get:ApiModelProperty(value = "Real weight")
            var realWeight: Double = 0.0,
            @get:ApiModelProperty(value = "Date of station out")
            var dateOfStationOut: java.sql.Date? = null,
            @get:ApiModelProperty(value = "Creferenz")
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
        var loadinglistNo: Long? = null
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

    enum class ResponseMsg(val value: String) {
        LL_WRONG_CHECKDIGIT("Loadinglist - wrong check digit"),
        LL_NOT_VALID("Loadinglist not valid"),
        LL_CHANGED("Loadinglist changed"),
        LL_WRONG_TYPE("Loadinglist not of Bag-Type"),
        LL_USED_FOR_MULTIPLE_BAGS("Loadinglist used for multiple bags"),
        LL_ALREADY_USED_FOR_ANOTHER_BAG("Loadinglist already used for another bag"),
        VAL_NOT_ALLOWED("Valuables not allowed"),
        VAL_NOT_ALLOWED_WITHOUT_BAG("Valuables not allowed without bag"),
        PARCEL_ALREADY_SCANNED("Parcel already scanned"),
        PARCEL_NOT_FOUND("Parcel not found"),
        PARCEL_DELIVERED("Parcel delivered"),
        PARCEL_DELETED("Parcel deleted"),
        MORE_PARCELS_FOR_CREFERENCE("More parcels found to this cReference"),
        ORDER_NOT_FOUND("Order not found"),
        NO_ONS("No ONS"),
        STATION_DISMATCH("Station dismatch"),//order depotnabd!=übergebene Station
        NO_ORDERS_FOUND("No orders found"),
        NO_PARCELS_FOUND("No parcels found"),
        NO_PARCELS_FOUND_FOR_STATION("No parcels found for this station"),
        NO_PARCELS_FOUND_FOR_LL("No parcels found for this list"),
        BAG_ID_WRONG_CHECKDIGIT("BagId wrong check digit"),
        BAG_ID_NOT_VALID("BagId not valid"),
        BAG_ID_NOT_FOUND("BagId not found"),
        BAG_ID_NULL("BagId null"),
        BAG_ID_WITHOUT_LAST_STATION("BagId without lastStation"),
        BAG_ID_NOT_IN_MOVE("BagId not found in move-state"),
        BAG_ID_OK_NO_BACK("BagId found - no bagback-unit found"),
        BAG_ID_OK_NO_BACK_ORDER("BagId found - no bagback-order found"),
        BAG_ID_OK_BACK_ORDER_WITHOUT_ABD("BagId found - bagback-order without depotnrabd"),
        BAG_ID_OK_BACK_ORDER_ABD_MISMATCH("BagId found - bagback-order station mismatch depotnrabd"),
        BAG_ID_OK_ALREADY_EXPORTED("BagId found - bag already exported"),
        BAG_ID_OK_ALREADY_CLOSED("BagId found - bag already closed - try to reopen"),
        BAG_ID_OK_ALREADY_OPEN("BagId found - already open"),
        BAG_UNIT_WRONG_CHECKDIGIT("Bag-UnitNo wrong check digit"),
        BAG_UNIT_NOT_VALID("Bag-UnitNo not valid"),
        BAG_BACK_UNIT_DISMATCH("Bag-BackUnitNo dismatch"),
        BAG_BACK_UNIT_USED_FOR_MULTIPLE_LL("BagBackUnitNo used for multiple loadinglists"),
        BAG_BACK_UNIT_ALREADY_USED_FOR_ANOTHER_LL("BagBackUnitNo already used for another loadinglist"),
        RED_SEAL_WRONG_CHECKDIGIT("Red seal - wrong check digit"),
        RED_SEAL_NOT_VALID("Red seal not valid"),
        RED_SEAL_NOT_FOUND("Red seal not found"),
        NO_STATION("No station"),
        LAST_DEPOT_STATION_MISMATCH("LastDepot-Station mismatch"),
        SEAL_STATUS_PROBLEM("Sealstatus-problem"),
        SEAL_ALREADY_IN_USE("SealNo already in use"),
        SEAL_MISMATCH("Seal number mismatch"),
        YELLOW_SEAL_WRONG_CHECKDIGIT("Yellow seal number - wrong check digit"),
        YELLOW_SEAL_NOT_VALID("Yellow seal number not valid"),
        NO_SEAL_IN_BAG("No seal number in bag"),
        WEIGHT_GREATER_THAN_MAX("Weight > max"),
        SEND_DATE_INVALID("Invalid senddate"),
        BAG_NOT_CLOSED("Bag not closed - close bag before export")
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
            @PathParam(BAG_ID) @ApiParam(value = "Bag ID", example = "700100000008", required = true) bagID: String,
            @QueryParam("includeParcels") @ApiParam(defaultValue = "false", value = "Include parcels") includeParcels: Boolean=false
    ): Bag

    @GET
    @Path("/station/{$STATION_NO}/loaded/order")
    @ApiOperation(value = "Get loaded parcels to export", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getLoadedParcelsToExportByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @QueryParam(SEND_DATE) @ApiParam(value = "Send date", example = "08/09/2017", required = false) sendDate: Date? = null
    ): List<Order>

    @GET
    @Path("/station/{$STATION_NO}/loaded/bag/order")
    @ApiOperation(value = "Get loaded parcels to export in bag", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getLoadedParcelsToExportInBagByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @QueryParam(SEND_DATE) @ApiParam(value = "Send date", example = "08/09/2017", required = false) sendDate: Date? = null
    ): List<Order>

    @GET
    @Path("/loadinglist")
    @ApiOperation(value = "Get list of all loadinglist", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getAllLoadingList(
            @QueryParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @QueryParam(SEND_DATE) @ApiParam(value = "Send date", example = "08/09/2017", required = false) sendDate: Date? = null,
            @QueryParam("includeParcels") @ApiParam(defaultValue = "false", value = "Include parcels") includeParcels: Boolean
    ): List<Loadinglist>

    @GET
    @Path("/loadinglist/{$LOADINGLIST_NO}")
    @ApiOperation(value = "Get loadinglist", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getLoadingList(
            @PathParam(LOADINGLIST_NO) @ApiParam(value = "Loadinglist number", example = "300005", required = true) loadinglistNo: String
    ): Loadinglist

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
            @QueryParam(SCANCODE)
            @ApiParam(value = "Parcel number or creference", required = true)
            scanCode: String = "",

            @QueryParam(LOADINGLIST_NO)
            @ApiParam(value = "Loadinglist number", required = true)
            loadingListNo: String,

            @QueryParam(STATION_NO)
            @ApiParam(value = "Station number", example = "220", required = true)
            @RestrictStation
            stationNo: Int
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
    @Path("/bag/{$BAG_ID}/set-red-seal")
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