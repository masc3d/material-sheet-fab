package org.deku.leoz.rest.entity.zalando.v1

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Created by 27694066 on 10.03.2017.
 */
@ApiModel(value = "NotifiedDeliveryOrder", description = "Notified delivery order")
class NotifiedDeliveryOrder {
    @ApiModelProperty(dataType = "string", position = 1, required = true, value = "Delivery order identifier in called system")
    var id: String? = null

    @ApiModelProperty(dataType = "string", notes = "URL", position = 2, required = false, value = "Link to delivery order tracking page")
    var tracking_url: String? = null

    constructor(){}

    constructor(id: String){
        this.id = id
    }

    constructor(id: String, tracking_url: String?){
        this.id = id
        this.tracking_url = tracking_url
    }
}