package org.deku.leoz.rest.entity.zalando.v1

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * Created by 27694066 on 10.03.2017.
 */
@ApiModel(value = "DeliveryOrder")
class DeliveryOrder {
    @ApiModelProperty(dataType = "string", position = 1, required = true, value = "Delivery order identifier in calling system")
    var incoming_id: String? = null

    @ApiModelProperty(dataType = "DeliveryOption", position = 2, required = true, value = "Delivery option")
    var delivery_option: DeliveryOption? = null

    @ApiModelProperty(dataType = "DeliveryAddress", position = 3, required = true, value = "Delivery option")
    var source_address: DeliveryAddress? = null

    @ApiModelProperty(dataType = "DeliveryAddress", position = 4, required = true, value = "Delivery option")
    var target_address: DeliveryAddress? = null
}
