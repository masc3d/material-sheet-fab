package org.deku.leoz.service.internal.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
/**
 * Created by helke on 16.03.17.
 */
@ApiModel(value = "BagInit", description = "Bag Init Request")
class BagInitRequest {
    @ApiModelProperty(dataType = "string", example = "900100000002", position = 2, required = true, value = "Bag white seal")
    var whiteSeal: String? = null

    @ApiModelProperty(dataType = "string", example = "900200000001", position = 3, required = true, value = "Bag yellow seal")
    var yellowSeal: String? = null

    @ApiModelProperty(dataType = "int", example = "020", position = 4, required = true, value = "Depot")
    var depotNr: Int? = null

    constructor() { }

    constructor(whiteSeal: String?,yellowSeal: String?,depotNr: Int){
        this.whiteSeal = whiteSeal
        this.yellowSeal = yellowSeal
        this.depotNr=depotNr
    }
}