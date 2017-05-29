package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.deku.leoz.service.internal.entity.DeliveryList
import org.deku.leoz.service.internal.entity.Order
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 29.05.17.
 */

@Path("internal/v1/deliverylist")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "DeliveryList service")

interface DeliveryListService {

    companion object {
        const val ORDERID = "order-id"
        const val DELIVERYLIST = "delivery-list"
        const val DRIVER = "driver"
    }

    @GET
    @Path("/{${DELIVERYLIST}}")
    @ApiOperation(value = "Get List<order> by Deliverylist")
    fun getOrderByDeliverylist(
            @PathParam(DELIVERYLIST) @ApiParam(value = "Deliverylist ID", required = true) ref: String? = null
    ): List<Order>

    @GET
    @Path("/find")
    @ApiOperation(value = "valid DeliveryLists")
    fun getDeliveryList(
            @QueryParam(DRIVER) @ApiParam(value = "Driver", required = false) driver: String? = null
    ): List<DeliveryList>


}