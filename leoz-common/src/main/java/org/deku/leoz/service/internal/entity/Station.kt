package org.deku.leoz.service.internal.entity

/**
 * Created by masc on 04.05.15.
 */
class Station {
    var depotNr: Int? = null
    var depotMatchcode: String? = null
    var address1: String? = null
    var address2: String? = null
    var lkz: String? = null
    var plz: String? = null
    var ort: String? = null
    var strasse: String? = null
}

data class StationV2 (
    var stationNo: Int? = null,
    var stationMatchcode: String? = null,
    var address: Address?=null,
    var sector: String? = null,
    var exportValuablesAllowed: Boolean = false,
    var exportValuablesWithoutBagAllowed: Boolean = false
)
