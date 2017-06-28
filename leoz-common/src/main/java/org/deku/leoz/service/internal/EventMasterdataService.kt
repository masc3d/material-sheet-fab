package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.service.entity.EventReason
import sx.io.serialization.Serializable
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 23.06.17.
 */
@Path("internal/v1/eventmasterdata")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Event Masterdata service")
interface EventMasterdataService {
// returns all events

    @GET
    @Path("/")
    @ApiOperation(value = "Get EventMasterdata")
    fun get(): List<EventReason>


    @Serializable(0x999)
    @ApiModel(value = "Event", description = "delivered, not delivered")
    data class Event(
            val id: Int = 0,
            val name: Map<String, String>
    )

    @ApiModel(value = "Reason", description = "normal, absent ")
    data class Reason(
            val id: Int = 0,
            val name: Map<String, String>
    )

    @ApiModel(value = "Event Reason", description = "not delivered; absent ")
    data class EventReason(
            val event: Event,
            val reason: Reason,
            val usage: EventReasonUsage,
            val longdescription: Map<String, String>,
            val publishLevel: Level,
            val clearingrelevant: Boolean,
            val qualityrelevant: Boolean,
            val stopaction: List<StopAction>
    )

    data class StopAction(
            val messagetext: String,
            val actiontype: ActionType
    )

    enum class ActionType {
        TAKE_IMAGE,
        DISPLAY_TEXT,
        TAKE_NEIGHBOR_NAME
    }

    enum class EventReasonUsage {
        DELIVERED_NORMAL,
        DELIVERED_NEIGHBOR,
        NOT_DELIVERED_NORMAL,
        IMPORT_SCAN
    }

    enum class Level {
        PUBLIC, INTERNAL, SYSTEM
    }
}