package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.entity.HEADERPARAM_APIKEY
import javax.ws.rs.*
import javax.ws.rs.core.MediaType


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

@Path("internal/v1/deliverylist")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Delivery list service")
interface DeliveryListService {
    companion object {
        //const val ORDER_ID = "order-id"
        const val ID = "id"
        //const val DRIVER = "driver"
        const val DELIVERY_DATE = "delivery-date"
    }

    @GET
    @Path("/{${ID}}")
    @ApiOperation(value = "Get delivery list by id")
    fun getById(
            @PathParam(ID) @ApiParam(example = "89586115", value = "Delivery list id", required = true) id: Long
    ): DeliveryList

    @GET
    @Path("/{${ID}}")
    @ApiOperation(value = "Get delivery list by id")
    fun getById(
            @PathParam(ID) @ApiParam(example = "89586115", value = "Delivery list id", required = true) id: Long,
            @HeaderParam(HEADERPARAM_APIKEY) @ApiParam(hidden = true) apiKey: String?
    ): DeliveryList

    @GET
    @Path("/info")
    @ApiOperation(value = "Get delivery list info")
    fun get(
            //todo not jet implemented            @QueryParam(DRIVER) @ApiParam(value = "Driver", required = false) driver: String? = null,
            @QueryParam(DELIVERY_DATE) @ApiParam(example = "2017-08-10", value = "Delivery date", required = false) deliveryDate: ShortDate? = null
    ): List<DeliveryListInfo>

    @GET
    @Path("/info")
    @ApiOperation(value = "Get delivery list info")
    fun get(
            //todo not jet implemented            @QueryParam(DRIVER) @ApiParam(value = "Driver", required = false) driver: String? = null,
            @QueryParam(DELIVERY_DATE) @ApiParam(example = "2017-08-10", value = "Delivery date", required = false) deliveryDate: ShortDate? = null,
            @HeaderParam(HEADERPARAM_APIKEY) @ApiParam(hidden = true) apiKey: String?
    ): List<DeliveryListInfo>

    @ApiModel(description = "Delivery list")
    data class DeliveryList(
            @ApiModelProperty(example = "89586115", position = 10, required = true, value = "Delivery list id")
            var id: Long = 0,
            @ApiModelProperty(example = "info", position = 20, required = true)
            var info: DeliveryListInfo = DeliveryListInfo(),
            @ApiModelProperty(position = 30, required = true, value = "Orders within delivery list")
            var orders: List<OrderService.Order> = listOf(),
            @ApiModelProperty(position = 40, required = true, value = "Stop list")
            var stops: List<Stop> = listOf()
    )

    @ApiModel(description = "Task")
    data class Task(
            @ApiModelProperty(example = "12345678", position = 10, required = true, value = "Order id")
            var orderId: Long = 0,
            @ApiModelProperty(example = "Pickup", position = 20, required = true, value = "Stop type")
            var stopType: Type = Task.Type.DELIVERY,
            @ApiModelProperty(example = "false", position = 30, required = true, value = "is removed from Deliverylist")
            var isRemoved: Boolean = false
    ) {
        enum class Type {
            PICKUP,
            DELIVERY
        }
    }

    @ApiModel(description = "Stop")
    data class Stop(
            var tasks: List<Task> = listOf()
    )

    @ApiModel(description = "Delivery list info")
    data class DeliveryListInfo(
            @ApiModelProperty(example = "89586115", position = 10, required = true, value = "Delivery list id")
            var id: Long = 0,
            @ApiModelProperty(example = "2017-08-10", position = 20, required = true, value = "Delivery list date")
            var date: ShortDate = ShortDate(),
            @ApiModelProperty(example = "1020", position = 10, required = true, value = "Debitor id")
            var debitorId: Long = 0
    )
}
