package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.deku.leoz.service.internal.entity.DeliveryList
import org.deku.leoz.service.internal.entity.DeliveryListInfo
import org.deku.leoz.service.internal.entity.Order
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 29.05.17.
 */

@Path("internal/v1/delivery-list")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Delivery list service")

interface DeliveryListService {

    companion object {
        const val ORDERID = "order-id"
        const val DELIVERYLIST = "delivery-list"
        const val DRIVER = "driver"
    }

    @GET
    @Path("/{${DELIVERYLIST}}")
    @ApiOperation(value = "Get delivery list by id")
    fun getById(
            @PathParam(DELIVERYLIST) @ApiParam(value = "Delivery list ID", required = true) id: String
    ): DeliveryList

    @GET
    @Path("/")
    @ApiOperation(value = "Get delivery list info")
    fun get(
            @QueryParam(DRIVER) @ApiParam(value = "Driver", required = false) driver: String? = null
    ): List<DeliveryListInfo>
}