package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.deku.leoz.service.internal.entity.LoadingList
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
        const val ORDERID = "orderId"
        const val LABELREFERENCE = "labelReference"
        const val CUSTOMERSREFERENCE = "customerReference"
        const val LOADINGLIST = "loadingList"
    }

    /**
     * Get Order
     * @param Order ref
     */
    @GET
    @Path("/")
    @ApiOperation(value = "Get Order")
    fun get(
            @QueryParam(PARCELSCAN) @ApiParam(value = "Order Reference") ref : String
    ): List<Order>

    /**
     * Vorschlag als Ergänzung
     * (Philipp)
     */
    @GET
    @Path("/order/find")
    @ApiOperation(value = "Get Order by label reference")
    fun getOrderByReference(
            @QueryParam(LABELREFERENCE) @ApiParam(value = "Label reference", required = false) labelRef: String? = null,
            @QueryParam(CUSTOMERSREFERENCE) @ApiParam(value = "Customers reference", required = false) custRef: String? = null
    ): List<Order>

    @GET
    @Path("/order/$ORDERID")
    @ApiOperation(value = "Get Order by label reference")
    fun getOrderByID(
            @PathParam(ORDERID) @ApiParam(value = "Unique order identifier", required = true) ref: String? = null
    ): Order

    @GET
    @Path("/loadingList/$LOADINGLIST")
    @ApiOperation(value = "Get Order")
    fun getLoadingListOrderByID(
            @PathParam(LOADINGLIST) @ApiParam(value = "Loadinglist ID", required = true) ref: String? = null
    ): LoadingList

    @GET
    @Path("/loadingList/find")
    @ApiOperation(value = "Get Order")
    fun getLoadingListOrderyOrder(
            @QueryParam(ORDERID) @ApiParam(value = "Unique order identifier", required = true) ref: String
    ): List<LoadingList>
}


