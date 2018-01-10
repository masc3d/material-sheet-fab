package org.deku.leoz.service.internal

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.service.internal.entity.Address
import sx.io.serialization.Serializable
import sx.rs.auth.ApiKey
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.sse.Sse
import javax.ws.rs.sse.SseEventSink

/**
 * Tour service interface
 */
@Path("internal/v1/tour")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Tour service")
@ApiKey(false)
interface TourServiceV1 {
    companion object {
        const val ID = "id"
        const val DEBITOR_ID = "debitor-id"
        const val NODE_UID = "node-uid"
        const val USER_ID = "user-id"
    }

    @GET
    @Path("/")
    @ApiOperation(
            value = "Get tour(s)",
            authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun get(
            @QueryParam(DEBITOR_ID) @ApiParam(value = "Debitor id", required = false)
            debitorId: Int?,
            @QueryParam(USER_ID) @ApiParam(value = "User id", required = false)
            userId: Int?
    ): List<Tour>

    @GET
    @Path("/{${ID}}")
    @ApiOperation(value = "Get tour by id", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getById(
            @PathParam(ID) @ApiParam(value = "Tour id", required = true)
            id: Int
    ): Tour

    @GET
    @Path("/node/{${NODE_UID}}")
    @ApiOperation(value = "Get tour by node uid", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getByNode(
            @PathParam(NODE_UID) @ApiParam(value = "Node uid", required = true)
            nodeUid: String
    ): Tour

    @GET
    @Path("/user/{${USER_ID}}")
    @ApiOperation(value = "Get (current) tour for a user", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getByUser(
            @PathParam(USER_ID) @ApiParam(value = "User id")
            userId: Int
    ): Tour

    /**
     * Contains information for creating a tour from a delivery list
     * @param deliveryListId
     */
    @ApiModel(description = "Contains information for creating a tour from a delivery list")
    data class TourFromDeliverylist(
            @ApiModelProperty(position = 10, required = true, value = "Delivery list (id) to create tour from")
            var deliveryListId: Int? = null,
            @ApiModelProperty(position = 20, required = true, value = "User (id) this tour will get assigned to")
            var userId: Int? = null
    )

    @POST
    @Path("/deliverylist")
    @ApiOperation(value = "Create a new tour from a delivery list",
            notes = "This tour will not be attached to a specific node and be used to split/optimize.",
            authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun create(
            @ApiParam(value = "Request for creating tour from a delivery list")
            request: TourFromDeliverylist
    ): Tour

    @PATCH
    @Path("/{${ID}}/optimize")
    @ApiOperation(value = "Optimize tour",
            notes = "This call is synchronous and will update central entities on completion.",
            authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun optimize(
            @PathParam(ID) @ApiParam(value = "Tour id")
            id: Int,
            @ApiParam(value = "Tour optimization options")
            optimizationOptions: TourOptimizationOptions,
            @Suspended response: AsyncResponse
    )

    @GET
    @Path("/optimize/status/sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @ApiOperation(
            value = "Get tour status updates",
            notes = "This call uses server-sent-events (SSE)",
            authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun status(
            @ApiParam(value = "Tour ids")
            ids: List<Int>,
            @Context sink: SseEventSink,
            @Context sse: Sse
    )

    @PATCH
    @Path("/node/{${NODE_UID}}/optimize")
    @ApiOperation(
            value = "Optimize tour for a (mobile) node",
            notes = "Starts the optimization process" +
                    " and sends a tour update (message) to the node which owns the tour when complete." +
                    " Central entities won't be updated as this is the responsibility of the node" +
                    " once the user has accepted the optimized tour.",
            authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun optimizeForNode(
            @PathParam(NODE_UID) @ApiParam(value = "Mobile node id")
            nodeUid: String,
            @ApiParam(value = "Tour optimization options")
            optimizationOptions: TourOptimizationOptions
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

    @Serializable(0xc41f67f95c2025)
    @ApiModel(description = "Tour optimization options")
    data class TourOptimizationOptions(
            @ApiModelProperty(position = 10,
                    required = false,
                    value = "Amend appointment times",
                    notes = "Amends all appointment times, so they relate to the current day",
                    example = "false"
            )
            var amendAppointmentTimes: Boolean = false
    )

    /**
     * Tour update
     */
    @Serializable(0x227e7efb9e653c)
    data class TourUpdate(
            /** Updated tour (may be filled partially) */
            var tour: Tour? = null,
            /** Update timestamp */
            var timestamp: Date = Date(),
            /** True whenthis update is a tour optimization */
            var isOptimization: Boolean = false
    )

    /**
     * Tour optimization request
     */
    @Serializable(0x20ccdbc9cf990c)
    data class TourOptimizationRequest(
            var nodeUid: String? = null,
            var optimizationOptions: TourOptimizationOptions = TourOptimizationOptions()
    )

    /**
     * Tour optimization status
     */
    @Serializable(0xdf5d24c36e52ac)
    data class TourOptimizationStatus(
            /** Tour id */
            var id: Int = 0,
            /** Optimization progress */
            var inProgress: Boolean = false
    )

    /**
     * Tour optimization error
     */
    @Serializable(0xc416aca7da4d56)
    data class TourOptimizationError(
            var type: Type = Type.ROUTE_COULD_NOT_BE_DETERMINED
    ) {
        enum class Type {
            INVALID_REQUEST,
            ROUTE_COULD_NOT_BE_DETERMINED
        }
    }
}

/** Stop id. Refers to the first task its tour entry (task) id */
val TourServiceV1.Stop.id: Int? get() = this.tasks.firstOrNull()?.id
