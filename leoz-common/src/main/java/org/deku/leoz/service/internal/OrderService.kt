package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.model.AdditionalInformationType
import org.deku.leoz.model.Carrier
import org.deku.leoz.model.OrderClassification
import org.deku.leoz.model.ParcelService
import java.sql.Date
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
            @PathParam(ORDERID) @ApiParam(value = "Unique order identifier", required = true) id: String
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
            @QueryParam(PARCELSCAN) @ApiParam(value = "Order reference") ref: String? = null
    ): List<Order>

    @ApiModel(description = "Order Model")
    data class Order(

            //pickups haben evtl keine parcels und benötigen daher eine id
            @get:ApiModelProperty(example = "12345678901", position = 10, required = true, value = "OrderID")
            var orderID: Int = 0,

            @get:ApiModelProperty(example = "DERKURIER", position = 20, required = true, value = "Carrier")
            var carrier: Carrier = Carrier.UNKNOWN,

            @get:ApiModelProperty(example = "12345678901", position = 30, required = false, value = "referenceIDToExchangeOrderID")
            var referenceIDToExchangeOrderID: Int = 0,

            @get:ApiModelProperty(example = "DELIVERY", position = 40, required = true, value = "OrderClassification")
            var orderClassification: OrderClassification = OrderClassification.DELIVERY,

            @get:ApiModelProperty(position = 60, required = true, value = "appointmentPickup")
            var appointmentPickup: Appointment,
            @get:ApiModelProperty(required = true, position = 120, value = "Pickup address")
            var pickupAddress: Address = Address(),
            @get:ApiModelProperty(required = true, position = 120, value = "Pickup service")
            var pickupService: Service = Order.Service(listOf(ParcelService.NO_ADDITIONAL_SERVICE)),
            @get:ApiModelProperty(required = true, position = 120, value = "Pickup information")
            var pickupInformation: Information? = null,

            @get:ApiModelProperty(position = 60, required = true, value = "appointmentDelivery")
            var appointmentDelivery: Appointment,
            @ApiModelProperty(required = true, position = 130, value = "Delivery address")
            var deliveryAddress: Address = Address(),
            @get:ApiModelProperty(required = true, position = 120, value = "delivery services")
            var deliveryService: Service = Order.Service(listOf(ParcelService.NO_ADDITIONAL_SERVICE)),
            @get:ApiModelProperty(required = true, position = 120, value = "delivery information")
            var deliveryInformation: Information? = null,

            @get:ApiModelProperty(position = 140, required = false, value = "Parcels")
            var Parcels: List<Parcel>


    ) {
        companion object {
            val pN = "12345678901"
        }

        @ApiModel(value = "Address", description = "pickup or delivery address")
        data class Address(
                @ApiModelProperty(dataType = "string", example = "Hans Mustermann", position = 10, required = true, value = "addressLine1")
                val addressLine1: String = "",
                @get:ApiModelProperty(dataType = "string", example = "c/o Schuster", position = 20, required = false, value = "addressLine2")
                val addressLine2: String? = null,
                @get:ApiModelProperty(dataType = "string", example = "Bitte bei S klingeln", position = 30, required = false, value = "addressLine3")
                val addressLine3: String? = null,
                @get:ApiModelProperty(dataType = "string", example = "0123456789", position = 50, required = false, value = "Telefon Numbner")
                val phoneNumber: String? = null,
                @get:ApiModelProperty(dataType = "string", example = "DE", position = 60, required = true, value = "Countr Code")
                val countryCode: String = "",
                @get:ApiModelProperty(dataType = "string", example = "648450", position = 70, required = true, value = "Zip")
                val zipCode: String = "",
                @get:ApiModelProperty(dataType = "string", example = "Schaafheim", position = 80, required = true, value = "Citty")
                val city: String = "",
                @get:ApiModelProperty(dataType = "string", example = "Hauptstrasse", position = 90, required = true, value = "Street")
                val street: String = "",
                @get:ApiModelProperty(dataType = "string", example = "HH 2", position = 100, required = false, value = "StreetNo")
                val streetNo: String? = null,
                @get:ApiModelProperty(position = 110, required = false, value = "Geo Location")
                val geoLocation: GeoLocation? = null
        ) {
            @ApiModel(value = "geoLocation", description = "geoLocation")
            data class GeoLocation(
                    @get:ApiModelProperty(dataType = "Double", example = "52.76866", position = 10, required = true, value = "latitude")
                    val latitude: Double = 0.0,
                    @get:ApiModelProperty(dataType = "Double", example = "8.875875875", position = 20, required = true, value = "longitude")
                    val longitude: Double = 0.0
            )
        }

        @ApiModel(value = "Service", description = "Service")
        data class Service(
                @get:ApiModelProperty(example = "xChange", position = 40, required = false, value = "Order Service")
                var services: List<ParcelService>? = null
        )

        @ApiModel(value = "Information", description = "Information")
        data class Information(
                @get:ApiModelProperty(example = "AdditionalInformation", position = 50, required = false, value = "AdditionalInformation")
                var additionalInformation: List<AdditionalInformation>? = null
        )


        @ApiModel(value = "Appointment", description = "Apointment")
        data class Appointment(
                @ApiModelProperty(position = 10, required = true, value = "AppointmentStart")
                val dateStart: Date? = null,
                @ApiModelProperty(position = 20, required = true, value = "AppointmentEnd")
                val dateEnd: Date? = null,
                @ApiModelProperty(position = 30, required = false, value = "noDeliveryBefore")
                val noDeliveryBefore: Boolean = false
        )

        @ApiModel(value = "AdditionalInformation", description = "AdditionalInformation")
        data class AdditionalInformation(

                @get:ApiModelProperty(example = "cash", position = 110, required = true, value = "AdditionalInformationType")
                val additionalInformationType: AdditionalInformationType? = null,

                @get:ApiModelProperty(example = "take cash in currency €", position = 120, required = false, value = "information")
                val information: String? = null,

                @get:ApiModelProperty(example = "10.99", position = 130, required = false, value = "cashAmount")
                var cashAmount: Double? = null

        )

        @ApiModel(value = "Parcel", description = "Parcel within Order")
        data class Parcel(
                @get:ApiModelProperty(example = "12345678901", position = 10, required = true, value = "parcelScanNumber")
                var parcelScanNumber: String = "",

                @get:ApiModelProperty(example = "12345678901", position = 20, required = true, value = "parcelID")
                var parcelID: Int = 0,

                @get:ApiModelProperty(position = 40, required = true, value = "ParcelDimension")
                var dimension: ParcelDimension? = null
        ) {
            @ApiModel(value = "ParcelDimentions", description = "Parcel dementions and weight")
            data class ParcelDimension(
                    @ApiModelProperty(dataType = "double", example = "10", position = 10, required = false, value = "length")
                    val length: Double? = null,
                    @ApiModelProperty(dataType = "double", example = "20", position = 20, required = false, value = "height")
                    val height: Double? = null,
                    @ApiModelProperty(dataType = "double", example = "30", position = 30, required = false, value = "width")
                    val width: Double? = null,
                    @ApiModelProperty(dataType = "double", example = "10", position = 40, required = true, value = "weight")
                    val weight: Double = 0.0
            )
        }
    }

}


