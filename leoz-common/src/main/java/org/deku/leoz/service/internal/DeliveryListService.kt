package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.service.entity.ShortDate
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
        const val ORDER_ID = "order-id"
        const val ID = "delivery-list"
        const val DRIVER = "driver"
    }

    @GET
    @Path("/{${ID}}")
    @ApiOperation(value = "Get delivery list by id")
    fun getById(
            @PathParam(ID) @ApiParam(value = "Delivery list ID", required = true) id: String
    ): DeliveryList

    @GET
    @Path("/info")
    @ApiOperation(value = "Get delivery list info")
    fun get(
            @QueryParam(DRIVER) @ApiParam(value = "Driver", required = false) driver: String? = null
    ): List<DeliveryListInfo>

    /**
     * Created by JT on 24.05.17.
     *
     *  Order:       (Auftrag / Sendung) kann Packstücke beinhalten
     *  Shipment:   = Order
     *  Parcel:     (Packstück / Collie ) entält als Eigenschaft nur Gewicht und Größe
     *  Unit:       = Parcel
     *  Deliverylist:  List<Order>
     *  Stop
     *  Job
     */
    @ApiModel(description = "Delivery list")
    data class DeliveryList(
            @ApiModelProperty(position = 10, required = true, value = "DeliveryListInfo")
            val info: DeliveryListInfo,
            @ApiModelProperty(position = 30, required = true, value = "Orders")
            val orders: List<OrderService.Order>
    )

    @ApiModel(description = "Delivery list info")
    data class DeliveryListInfo(
            @ApiModelProperty(example = "12345678", position = 10, required = true, value = "DeliveryListID")
            val id: String,
            @ApiModelProperty(example = "2017-05-26", position = 20, required = true, value = "Date")
            val date: ShortDate
    )
}