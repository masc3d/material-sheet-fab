package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.model.*
import org.deku.leoz.config.Rest
import sx.rs.auth.ApiKey
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import org.deku.leoz.service.internal.entity.Address

/**
 * Created by JT on 24.05.17.
 */
@Path("internal/v1/order")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Order service")
@ApiKey(false)
interface OrderService {
    companion object {
        const val PARCELSCAN = "parcel-scan"
        const val ORDERID = "order-id"
        const val LABELREFERENCE = "label-reference"
        const val CUSTOMERSREFERENCE = "customer-reference"
    }

    /**
     * Get order by id
     * @param id Order id
     */

    @GET
    @Path("/{$ORDERID}")
    @ApiOperation(value = "Get order by order ID", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getById(
            @PathParam(ORDERID) @ApiParam(value = "Unique order identifier", required = true) id: Long
    ): Order

    @GET
    @Path("/{$ORDERID}")
    @ApiOperation(value = "Get order by order ID", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getById(
            @PathParam(ORDERID) @ApiParam(value = "Unique order identifier", required = true) id: Long,
            @HeaderParam(Rest.API_KEY) @ApiParam(hidden = true) apiKey: String?
    ): Order

    /**
     * Get orders
     * @param labelRef Label reference (optional query param)
     * @param custRef Custom reference (optional query param)
     * @param ref Order reference (optional query param)
     */

    @GET
    @Path("/")
    @ApiOperation(value = "Get orders", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun get(
            @QueryParam(LABELREFERENCE) @ApiParam(value = "Label reference", required = false) labelRef: String? = null,
            @QueryParam(CUSTOMERSREFERENCE) @ApiParam(value = "Customers reference", required = false) custRef: String? = null,
            @QueryParam(PARCELSCAN) @ApiParam(value = "Parcel scan reference") parcelScan: String? = null
    ): List<Order>


    @GET
    @Path("/")
    @ApiOperation(value = "Get orders", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun get(
            @QueryParam(LABELREFERENCE) @ApiParam(value = "Label reference", required = false) labelRef: String? = null,
            @QueryParam(CUSTOMERSREFERENCE) @ApiParam(value = "Customers reference", required = false) custRef: String? = null,
            @QueryParam(PARCELSCAN) @ApiParam(value = "Parcel scan reference") parcelScan: String? = null,
            @HeaderParam(Rest.API_KEY) @ApiParam(hidden = true) apiKey: String?
    ): List<Order>

    @ApiModel(description = "Order Model")
    data class Order(

            @get:ApiModelProperty(example = "12345678901", position = 10, required = true, value = "OrderID")
            var id: Long = 0,

            @get:ApiModelProperty(example = "DERKURIER", position = 20, required = true, value = "Carrier")
            var carrier: Carrier = Carrier.UNKNOWN,

            @get:ApiModelProperty(example = "12345678901", position = 30, required = false, value = "referenceIDToExchangeOrderID")
            var referenceIDToExchangeOrderID: Long = 0,

            @get:ApiModelProperty(example = "DELIVERY", position = 40, required = true, value = "OrderClassification")
            var orderClassification: OrderClassification = OrderClassification.DELIVERY,

            @get:ApiModelProperty(position = 60, required = true, value = "Pickup appointment")
            var pickupAppointment: Appointment = Appointment(),
            @get:ApiModelProperty(required = true, position = 70, value = "Pickup address")
            var pickupAddress: Address = Address(),
            @get:ApiModelProperty(required = true, position = 80, value = "Pickup service")
            var pickupServices: List<ParcelService>? = listOf(),
            @get:ApiModelProperty(required = true, position = 90, value = "Pickup text information")
            var pickupNotice: String? = null,
            @get:ApiModelProperty(required = true, position = 95, value = "Pickup Station")
            var pickupStation: Int = 0,

            @get:ApiModelProperty(position = 100, required = true, value = "Delivery appointment")
            var deliveryAppointment: Appointment = Appointment(),
            @ApiModelProperty(required = true, position = 110, value = "Delivery address")
            var deliveryAddress: Address = Address(),
            @get:ApiModelProperty(required = true, position = 50, value = "Delivery services")
            var deliveryServices: List<ParcelService>? = listOf(),
            @get:ApiModelProperty(required = true, position = 130, value = "Delivery Cash information")
            var deliveryCashService: CashService? = null,
            @get:ApiModelProperty(required = true, position = 140, value = "Delivery text information")
            var deliveryNotice: String? = null,
            @get:ApiModelProperty(required = true, position = 145, value = "Delivery Station")
            var deliveryStation: Int = 0,

            @get:ApiModelProperty(position = 150, required = false, value = "Parcels")
            var parcels: List<Parcel> = listOf()
    ) {
        companion object {
            val pN = "12345678901"
        }



        data class CashService(
                @get:ApiModelProperty(example = "10.99", position = 130, required = false, value = "cashAmount")
                var cashAmount: Double = 0.0,
                /** Currency code according to ISO 4217 https://en.wikipedia.org/wiki/ISO_4217 */
                var currency: String = "EUR"
        )

        @ApiModel(value = "Appointment", description = "Apointment")
        data class Appointment(
                @ApiModelProperty(position = 10, required = true, value = "AppointmentStart")
                var dateStart: Date? = null,
                @ApiModelProperty(position = 20, required = true, value = "AppointmentEnd")
                var dateEnd: Date? = null,
                @ApiModelProperty(position = 30, required = false, value = "notBeforeStart")
                var notBeforeStart: Boolean = false
        )

        @ApiModel(value = "Parcel", description = "Parcel within Order")
        data class Parcel(
                @get:ApiModelProperty(example = "1234567890101", position = 20, required = true, value = "parcelID")
                var id: Long = 0,
                @get:ApiModelProperty(example = "12345678901", position = 10, required = true, value = "parcelScanNumber")
                var number: String = "",
                @get:ApiModelProperty(example = "info", position = 30, required = true, value = "parcelType")
                var parcelType: ParcelType = ParcelType.UNKNOWN,
                @get:ApiModelProperty(example = "10729985", position = 40, required = true, value = "parcelType")
                var lastDeliveryListId: Int? = null,
                @get:ApiModelProperty(example = "info", position = 50, required = false, value = "information")
                var notice: String? = null,
                @get:ApiModelProperty(position = 60, required = true, value = "ParcelDimension")
                var dimension: ParcelDimension = Parcel.ParcelDimension(),
                @get:ApiModelProperty(position = 70, required = true, value = "is Delivered")
                var isDelivered: Boolean = false,
                @get:ApiModelProperty(position = 80, required = true, value = "is Missing")
                var isMissing: Boolean = false,
                @get:ApiModelProperty(position = 80, required = true, value = "is Damaged")
                var isDamaged: Boolean = false
        ) {
            @ApiModel(value = "ParcelDimentions", description = "Parcel dementions and weight")
            data class ParcelDimension(
                    @ApiModelProperty(dataType = "double", example = "10", position = 10, required = false, value = "length")
                    var length: Int? = null,
                    @ApiModelProperty(dataType = "double", example = "20", position = 20, required = false, value = "height")
                    var height: Int? = null,
                    @ApiModelProperty(dataType = "double", example = "30", position = 30, required = false, value = "width")
                    var width: Int? = null,
                    @ApiModelProperty(dataType = "double", example = "11.5", position = 40, required = true, value = "weight")
                    var weight: Double = 0.0
            )
        }
    }

}


