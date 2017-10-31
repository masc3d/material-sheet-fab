package org.deku.leoz.service.internal.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Created by masc on 04.05.15.
 */
@ApiModel(value = "Station", description = "Depot")
class Station {
    @get:ApiModelProperty(example = "102")
    var depotNr: Int? = null
    var depotMatchcode: String? = null
    var address1: String? = null
    var address2: String? = null
    var lkz: String? = null
    var plz: String? = null
    var ort: String? = null
    var strasse: String? = null
}

@ApiModel(value = "StationV2", description = "Station")
data class StationV2(
        @get:ApiModelProperty(example = "109", position = 100, required = false, value = "Station No")
        var stationNo: Int? = null,
        var stationMatchcode: String? = null,
        var address: Address? = null,
        var sector: String? = null,
        var exportValuablesAllowed: Boolean = false,
        var exportValuablesWithoutBagAllowed: Boolean = false
)
