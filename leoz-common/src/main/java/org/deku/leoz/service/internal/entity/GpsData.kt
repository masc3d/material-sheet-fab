package org.deku.leoz.service.internal.entity

import io.swagger.annotations.*

/**
 *
 * Created by helke on 24.05.17.
 */
@ApiModel(description = "GpsData Model")
data class GpsData(
        @get:ApiModelProperty(example = "foo@bar.com", required = true, value = "User identifier")
        var userEmail: String? = null,
        @get:ApiModelProperty(required = false, value = "Positions")
        var gpsDataPoints: List<GpsDataPoint>? = null
)