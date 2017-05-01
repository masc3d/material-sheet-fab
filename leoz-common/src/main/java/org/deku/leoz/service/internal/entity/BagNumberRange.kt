package org.deku.leoz.service.internal.entity

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
        var minUnitNo: Double? = null,
        var maxUnitNo: Double? = null,
        var minUnitNoBack: Double? = null,
        var maxUnitNoBack: Double? = null
)