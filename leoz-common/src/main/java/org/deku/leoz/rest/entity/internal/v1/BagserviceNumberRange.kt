package org.deku.leoz.rest.entity.internal.v1
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
/**
 * Created by helke on 04.04.17.
 */
@ApiModel(value = "BagserviceNumberRange", description = "BagserviceNumberRange")
class BagserviceNumberRange {

    var dblMinBagId:Double?=null
    var dblMaxBagId:Double?=null
    var dblMinWhiteSeal:Double?=null
    var dblMaxWhiteSeal:Double?=null
    var dblMinYellowSeal:Double?=null
    var dblMaxYellowSeal:Double?=null
    var dblMinCollieNr:Double?=null
    var dblMaxCollieNr:Double?=null
    var dblMinCollieNrBack:Double?=null
    var dblMaxCollieNrBack:Double?=null

    constructor() { }
    constructor(minBagId:Double?,maxBagId:Double?,
                minWhiteSeal:Double?,maxWhiteSeal:Double?,
                minYellowSeal:Double?,maxYellowSeal:Double?,
                minCollieNr:Double?,maxCollieNr:Double?,
                minCollieNrBack:Double?,maxCollieNrBack:Double?){
        this.dblMinBagId=minBagId
        this.dblMaxBagId=maxBagId
        this.dblMinWhiteSeal=minWhiteSeal
        this.dblMaxWhiteSeal=maxWhiteSeal
        this.dblMinYellowSeal=minYellowSeal
        this.dblMaxYellowSeal=maxYellowSeal
        this.dblMinCollieNr=minCollieNr
        this.dblMaxCollieNr=maxCollieNr
        this.dblMinCollieNrBack=minCollieNrBack
        this.dblMaxCollieNrBack=maxCollieNrBack
    }
}