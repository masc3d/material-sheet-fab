package org.deku.leoz.rest.entity.internal.v1
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
/**
 * Created by helke on 04.04.17.
 */
@ApiModel(value = "SectionDepotsRequest", description = "SectionDepotsRequest")
class SectionDepotsRequest {
    @ApiModelProperty(dataType = "int", example = "1", position = 1, required = true, value = "Section")
    var section: Int? = null


    @ApiModelProperty(dataType = "int", example = "1", position = 2, required = true, value = "Position")
    var position: Int? = null

    constructor() { }

    constructor(section: Int, position: Int){
        this.section = section
        this.position=position
    }
}