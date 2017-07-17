package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.model.*
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 24.05.17.
 */
@Path("internal/v1/order")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Order service")

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
    @ApiOperation(value = "Get order by order ID")
    fun getById(
            @PathParam(ORDERID) @ApiParam(value = "Unique order identifier", required = true) id: Long
    ): Order

    /**
     * Get orders
     * @param labelRef Label reference (optional query param)
     * @param custRef Custom reference (optional query param)
     * @param ref Order reference (optional query param)
     */
    @GET
    @Path("/")
    @ApiOperation(value = "Get orders")
    fun get(
            @QueryParam(LABELREFERENCE) @ApiParam(value = "Label reference", required = false) labelRef: String? = null,
            @QueryParam(CUSTOMERSREFERENCE) @ApiParam(value = "Customers reference", required = false) custRef: String? = null,
            @QueryParam(PARCELSCAN) @ApiParam(value = "Parcel scan reference") parcelScan: String? = null
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

            @get:ApiModelProperty(position = 60, required = true, value = "appointmentPickup")
            var pickupAppointment: Appointment = Appointment(),
            @get:ApiModelProperty(required = true, position = 70, value = "Pickup address")
            var pickupAddress: Address = Address(),
            @get:ApiModelProperty(required = true, position = 80, value = "Pickup service")
            var pickupServices: List<ParcelService>? = listOf(),
            @get:ApiModelProperty(required = true, position = 90, value = "Pickup text information")
            var pickupNotice: String? = null,

            @get:ApiModelProperty(position = 100, required = true, value = "appointmentDelivery")
            var deliveryAppointment: Appointment = Appointment(),
            @ApiModelProperty(required = true, position = 110, value = "Delivery address")
            var deliveryAddress: Address = Address(),
            @get:ApiModelProperty(required = true, position = 50, value = "delivery services")
            var deliveryServices: List<ParcelService>? = listOf(),
            @get:ApiModelProperty(required = true, position = 130, value = "delivery Cash information")
            var deliveryCashService: CashService? = null,

            @get:ApiModelProperty(required = true, position = 140, value = "delivery text information")
            var deliveryNotice: String? = null,


            @get:ApiModelProperty(position = 150, required = false, value = "Parcels")
            var parcels: List<Parcel> = listOf()
    ) {
        companion object {
            val pN = "12345678901"
        }

        @ApiModel(value = "Address", description = "pickup or delivery address")
        data class Address(
                @ApiModelProperty(dataType = "string", example = "Hans Mustermann", position = 10, required = true, value = "addressLine1")
                var line1: String = "",
                @get:ApiModelProperty(dataType = "string", example = "c/o Schuster", position = 20, required = false, value = "addressLine2")
                var line2: String? = null,
                @get:ApiModelProperty(dataType = "string", example = "Bitte bei S klingeln", position = 30, required = false, value = "addressLine3")
                var line3: String? = null,
                @get:ApiModelProperty(dataType = "string", example = "0123456789", position = 50, required = false, value = "Telefon Numbner")
                var phoneNumber: String? = null,
                @get:ApiModelProperty(dataType = "string", example = "DE", position = 60, required = true, value = "Countr Code")
                var countryCode: String = "",
                @get:ApiModelProperty(dataType = "string", example = "648450", position = 70, required = true, value = "Zip")
                var zipCode: String = "",
                @get:ApiModelProperty(dataType = "string", example = "Schaafheim", position = 80, required = true, value = "Citty")
                var city: String = "",
                @get:ApiModelProperty(dataType = "string", example = "Hauptstrasse", position = 90, required = true, value = "Street")
                var street: String = "",
                @get:ApiModelProperty(dataType = "string", example = "HH 2", position = 100, required = false, value = "StreetNo")
                var streetNo: String? = null,
                @get:ApiModelProperty(position = 110, required = false, value = "Geo Location")
                var geoLocation: GeoLocation? = null
        ) {
            @ApiModel(value = "geoLocation", description = "geoLocation")
            data class GeoLocation(
                    @get:ApiModelProperty(dataType = "Double", example = "52.76866", position = 10, required = true, value = "latitude")
                    var latitude: Double = 0.0,
                    @get:ApiModelProperty(dataType = "Double", example = "8.875875875", position = 20, required = true, value = "longitude")
                    var longitude: Double = 0.0
            )
        }

//        @ApiModel(value = "Service", description = "Service")
//        data class Service(
//                @get:ApiModelProperty(example = "xChange", position = 40, required = false, value = "Order Service")
//                var services: List<ParcelService>? = null
//        )


        data class CashService(
                //                @get:ApiModelProperty(example = "take cash in currency €", position = 120, required = false, value = "information")
                @get:ApiModelProperty(example = "10.99", position = 130, required = false, value = "cashAmount")
                var cashAmount: Double = 0.0,
                var currency: String = "DE"
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
                var dimension: ParcelDimension = Parcel.ParcelDimension()
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


