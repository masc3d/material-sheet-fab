package org.deku.leoz.service.internal.entity

import io.swagger.annotations.*
/**
 * Created by helke on 24.05.17.
 */
@ApiModel(description = "GpsData Model")
data class GpsData (
    @get:ApiModelProperty(example = "49.9283", required = false, value = "lat")
    var lat: Double? = null,
    @get:ApiModelProperty(example = "9.0164", required = false, value = "long")
    var long: Double? = null,
    @get:ApiModelProperty(example = "24.6", required = false, value = "speed")
    var speed: Double? = null,
    @get:ApiModelProperty(example = "2017-03-16T17:00:00.000Z", required = false, value = "gpsTimestamp")
    var gpsTimestamp: java.util.Date? = null


){}