package org.deku.leoz.service.internal

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.model.TourRouteMeta
import org.deku.leoz.model.TourStopRouteMeta
import org.deku.leoz.service.entity.ShortDate
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
        const val FROM = "from"
        const val INCLUDE_RELATED = "include-related"
        const val NODE_UID = "node-uid"
        const val STATION_NO = "station-no"
        const val TO = "to"
        const val USER_ID = "user-id"
        const val WAIT_FOR_COMPLETION = "wait-for-completion"
    }

    @GET
    @Path("/")
    @ApiOperation(
            value = "Get tour(s)",
            authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun get(
            @QueryParam(DEBITOR_ID) @ApiParam(value = "Debitor id", required = false)
            debitorId: Long?,
            @QueryParam(STATION_NO) @ApiParam(value = "Station no", required = false)
            stationNo: Long?,
            @QueryParam(USER_ID) @ApiParam(value = "User id", required = false)
            userId: Long?,
            @QueryParam(FROM) @ApiParam(example = "2018-01-27", value = "From (tour) date", required = false)
            from: ShortDate?,
            @QueryParam(TO) @ApiParam(example = "2018-01-27", value = "To (tour) date", required = false)
            to: ShortDate?
    ): List<Tour>

    @GET
    @Path("/{${ID}}")
    @ApiOperation(value = "Get tour by id", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getById(
            @PathParam(ID) @ApiParam(value = "Tour id", required = true)
            id: Long
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
            userId: Long
    ): Tour

    @POST
    @Path("/deliverylist")
    @ApiOperation(value = "Create a new tour from delivery lists",
            notes = "The tours created will be owned by the same station",
            authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun create(
            @ApiParam(value = "Source delivery list id(s)")
            deliverylistIds: List<Long>
    ): List<Tour>

    @DELETE
    @Path("/")
    @ApiOperation(value = "Delete tour(s)",
            authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun delete(
            @QueryParam(ID) @ApiParam(value = "Tour id(s)", required = false)
            ids: List<Long>,
            @QueryParam(USER_ID) @ApiParam(value = "User id", required = false)
            userId: Long?,
            @QueryParam(STATION_NO) @ApiParam(value = "Station no", required = false)
            stationNo: Long?,
            @QueryParam(INCLUDE_RELATED) @ApiParam(value = "Inlcude related tours (eg. split optimized tours)", required = false, defaultValue = "false")
            includeRelated: Boolean
    )

    @PATCH
    @Path("/optimize")
    @ApiOperation(value = "Optimize tour(s)",
            authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun optimize(
            @QueryParam(ID) @ApiParam(value = "Tour id(s)")
            ids: List<Long>,
            @QueryParam(WAIT_FOR_COMPLETION) @ApiParam(value = "Wait for optimization completion", example = "false")
            waitForCompletion: Boolean,
            @ApiParam(value = "Tour optimization options")
            options: TourOptimizationOptions,
            @Suspended response: AsyncResponse
    )

    @PATCH
    @Path("/{${ID}}/optimize")
    @ApiOperation(value = "Optimize tour",
            authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun optimize(
            @PathParam(ID) @ApiParam(value = "Tour id")
            id: Long,
            @QueryParam(WAIT_FOR_COMPLETION) @ApiParam(value = "Wait for optimization completion", example = "false")
            waitForCompletion: Boolean,
            @ApiParam(value = "Tour optimization options")
            options: TourOptimizationOptions,
            @Suspended response: AsyncResponse
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
            options: TourOptimizationOptions
    )


    @Deprecated("Superseded by `subscribe`")
    @GET
    @Path("/optimize/status/sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @ApiOperation(
            value = "Get tour status updates",
            notes = "This call uses server-sent-events (SSE)",
            authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(
            ApiResponse(code = 200, response = TourOptimizationStatus::class, message = "Tour optimization status")
    )
    fun status(
            @QueryParam(STATION_NO)
            @ApiParam(value = "The station no to retrieve tour status updates for")
            stationNo: Long,
            @Context sink: SseEventSink,
            @Context sse: Sse
    )

    @GET
    @Path("/subscribe/sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @ApiOperation(
            value = "Subscribe to tour updates",
            notes = "This call uses server-sent-events (SSE)",
            authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(
            ApiResponse(code = 200, response = SubscriptionEvent::class, message = "Tour service subscription event")
    )
    fun subscribe(
            @QueryParam(STATION_NO)
            @ApiParam(value = "The station no to get tour updates for")
            stationNo: Long,
            @Context sink: SseEventSink,
            @Context sse: Sse
    )

    @ApiModel(description = "Tour")
    data class Tour(
            @ApiModelProperty(position = 10, required = false, value = "Tour id")
            var id: Long? = null,
            @ApiModelProperty(position = 15, required = false, value = "Tour uid")
            var uid: String? = null,
            @ApiModelProperty(position = 20, required = false, value = "Node this tour belongs to")
            var nodeUid: String? = null,
            @ApiModelProperty(position = 30, required = false, value = "User this tour belongs to")
            var userId: Long? = null,
            @ApiModelProperty(position = 40, required = false, value = "Station this tour belongs to")
            var stationNo: Long? = null,
            @ApiModelProperty(position = 45, required = false,
                    value = "Parent id. Refers to the original tour in case" +
                            "this tour was created as an optimization result")
            var parentId: Long? = null,
            @ApiModelProperty(position = 50, required = false, value = "Custom id for this tour")
            var customId: String? = null,
            @Deprecated("Will be replaced with customid")
            @ApiModelProperty(position = 55, required = false, value = "Delivery list this tour refers to")
            var deliverylistId: Long? = customId?.toLongOrNull(),
            @ApiModelProperty(position = 60, required = true, value = "Tour date")
            var date: ShortDate? = null,
            @ApiModelProperty(position = 70, required = true, value = "Orders referenced by this tour")
            var orders: List<OrderService.Order> = listOf(),
            @ApiModelProperty(position = 80, required = true, value = "Tour stop list")
            var stops: List<Stop> = listOf(),
            @ApiModelProperty(position = 90, required = false, value = "Last optimization time")
            var optimized: Date? = null,
            @ApiModelProperty(position = 100, required = true, value = "Creation date")
            var created: Date? = null,

            @ApiModelProperty(position = 110, required = false,
                    value = "Tour level route informations. Available when the tour was optimized")
            var route: TourRouteMeta? = null
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
            var appointmentEnd: Date? = null,

            @ApiModelProperty(position = 50, required = false, value = "Stop weight")
            var weight: Double? = null,

            @ApiModelProperty(position = 60, required = false,
                    value = "Stop level route information. Available when the tour was optimized")
            var route: TourStopRouteMeta? = null
    )

    @Serializable(0x8eeb2fbff14af5)
    @ApiModel(description = "Task")
    data class Task(
            /**
             * Stop task id. Refers to its tour entry (task) id.
             * The task id is optional when updating tours, as the entire tour will be replaced
             */
            @ApiModelProperty(position = 10, required = false, value = "Stop task id")
            var id: Long? = null,
            @ApiModelProperty(position = 15, required = false, value = "Stop task uid")
            var uid: String? = null,
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
                    value = "Start address/location")
            var start: Address? = null,

            @ApiModelProperty(position = 20,
                    required = false,
                    value = "Appointment related options")
            var appointments: Appointments = Appointments(),

            @ApiModelProperty(position = 30,
                    required = false,
                    value = "Vehicles to optimize for",
                    notes = "When this parameter is omitted, the tour will be optimized in place. " +
                            "When provided, new tours will be created for each vehicle.")
            var vehicles: List<Vehicle>? = null,

            @ApiModelProperty(position = 40,
                    required = false,
                    value = "Omit loads",
                    notes = "Omits load parameters (on delivery/stop and vehicle level)")
            var omitLoads: Boolean? = null,

            @ApiModelProperty(position = 50,
                    required = false,
                    example = "true",
                    value = "Consider traffic")
            var traffic: Boolean = true
    ) {
        @Serializable(0xf045189c321341)
        @ApiModel(description = "Appointment options")
        data class Appointments(
                @ApiModelProperty(position = 10,
                        required = false,
                        value = "Omit appointments",
                        notes = "Omits appointments entirely",
                        example = "false")
                var omit: Boolean = false,

                @ApiModelProperty(position = 20,
                        required = false,
                        value = "Replaces date of all appointments with current day",
                        example = "false")
                var replaceDatesWithToday: Boolean = false,

                @ApiModelProperty(position = 30,
                        required = false,
                        value = "Shift appointment times in hours",
                        notes = "When this parameter is provided, appointments times are shifted " +
                                "relative to the current time (rounded to next full hour)",
                        example = "4")
                var shiftHoursFromNow: Int? = null,

                @ApiModelProperty(position = 40,
                        required = false,
                        value = "Shift appointment times in days",
                        notes = "When this parameter is provided, appointments times are shifted " +
                                "relative to the current day",
                        example = "1")
                var shiftDaysFromNow: Int? = null
        )

        @Serializable(0x4bcec10612464e)
        data class Vehicle(
                @ApiModelProperty(position = 10,
                        required = false,
                        value = "Vehicle capacity in kg",
                        example = "500.0")
                var capacity: Double = DEFAULT_CAPACITY
        ) {
            companion object {
                val DEFAULT_CAPACITY: Double = 500.0
            }
        }


    }

    /**
     * Generic tour update (for mq).
     */
    @Serializable(0x227e7efb9e653c)
    data class TourUpdate(
            /** Updated tour (may be filled partially) */
            var tour: Tour? = null,
            /** Update timestamp */
            var timestamp: Date = Date()
    )

    /**
     * Tour optimization request (for mq)
     */
    @Serializable(0x20ccdbc9cf990c)
    data class TourOptimizationRequest(
            /** Unique uid for this request. Will be passed back on response */
            var requestUid: String? = null,
            /** Node uid requesting optimization */
            var nodeUid: String? = null,
            /** Tour optimization options */
            var options: TourOptimizationOptions = TourOptimizationOptions(),

            /** Override start location with specific station address (for testing) */
            var startStationNo: Int? = null
    )

    /**
     * Tour optimization result (for mq)
     */
    @Serializable(0x8a511f02d293cb)
    data class TourOptimizationResult(
            /** Request uid this response belongs to */
            var requestUid: String? = null,
            /** Node uid */
            var nodeUid: String? = null,
            /** Optimized tour or null in case an error occured */
            var tour: Tour? = null,
            /** Error details */
            var error: ErrorType? = null
    ) {
        enum class ErrorType {
            INVALID_REQUEST,
            REMOTE_REQUEST_FAILED,
            IN_PROGRESS,
            ROUTE_COULD_NOT_BE_DETERMINED
        }
    }

    /**
     * Tour optimization status
     */
    @Serializable(0xdf5d24c36e52ac)
    @ApiModel(description = "Tour optimization status")
    data class TourOptimizationStatus(
            /** Tour id */
            var id: Long = 0,
            /** Optimization progress */
            var inProgress: Boolean = false
    )

    /**
     * Tour update / subscription event
     * TODO: generalize
     */
    @Serializable(0x5e37fb2d93c1b9)
    @ApiModel(description = "Tour service subscription event")
    data class SubscriptionEvent(
            var stationNo: Long? = null,
            var items: List<Tour>? = null,
            var deleted: List<Long>? = null
    )
}

/** Stop id. Refers to the first task its tour entry (task) id */
val TourServiceV1.Stop.id: Long? get() = this.tasks.firstOrNull()?.id

/** Stop id. Refers to the first task its tour entry (task) id */
val TourServiceV1.Stop.uid: String? get() = this.tasks.firstOrNull()?.uid
