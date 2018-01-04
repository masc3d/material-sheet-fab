package org.deku.leoz.service.internal

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.service.internal.entity.Address
import sx.io.serialization.Serializable
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

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
    @Path("/${NODE_UID}/{${NODE_UID}}")
    @ApiOperation(value = "Get tour by node uid", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getByNode(
            @PathParam(NODE_UID) @ApiParam(value = "Node uid", required = true)
            nodeUid: String
    ): Tour

    @GET
    @Path("/${USER_ID}/{${USER_ID}}")
    @ApiOperation(value = "Get tour by user", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getByUser(
            @PathParam(USER_ID) @ApiParam(value = "User id")
            userId: Int
    ): Tour

    @PATCH
    @Path("/{${ID}}/optimize")
    @ApiOperation(value = "Optimize tour", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun optimize(
            @PathParam(ID) @ApiParam(value = "Tour id")
            id: Int,
            @Suspended response: AsyncResponse
    )

    @ApiModel(description = "Tour")
    data class Tour(
            @ApiModelProperty(position = 10, required = false, value = "Tour id")
            var id: Int? = null,
            @ApiModelProperty(position = 20, required = false, value = "Node this tour belongs to")
            var nodeUid: String? = null,
            @ApiModelProperty(position = 20, required = false, value = "User this tour belongs to")
            var userId: Int? = null,
            @ApiModelProperty(position = 40, required = true, value = "Orders referenced by this tour")
            var orders: List<OrderService.Order> = listOf(),
            @ApiModelProperty(position = 50, required = true, value = "Tour stop list")
            var stops: List<Stop> = listOf()
    )

    @Serializable(0xc65eacc35a3d73)
    @JsonPropertyOrder("id")
    @ApiModel(description = "Stop")
    class Stop(
            /** The first stop task's address */
            @ApiModelProperty(position = 10, required = false, value = "Stop address")
            var address: Address? = null,
            /** Stop tasks */
            @ApiModelProperty(position = 20, required = true, value = "Stop tasks")
            var tasks: List<Task> = listOf(),

            @ApiModelProperty(position = 30, required = false, value = "Stop appointment start")
            var appointmentStart: Date? = null,
            @ApiModelProperty(position = 40, required = false, value = "Stop appointment end")
            var appointmentEnd: Date? = null
    )

    @Serializable(0x8eeb2fbff14af5)
    @ApiModel(description = "Task")
    data class Task(
            /**
             * Stop task id. Refers to its tour entry (task) id.
             * The task id is optional when updating tours, as the entire tour will be replaced
             */
            @ApiModelProperty(position = 10, required = false, value = "Stop task id")
            var id: Int? = null,
            @ApiModelProperty(position = 20, required = true, value = "Order id this task refers to")
            var orderId: Long = 0,
            @ApiModelProperty(position = 30, required = true, value = "Task type", example = "DELIVERY")
            var taskType: Task.Type = Type.DELIVERY,

            @ApiModelProperty(position = 40, required = false, value = "Task appointment start")
            var appointmentStart: Date? = null,
            @ApiModelProperty(position = 50, required = false, value = "Task appointment end")
            var appointmentEnd: Date? = null
    ) {
        enum class Type {
            PICKUP,
            DELIVERY
        }
    }

    /**
     * Tour update
     */
    @Serializable(0x227e7efb9e653c)
    data class TourUpdate(
            /** Updated tour (may be filled partially) */
            var tour: Tour? = null,
            /** Update timestamp */
            var timestamp: Date = Date()
    )
}

/** Stop id. Refers to the first task its tour entry (task) id */
val TourServiceV1.Stop.id: Int? get() = this.tasks.firstOrNull()?.id
