package org.deku.leoz.rest.entity.internal.v1

import io.swagger.annotations.ApiModel

/**
 * Created by helke on 04.04.17.
 */
@ApiModel(value = "BagserviceNumberRange", description = "BagserviceNumberRange")
class BagNumberRange(
        var minBagId: Double? = null,
        var maxBagId: Double? = null,
        var minWhiteSeal: Double? = null,
        var maxWhiteSeal: Double? = null,
        var minYellowSeal: Double? = null,
        var maxYellowSeal: Double? = null,
        var minCollieNr: Double? = null,
        var maxCollieNr: Double? = null,
        var minCollieNrBack: Double? = null,
        var maxCollieNrBack: Double? = null
)