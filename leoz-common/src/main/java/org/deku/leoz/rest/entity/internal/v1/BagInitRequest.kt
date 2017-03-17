package org.deku.leoz.rest.entity.internal.v1

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
/**
 * Created by helke on 16.03.17.
 */
@ApiModel(value = "BagInit", description = "Bag Init Request")
class BagInitRequest {
    @ApiModelProperty(dataType = "string", example = "700100000000", position = 1, required = true, value = "Bag id")
    var bagId: String? = null

    @ApiModelProperty(dataType = "string", example = "900100000000", position = 2, required = true, value = "Bag white seal")
    var whiteSeal: String? = null

    @ApiModelProperty(dataType = "string", example = "900200000000", position = 3, required = true, value = "Bag yellow seal")
    var yellowSeal: String? = null

    @ApiModelProperty(dataType = "int", example = "020", position = 4, required = true, value = "Depot")
    var depotNr: Int? = null

    constructor() { }

    constructor(bagId: String?, whiteSeal: String?,yellowSeal: String?,depotNr: Int){
        this.bagId = bagId
        this.whiteSeal = whiteSeal
        this.yellowSeal = yellowSeal
        this.depotNr=depotNr
    }
}