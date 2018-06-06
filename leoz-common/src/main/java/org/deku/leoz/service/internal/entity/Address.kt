package org.deku.leoz.service.internal.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty


@ApiModel(value = "Address", description = "Universal address")
data class Address(
        @ApiModelProperty(dataType = "string", example = "Hans Mustermann", position = 10, required = false, value = "addressLine1")
        var line1: String? = null,
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
        @get:ApiModelProperty(dataType = "string", example = "Schaafheim", position = 80, required = false, value = "Citty")
        var city: String? = null,
        @get:ApiModelProperty(dataType = "string", example = "Hauptstrasse", position = 90, required = false, value = "Street")
        var street: String? = null,
        @get:ApiModelProperty(dataType = "string", example = "HH 2", position = 100, required = false, value = "StreetNo")
        var streetNo: String? = null,
        @get:ApiModelProperty(position = 110, required = false, value = "Geo Location")
        var geoLocation: GeoLocation? = null
)

@ApiModel(value = "GeoLocation", description = "GeoLocation")
data class GeoLocation(
        @get:ApiModelProperty(dataType = "Double", example = "52.76866", position = 10, required = true, value = "latitude")
        var latitude: Double = 0.0,
        @get:ApiModelProperty(dataType = "Double", example = "8.875875875", position = 20, required = true, value = "longitude")
        var longitude: Double = 0.0
)

