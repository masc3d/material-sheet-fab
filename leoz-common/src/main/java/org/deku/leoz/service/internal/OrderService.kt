package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.deku.leoz.service.internal.entity.Order
import javax.ws.rs.*
import javax.ws.rs.core.MediaType


/**
 * Created by JT on 24.05.17.
 */

@Path("internal/v1/order")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Order service")

interface OrderService {
    companion object {
        const val PARCELSCAN = "parcel-scan"
        const val ORDERID = "order-id"
        const val LABELREFERENCE = "label-reference"
        const val CUSTOMERSREFERENCE = "customer-reference"
    }

    /**
     * Get order by id
     * @param id Order id
     */

    @GET
    @Path("/{$ORDERID}")
    @ApiOperation(value = "Get order by order ID")
    fun getById(
            @PathParam(ORDERID) @ApiParam(value = "Unique order identifier", required = true) id: String
    ): Order


    /**
     * Get orders
     * @param labelRef Label reference (optional query param)
     * @param custRef Custom reference (optional query param)
     * @param ref Order reference (optional query param)
     */
    @GET
    @Path("/")
    @ApiOperation(value = "Get orders")
    fun get(
            @QueryParam(LABELREFERENCE) @ApiParam(value = "Label reference", required = false) labelRef: String? = null,
            @QueryParam(CUSTOMERSREFERENCE) @ApiParam(value = "Customers reference", required = false) custRef: String? = null,
            @QueryParam(PARCELSCAN) @ApiParam(value = "Order reference") ref: String? = null
    ): List<Order>
}


