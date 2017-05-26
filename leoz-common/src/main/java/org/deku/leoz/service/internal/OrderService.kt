package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.deku.leoz.enums.Carrier
import org.deku.leoz.enums.OrderClassifikation
import org.deku.leoz.service.internal.entity.GpsData
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

interface OrderService  {
    companion object {
        const val PARCELSCAN = "ParcelScan"
    }

    /**
     * Get Order
     * @param Order ref
     */
    @GET
    @Path("/")
    @ApiOperation(value = "Get Order")
    fun get(
            @QueryParam(PARCELSCAN) @ApiParam(value = "Order Reference") ref : String? = null
    ): List<Order>

}


