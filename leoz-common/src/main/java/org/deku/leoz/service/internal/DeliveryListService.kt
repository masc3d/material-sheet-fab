package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.service.entity.ShortDate
import sx.io.serialization.Serializable
import sx.rs.auth.ApiKey
import java.util.*
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
@ApiKey
interface DeliveryListService {
    companion object {
        const val ID = "id"
        const val STATION_ID = "station-id"
        const val DELIVERY_DATE = "delivery-date"
    }

    @GET
    @Path("/{${ID}}")
    @ApiOperation(value = "Get delivery list by id", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getById(
            @PathParam(ID) @ApiParam(example = "89586115", value = "Delivery list id", required = true)
            id: Long
    ): DeliveryList

    @GET
    @Path("/station/{${STATION_ID}}")
    @ApiOperation(value = "Get delivery list(s)", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getByStationId(
            @PathParam(STATION_ID) @ApiParam(example = "89586115", value = "Station id", required = false)
            stationId: Int?
    ): List<DeliveryList>

    @GET
    @Path("/info")
    @ApiOperation(value = "Get delivery list info/overview", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getInfo(
            //todo not jet implemented            @QueryParam(DRIVER) @ApiParam(value = "Driver", required = false) driver: String? = null,
            @QueryParam(DELIVERY_DATE) @ApiParam(example = "2017-08-10", value = "Delivery date", required = false)
            deliveryDate: ShortDate?)
            : List<DeliveryListInfo>

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

    @Serializable(0x3c2666979388d7)
    @ApiModel(description = "Task")
    data class Task(
            @ApiModelProperty(example = "12345678", position = 10, required = true, value = "Order id")
            var orderId: Long = 0,
            @Deprecated("Required by leoz-central <= 0.147, leoz-mobile <= 0.118")
            @ApiModelProperty(example = "PICKUP", position = 20, required = true, value = "Stop type")
            var stopType: Type = Task.Type.DELIVERY,
            @ApiModelProperty(example = "PICKUP", position = 30, required = true, value = "Task type")
            var taskType: Type? = null,
            @ApiModelProperty(example = "false", position = 40, required = true, value = "is removed from Deliverylist")
            var isRemoved: Boolean = false
    ) {
        enum class Type {
            PICKUP,
            DELIVERY
        }
    }

    @Serializable(0x24dd5d7b4cf66b)
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
            @ApiModelProperty(example = "1020", position = 30, required = true, value = "Debitor id")
            var debitorId: Long = 0,
            @ApiModelProperty(position = 40, value = "Modification timestamp")
            var modified: Date? = null
    )

    /**
     * Message sent when delivery list order is updated
     */
    @Deprecated("Required for mobile <= 0.118")
    @Serializable(0x84361dd5ef5f84)
    data class StopOrderUpdateMessage(
            /** The node uid this message originates from */
            var nodeUid: String = "",
            /** User id */
            var userId: Int = 0,
            /** Stops in updated order */
            var stops: List<Stop> = listOf()
    )
}
