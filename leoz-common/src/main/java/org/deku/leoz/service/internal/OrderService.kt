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
        const val PARCELSCAN = "parcel-scan"
        const val ORDERID = "order-id"
        const val LABELREFERENCE = "label-reference"
        const val CUSTOMERSREFERENCE = "customer-reference"
        const val LOADINGLIST = "loading-list"
        const val DRIVER = "driver"
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
//todo im Fall xchange muss falls der Rücksatz benötigt wird dieser separat geholt werden
    ): Order

    /**
     * Vorschlag als Ergänzung
     * (Philipp)
     */
    @GET
    @Path("/order/find")
    @ApiOperation(value = "Get order by label reference")
    fun getOrderByReference(
            @QueryParam(LABELREFERENCE) @ApiParam(value = "Label reference", required = false) labelRef: String? = null,
            @QueryParam(CUSTOMERSREFERENCE) @ApiParam(value = "Customers reference", required = false) custRef: String? = null
    ): Order

    @GET
    @Path("/order/find/{$ORDERID}")
    @ApiOperation(value = "Get order by label reference")
    fun getOrderByID(
            @PathParam(ORDERID) @ApiParam(value = "Unique order identifier", required = true) ref: String? = null
    ): Order

    @GET
    @Path("/order/loadingList/{$LOADINGLIST}")
    @ApiOperation(value = "Get List<order> by Loadinlist")
    fun getOrderByLoadinglist(
            @PathParam(LOADINGLIST) @ApiParam(value = "Loadinglist ID", required = true) ref: String? = null
    ): List<Order>

    @GET
    @Path("/loadingList/find")
    @ApiOperation(value = "valid loadingLists")
    fun getLoadingListOrderyOrder(
            @QueryParam(DRIVER) @ApiParam(value = "Driver", required = false) driver: String? = null
            ): List<LoadingList>

}


