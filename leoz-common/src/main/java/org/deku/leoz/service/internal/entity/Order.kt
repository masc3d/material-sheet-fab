package org.deku.leoz.service.internal.entity

import io.swagger.annotations.*
import org.deku.leoz.enums.AdditionalInformationType
import org.deku.leoz.enums.Carrier
import org.deku.leoz.enums.OrderClassifikation
import org.deku.leoz.service.entity.ShortDate
import java.sql.Date

/**
 * Created by JT on 24.05.17.
 *
 *
 *  Order:       (Auftrag / Sendung) kann Packstücke beinhalten
 *  Shipment:   = Order
 *  Parcel:     (Packstück / Collie ) entält als Eigenschaft nur Gewicht und Größe
 *  Unit:       = Parcel
 *  Loadinglist:
 *  Stop
 *  Job
 *
 *
 *
 *
 */
@ApiModel(description = "Loading List")
data class LoadingList(
        @ApiModelProperty(example = "12345678", position = 10, required = true, value = "LoadingListID")
        val id: String,
        @ApiModelProperty(example = "2017-05-26", position = 20, required = true, value = "Date")
        val date: ShortDate,
        @ApiModelProperty(position = 30, required = true, value = "Orders")
        val orders: List<Order>
)

@ApiModel(description = "Order Model")
data class Order(

        //pickups haben evtl keine parcels und benötigen daher eine id
        @get:ApiModelProperty(example = "12345678901", position = 10, required = true, value = "ShippmentID")
        var shippmentID: Int = 0,

        @get:ApiModelProperty(example = "DERKURIER", position = 20, required = true, value = "Carrier")
        var carrier: Carrier = Carrier.UNKNOWN,

        @get:ApiModelProperty(example = "12345678901", position = 30, required = false, value = "referencToExchangeShipmentID")
        var referencToExchangeShipmentID: Int = 0,

        //todo ich sehe nur OrderClassifikation.     PICKUP, DELIVERY, PICKUP_DELIVERY .
        //todo EXCHANGE_DELIVERY, EXCHANGE_PICKUP wie in stop.kt sehe ich nicht als addressclassifikation
        @get:ApiModelProperty(example = "DELIVERY", position = 40, required = true, value = "OrderClassifikation")
        var orderClassification: OrderClassifikation = OrderClassifikation.DELIVERY,

        @get:ApiModelProperty(position = 60, required = true, value = "appointmentPickup")
        var appointmentPickup: Appointment,

        @get:ApiModelProperty(position = 60, required = true, value = "appointmentDelivery")
        var appointmentDelivery: Appointment,

        //todo leo ist an der stelle kein Argument warum nicht eine List<Address> mit Classificirung der Adressen gemacht werden könnte.
        //todo Aber ich sehe keine weiteren. und dann find ich das klarer da die ja auch required sind
        @get:ApiModelProperty(required = true, position = 120, value = "Pickup address")
        var pickupAddress: Address = Address(),

        @ApiModelProperty(required = true, position = 130, value = "Delivery address")
        var deliveryAddress: Address = Address(),

        @get:ApiModelProperty(position = 140, required = false, value = "Parcels")
        var Parcels: List<Parcel>


) {
    companion object {
        val pN = "12345678901"
    }

}


@ApiModel(value = "Appointment", description = "Apointment")
data class Appointment(
        @ApiModelProperty(position = 10, required = true, value = "AppointmentStart")
        val dateStart: Date? = null,
        @ApiModelProperty(position = 20, required = true, value = "AppointmentEnd")
        val dateEnd: Date? = null,
        @ApiModelProperty(position = 30, required = false, value = "noDeliveryBefore")
        val noDeliveryBefore: Boolean = false,
        //todo ich habe jetzt den Service und die additionalinfos in den Stop mit reingenommen. jetzt ist die differenzierung wann der service relevant ist klarer
        //todo auch die evtl kommende abholnachnahme ist so besser
        @get:ApiModelProperty(example = "xChange", position = 40, required = false, value = "Shipment Service")
        var services: List<org.deku.leoz.enums.ParcelService>? = null,
        @get:ApiModelProperty(example = "AdditionalInformation", position = 50, required = false, value = "AdditionalInformation")
        var AdditionalInformation: List<AdditionalInformation>? = null
)

@ApiModel(value = "AdditionalInformation", description = "AdditionalInformation")
data class AdditionalInformation(

        @get:ApiModelProperty(example = "cash", position = 110, required = true, value = "AdditionalInformationType")
        val AdditionalInformationType: AdditionalInformationType? = null,

        @get:ApiModelProperty(example = "take cash in currency €", position = 120, required = false, value = "information")
        val information: String? = null,

        @get:ApiModelProperty(example = "10.99", position = 130, required = false, value = "cashAmount")
        var cashAmount: Double? = null

)

@ApiModel(value = "Parcel", description = "Parcel within Shipment")
data class Parcel(
        @get:ApiModelProperty(example = "12345678901", position = 10, required = true, value = "parcelScanNumber")
        var parcelScanNumber: String = "",

        @get:ApiModelProperty(example = "12345678901", position = 20, required = true, value = "parcelID")
        var parcelID: Int = 0,

        @get:ApiModelProperty(example = "12345678901", position = 30, required = false, value = "loadingListNo")
        var loadingListNo: Int? = null,

        @get:ApiModelProperty(position = 40, required = true, value = "ParcelDimension")
        var dimention: ParcelDimension? = null
)


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

@ApiModel(value = "Address", description = "pickup or delivery address")
data class Address(
        @ApiModelProperty(dataType = "string", example = "Hans Mustermann", position = 10, required = true, value = "addressLine1")
        val addressLine1: String = "",
        @get:ApiModelProperty(dataType = "string", example = "c/o Schuster", position = 20, required = false, value = "addressLine2")
        val addressLine2: String? = null,
        @get:ApiModelProperty(dataType = "string", example = "Bitte bei S klingeln", position = 30, required = false, value = "addressLine3")
        val addressLine3: String? = null,
        @get:ApiModelProperty(dataType = "string", example = "0123456789", position = 50, required = false, value = "Telefon Numbner")
        val telefonNumber: String? = null,
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
        val geoLocation: geoLocation? = null
)

@ApiModel(value = "geoLocation", description = "geoLocation")
data class geoLocation(
        @get:ApiModelProperty(dataType = "Double", example = "52.76866", position = 10, required = true, value = "lateral")
        val lateral: Double = 0.0,
        @get:ApiModelProperty(dataType = "Double", example = "8.875875875", position = 20, required = true, value = "longitudinal")
        val longitudinal: Double = 0.0
)
