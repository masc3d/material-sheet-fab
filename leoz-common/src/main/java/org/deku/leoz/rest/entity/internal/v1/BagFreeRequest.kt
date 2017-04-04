package org.deku.leoz.rest.entity.internal.v1

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
/**
 * Created by helke on 30.03.17.
 */
@ApiModel(value = "BagFree", description = "Bag Free Request")
class BagFreeRequest {

    @ApiModelProperty(dataType = "string", example = "700100000008", position = 1, required = true, value = "Bag id")
    var bagId: String? = null


    @ApiModelProperty(dataType = "int", example = "020", position = 2, required = true, value = "Depot")
    var depotNr: Int? = null

    constructor() { }

    constructor(bagId: String?, depotNr: Int){
        this.bagId = bagId
        this.depotNr=depotNr
    }
}