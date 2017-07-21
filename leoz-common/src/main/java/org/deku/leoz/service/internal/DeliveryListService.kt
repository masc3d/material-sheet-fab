package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.service.entity.ShortDate
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
        const val ID = "ID"
        //const val DRIVER = "driver"
        const val DELIVERY_DATE = "delivery-date"
    }

    @GET
    @Path("/{${ID}}")
    @ApiOperation(value = "Get delivery list by id")
    fun getById(
            @PathParam(ID) @ApiParam(example = "10730061", value = "Delivery list ID", required = true) id: Long
    ): DeliveryList

    @GET
    @Path("/info")
    @ApiOperation(value = "Get delivery list info")
    fun get(
            //todo not jet implemented            @QueryParam(DRIVER) @ApiParam(value = "Driver", required = false) driver: String? = null,
            @QueryParam(DELIVERY_DATE) @ApiParam(example = "2017-06-20", value = "Delivery date", required = false) deliveryDate: ShortDate? = null
    ): List<DeliveryListInfo>

    @ApiModel(description = "Delivery list")
    data class DeliveryList(
            @ApiModelProperty(example = "10730061", position = 10, required = true, value = "DeliveryListID")
            var id: Long = 0,
            @ApiModelProperty(example = "2017-06-20", position = 20, required = true)
            var info: DeliveryListInfo = DeliveryListInfo(),
            @ApiModelProperty(position = 30, required = true, value = "Orders within deliverylist")
            var orders: List<OrderService.Order> = listOf(),
            @ApiModelProperty(position = 40, required = true, value = "Stoplist")
            var stops: List<Stop> = listOf()
    )

    @ApiModel(description = "Task")
    data class Task(
            @ApiModelProperty(example = "12345678", position = 10, required = true, value = "order id")
            var orderId: Long = 0,
            @ApiModelProperty(example = "Pickup", position = 20, required = true, value = "stoptype")
            var stopType: Type = Task.Type.DELIVERY
    ) {
        enum class Type {
            PICKUP,
            DELIVERY
        }
    }

    @ApiModel(description = "Stop")
    data class Stop(
            //@ApiModelProperty-(example = "12345678", position = 10, required = true, value = "order id")
            var tasks: List<Task> = listOf()
    )

    @ApiModel(description = "Delivery list info")
    data class DeliveryListInfo(
            @ApiModelProperty(example = "10729637", position = 10, required = true, value = "DeliveryListID")
            var id: Long = 0,
            @ApiModelProperty(example = "2017-06-20", position = 20, required = true, value = "Date")
            var date: ShortDate = ShortDate()
    )

}