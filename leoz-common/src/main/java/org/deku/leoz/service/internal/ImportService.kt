package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.model.DekuUnitNumber
import org.deku.leoz.service.entity.ServiceError
import org.deku.leoz.service.internal.entity.Address
import sx.io.serialization.Serializable
import sx.rs.auth.ApiKey
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("internal/v1/import")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Import service", authorizations = arrayOf(Authorization(Rest.API_KEY)))
@ApiKey
interface ImportService {

    companion object {
        const val STATION_NO = "station-no"
        const val DELIVERY_DATE = "delivery-date"
        const val SCANCODE = "scancode"
    }

    @ApiModel(value = "Importorder", description = "Importorder Model")
    @Serializable(0x3472fac6b461d8)
    data class Order(
            var orderId: Long = 0,
            var deliveryAddress: Address = Address(),
//            var deliveryZip: String = "",//nicht die ganze Adresse - soll minimalisitisch gehalten werden
//            var deliveryCity: String = "",
            var deliveryStation: Int = 0,
            var deliveryDate: java.sql.Date? = null,
            var sealNumber: Long? = null,
            var parcels: List<Parcel> = listOf()

    )

    @ApiModel(description = "Parcel Model", value = "Importparcel")
    @Serializable(0xc70cdcb9c5731f)
    data class Parcel(
            @get:ApiModelProperty(example = "1234567898", required = true, value = "Order id")
            var orderId: Long = 0,
            @get:ApiModelProperty(example = "9876543212", required = true, value = "Parcel number")
            var parcelNo: Long = 0,
            @get:ApiModelProperty(example = "10586136", required = false, value = "Cartage note")
            var cartageNote: Long? = null,
            @get:ApiModelProperty(example = "1.5", required = false, value = "Weight")
            var realWeight: Double? = null,
            @get:ApiModelProperty(example = "4.4", required = false, value = "Volume weight")
            var volWeight: Double? = null,
            @get:ApiModelProperty(example = "20", required = false, value = "Length")
            var length: Int? = null,
            @get:ApiModelProperty(example = "30", required = false, value = "Width")
            var width: Int? = null,
            @get:ApiModelProperty(example = "40", required = false, value = "Height")
            var height: Int? = null,
            @get:ApiModelProperty(example = "08/09/2017", required = false, value = "Date of station in")
            var dateOfStationIn: java.sql.Date? = null,
            @get:ApiModelProperty(example = "abcdef", required = false, value = "Reference")
            var cReference: String? = null,
            @get:ApiModelProperty(example = "541", required = false, value = "Tour")
            var tourNo: Int? = null,
            @get:ApiModelProperty(example = "true", required = false, value = "Is missing")
            var isMissing: Boolean? = null,
            @get:ApiModelProperty(example = "true", required = false, value = "Is damaged")
            var isDamaged: Boolean? = null,
            @get:ApiModelProperty(example = "true", required = false, value = "Is wrong station")
            var isWrongLoaded: Boolean? = null,
            @get:ApiModelProperty(example = "true", required = false, value = "Is wrong routed")
            var isWrongRouted: Boolean? = null,
            @get:ApiModelProperty(example = "null", required = false, value = "Station export scan")
            var dateOfStationOut: Date? = null,
            @get:ApiModelProperty(example = "null", required = false, value = "Hub export scan")
            var dateOfHubOut: Date? = null
    )

    enum class ResponseMsg(val value: String) {
        PARCEL_ALREADY_SCANNED("Parcel already scanned"),
        PARCEL_NOT_FOUND("Parcel not found"),
        NO_PARCELS_FOUND("No parcels found"),
        MORE_PARCELS_FOR_CREFERENCE("More parcels found to this cReference"),
        ORDER_NOT_FOUND("Order not found"),
        NO_ORDERS_FOUND("No orders found")
    }

    @GET
    @Path("/station/{$STATION_NO}/order")
    @ApiOperation(value = "Get parcels to import", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getParcelsToImportByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @QueryParam(DELIVERY_DATE) @ApiParam(value = "Delivery date", example = "08/09/2017", required = false) deliveryDate: Date? = null
    ): List<Order>

    @GET
    @Path("/station/{$STATION_NO}/imported/order")
    @ApiOperation(value = "Get imported parcels", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getImportedParcelsByStationNo(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int,
            @QueryParam(DELIVERY_DATE) @ApiParam(value = "Delivery date", example = "08/09/2017", required = false) deliveryDate: Date? = null
    ): List<Order>

    @PATCH
    @Path("/")
    @ApiOperation(value = "Import parcel", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun import(
            @QueryParam(SCANCODE) @ApiParam(value = "Parcel number or creference", required = true) scanCode: String = "",
            @QueryParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int
    ): Order

    @PATCH
    @Path("/set-properties")
    @ApiOperation(value = "Set properties", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun setProperties(
            @ApiParam(value = "Parcel") parcel: Parcel

    ): Order

    @GET
    @Path("/{$SCANCODE}")
    @ApiOperation(value = "Get parcel", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getParcel(
            @PathParam(SCANCODE) @ApiParam(value = "Parcel number or creference", required = true) scanCode: String = "",
            @QueryParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int
    ): Order
}