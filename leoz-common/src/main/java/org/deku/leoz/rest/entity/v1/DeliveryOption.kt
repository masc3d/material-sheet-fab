package org.deku.leoz.rest.entity.v1

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Created by 27694066 on 02.03.2017.
 */
@ApiModel(value = "DeliveryOption", description = "Delivery Options")
class DeliveryOption {
    @ApiModelProperty(dataType = "string", position = 1, required = true, value = "Delivery option identifier")
    var id: String? = null

    @ApiModelProperty(dataType = "string", position = 2, required = true, value = "Cut off time (ISO-8601) for order modification / cancelation")
    var cut_off: String? = null

    @ApiModelProperty(dataType = "string", position = 3, required = true, value = "Scheduled pick up time (ISO-8601)")
    var pick_up: String? = null

    @ApiModelProperty(dataType = "string", position = 4, required = true, value = "Earliest possible delivery time (ISO-8601)")
    var delivery_from: String? = null

    @ApiModelProperty(dataType = "string", position = 5, required = true, value = "Latest possible delivery time (ISO-8601)")
    var delivery_to: String? = null

    constructor(){}

    constructor(id: String?, cut_off: String?, pick_up: String?, delivery_from: String?, delivery_to: String?) {
        this.id = id
        this.cut_off = cut_off
        this.pick_up = pick_up
        this.delivery_from = delivery_from
        this.delivery_to = delivery_to
    }


}