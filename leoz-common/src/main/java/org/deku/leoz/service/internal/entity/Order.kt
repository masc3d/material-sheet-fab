package org.deku.leoz.service.internal.entity

import io.swagger.annotations.*
import org.deku.leoz.enums.Carrier
import org.deku.leoz.enums.OrderClassifikation
import org.deku.leoz.service.entity.ShortDate
import java.sql.Time

/**
 * Created by JT on 24.05.17.
 */
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
        //todo EXCHANGE_DELIVERY, EXCHANGE_PICKUP wie in stop.kt sehe ich nicht als addressclassifikation sondern als service

        @get:ApiModelProperty(example = "DELIVERY", position = 40, required = true, value = "OrderClassifikation")
        var orderClassifikation: OrderClassifikation = OrderClassifikation.DELIVERY,

        @get:ApiModelProperty(example = "xChange", position = 50, required = false, value = "Shipment Service")
        var services: List<org.deku.leoz.enums.ParcelService>? = null,

        //todo zur zeit nur für fixzeitfenster benutzt könnte auch als zusätlicher service in org.deku.leoz.enums.ParcelService eingefügt werden
        @get:ApiModelProperty(example = "FIXTIMEWINDOWPICKUP", position = 60, required = false, value = "Internal Service")
        var internalServices: List<org.deku.leoz.enums.ShipmentInternalServices>? = null,

        //todo @masc ist ShortDate hier best?
        @get:ApiModelProperty(example = "12-05-2017", position = 70, required = true, value = "pickup Date")
        var pickupDate: ShortDate? = null,

        //todo @masc ist Time hier best?
        @get:ApiModelProperty(example = "09:00-12:00", position = 80, required = true, value = "pickup Tiemwindow")
        var pickupTiemwindow: Pair<Time, Time> = Pair(Time(0), Time(0)),

        @get:ApiModelProperty(example = "13-05-2017", position = 90, required = true, value = "delivery Date")
        var deliveryDate: ShortDate? = null,

        @get:ApiModelProperty(example = "09:00-12:00", position = 100, required = true, value = "delivery Tiemwindow")
        var deliveryTiemwindow: Pair<Time, Time> = Pair(Time(0), Time(0)),

        @get:ApiModelProperty(example = "10.10", position = 110, required = false, value = "cashAmount")
        var cashAmount: Double? = null,

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

@ApiModel(value = "Parcel", description = "Parcel within Shipment")
class Parcel {
    @get:ApiModelProperty(example = "12345678901", position = 10, required = true, value = "parcelScanNumber")
    var parcelScanNumber: String = ""

    //todo : id vielleicht nicht nötig ?
    @get:ApiModelProperty(example = "12345678901", position = 20, required = true, value = "parcelID")
    var parcelID: Int = 0

    @get:ApiModelProperty(example = "12345678901", position = 30, required = false, value = "loadingListNo")
    var loadingListNo: Int? = null

    @get:ApiModelProperty(position = 40, required = true, value = "ParcelDimension")
    var dimention: ParcelDimension? = null

    constructor() {}
}


@ApiModel(value = "ParcelDimentions", description = "Parcel dementions and weight")
class ParcelDimension {
    @ApiModelProperty(dataType = "double", example = "10", position = 10, required = false, value = "length")
    val length: Double? = null
    @ApiModelProperty(dataType = "double", example = "20", position = 20, required = false, value = "height")
    val height: Double? = null
    @ApiModelProperty(dataType = "double", example = "30", position = 30, required = false, value = "width")
    val width: Double? = null
    @ApiModelProperty(dataType = "double", example = "10", position = 40, required = true, value = "weight")
    val weight: Double = 0.0
}

@ApiModel(value = "Address", description = "pickup or delivery address")
class Address {
    @ApiModelProperty(dataType = "string", example = "Hans Mustermann", position = 10, required = true, value = "addressLine1")
    val addressLine1: String = ""
    @get:ApiModelProperty(dataType = "string", example = "c/o Schuster", position = 20, required = false, value = "addressLine2")
    val addressLine2: String? = null
    @get:ApiModelProperty(dataType = "string", example = "Bitte bei S klingeln", position = 30, required = false, value = "addressLine3")
    val addressLine3: String? = null
    @get:ApiModelProperty(dataType = "string", example = "Freundlich sein", position = 40, required = false, value = "Driver Information")
    val driverInformation: String? = null
    @get:ApiModelProperty(dataType = "string", example = "0123456789", position = 50, required = false, value = "Telefon Numbner")
    val telefonNumber: String? = null
    @get:ApiModelProperty(dataType = "string", example = "DE", position = 60, required = true, value = "Countr Code")
    val countryCode: String = ""
    @get:ApiModelProperty(dataType = "string", example = "648450", position = 70, required = true, value = "Zip")
    val zipCode: String = ""
    @get:ApiModelProperty(dataType = "string", example = "Schaafheim", position = 80, required = true, value = "Citty")
    val city: String = ""
    @get:ApiModelProperty(dataType = "string", example = "Hauptstrasse", position = 90, required = true, value = "Street")
    val street: String = ""
    @get:ApiModelProperty(dataType = "string", example = "HH 2", position = 100, required = false, value = "StreetNo")
    val streetNo: String? = null
    @get:ApiModelProperty(position = 110, required = false, value = "Geo Location")
    val geoLocation: geoLocation? = null

    constructor() {}


    companion object {
    }

}

@ApiModel(value = "geoLocation", description = "geoLocation")
class geoLocation {
    @get:ApiModelProperty(dataType = "Double", example = "52.76866", position = 10, required = true, value = "lateral")
    val lateral: Double = 0.0
    @get:ApiModelProperty(dataType = "Double", example = "8.875875875", position = 20, required = true, value = "longitudinal")
    val longitudinal: Double = 0.0

    constructor() {}

}
