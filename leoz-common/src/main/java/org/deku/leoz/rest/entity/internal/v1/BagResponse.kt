package org.deku.leoz.rest.entity.internal.v1
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
/**
 * Created by helke on 05.04.17.
 */
class BagResponse {
    var ok=false
    var info:String?=null
    constructor() { }
    constructor(ok:Boolean,info:String?) {
        this.ok=ok
        this.info=info
    }
}