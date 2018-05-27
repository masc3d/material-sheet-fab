package org.deku.leoz.service.internal.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Created by 27694066 on 22.02.2017.
 */
@ApiModel(value = "OutgoingBag", description = "Outgoing Bag Request")
class OutgoingBag {
    @ApiModelProperty(dataType = "string", example = "700100000000", position = 1, required = true, value = "Bag reference")
    var bagReference: String? = null

    @ApiModelProperty(dataType = "string", example = "900100000000", position = 2, required = true, value = "Bag white lead-seal")
    var leadSeal: String? = null

    constructor() { }

    constructor(bagReference: String?, leadSeal: String?){
        this.bagReference = bagReference
        this.leadSeal = leadSeal
    }
}