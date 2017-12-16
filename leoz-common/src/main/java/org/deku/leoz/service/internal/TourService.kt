package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.service.entity.ShortDate
import sx.io.serialization.Serializable
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Tour service interface
 */
@Path("internal/v1/tour")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Tour service")
interface TourServiceV1 {
    companion object {
        const val ID = "id"
        const val NODE_UID = "node-uid"
        const val USER_ID = "user-id"
    }

    @GET
    @Path("/{${ID}}")
    @ApiOperation(value = "Get tour by id", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getById(
            @PathParam(ID) @ApiParam(value = "Tour id", required = true)
            id: Int
    ): Tour

    @GET
    @Path("/by-node/{${NODE_UID}}")
    @ApiOperation(value = "Get tour by node uid", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getByNode(
            @PathParam(NODE_UID) @ApiParam(value = "Node uid", required = true)
            nodeUid: String
    ): Tour

    @GET
    @Path("/by-user/{${USER_ID}}")
    @ApiOperation(value = "Get tour by user", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getByUser(
            @PathParam(USER_ID) @ApiParam(value = "User id")
            userId: Int
    ): Tour

    @ApiModel(description = "Tour")
    data class Tour(
            @ApiModelProperty(position = 10, required = true, value = "Tour id")
            var id: Int = 0,
            @ApiModelProperty(position = 20, required = true, value = "Node this tour belongs to")
            var nodeUid: String? = null,
            @ApiModelProperty(position = 20, required = true, value = "User this tour belongs to")
            var userId: Int? = null,
            @ApiModelProperty(position = 40, required = true, value = "Orders referenced by this tour")
            var orders: List<OrderService.Order> = listOf(),
            @ApiModelProperty(position = 50, required = true, value = "Tour stop list")
            var stops: List<Stop> = listOf()
    )

    @Serializable(0xc65eacc35a3d73)
    @ApiModel(description = "Stop")
    data class Stop(
            var tasks: List<Task> = listOf()
    )

    @Serializable(0x8eeb2fbff14af5)
    @ApiModel(description = "Task")
    data class Task(
            @ApiModelProperty(position = 10, required = true, value = "Order id")
            var orderId: Long = 0,
            @ApiModelProperty(position = 20, required = true, value = "Task type", example = "DELIVERY")
            var taskType: Task.Type = Type.DELIVERY
    ) {
        enum class Type {
            PICKUP,
            DELIVERY
        }
    }

    /**
     * Message sent when tour is updated
     */
    @Serializable(0x227e7efb9e653c)
    data class TourUpdateMessage(
            /** The node uid this message originates from */
            var nodeUid: String = "",
            /** User id */
            var userId: Int = 0,
            /** Tour update timestamp */
            var timestamp: Date = Date(),
            /** Stops in current tour order */
            var stops: List<Stop> = listOf()
    )
}
