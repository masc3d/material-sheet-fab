package org.deku.leoz.rest.entity.internal.v1
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
/**
 * Created by helke on 10.04.17.
 */
class BagDiff {
    var unitno:String?=null
    var section:Int?=null
    var deliverydepot:String?=null
    var notice:String?=null
    var delta:Int?=null

    constructor() { }
    constructor(unitno:String?,section:Int?,deliverydepot:String?,notice:String?,delta:Int?) {
        this.unitno=unitno
        this.section=section
        this.deliverydepot=deliverydepot
        this.notice=notice
        this.delta=delta
    }
}