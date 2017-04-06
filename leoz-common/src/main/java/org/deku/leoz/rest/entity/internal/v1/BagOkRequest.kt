package org.deku.leoz.rest.entity.internal.v1
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
/**
 * Created by helke on 05.04.17.
 */
@ApiModel(value = "BagOk", description = "Bag Ok Request")
class BagOkRequest {
    @ApiModelProperty(dataType = "string", example = "700100000008", position = 1, required = true, value = "Bag id")
    var bagId: String? = null

    @ApiModelProperty(dataType = "string", example = "100710000002", position = 2, required = true, value = "Bag Collie Nr")
    var bagCollieNr: String? = null

    constructor() { }

    constructor(bagId: String?, bagCollieNr:String?){
        this.bagId = bagId
        this.bagCollieNr=bagCollieNr

    }
}