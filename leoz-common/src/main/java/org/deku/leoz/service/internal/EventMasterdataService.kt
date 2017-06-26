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
    @ApiModel(value = "Event", description = "i.e.: EN delivered | EN not delivered")
    data class Event(
            val id: Int = 0, // evtl nicht nötig
            val name: List<EventName>
    )

    @ApiModel(value = "Reason", description = "i.e.: not delivered: EN absent ")
    data class Reason(
            val id: Int = 0, // evtl nicht nötig
            val name: List<ReasonName>
    )

    data class EventReason(
            val event: Event,
            val reason: Reason,
            val usage: EventUsage,
            val description: Map<String, String>,
            val publishLevel: Level
    )

    data class EventName(
            val language: String,
            val name: String
    )

    data class ReasonName(
            val language: String,
            val name: String
    )

    enum class EventUsage {
        DELIVERED,
        NOT_DELIVERED,
        IMPORT_SCAN
    }

    enum class Level {
        PUBLIC, INTERNAL, SYSTEM
    }
}