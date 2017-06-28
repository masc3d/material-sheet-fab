package org.deku.leoz.service.internal

import io.swagger.annotations.*
import sx.io.serialization.Serializable
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

    enum class EventType(val id: Int, val eventType: Class<*>) {
        Delivered(1, EventDelivered::class.java),
        NotDelivered(2, EventNotDelivered::class.java)
    }

    abstract class EventValue<R : Enum<R>>(
            val type: EventType,
            val reason: R
    )

    enum class EventDeliveredReason(val id: Int) {
        Normal(1),
        Neighbor(2),                // nur wenn SKZ 536870912 (Keine Alternativzustellung) nicht gesetzt
        Postbox(3)                  // nur wenn SKZ 268435456 (Briefkastenzustellung möglich) gesetzt
    }

    enum class EventNotDeliveredReason(val id: Int) {
        Absend(500),
        Refuse(503),                // Text : Wer? / Warum?
        Vacation(504),              // Text : Wie lange?
        AdressWrong(507),           // Text : Richtige Adresse?
        Moved(509),                 // Text : Neue Adresse?
        Unknown(510),
        Damaged(513),               // Packstück bleibt in der Auswahl
        XC_CodeWrong(516),
        XC_ObjectDamaged(517),
        XC_ObjectWrong(518),
        SignatureRefused(519),      // Text : Warum?
        CouldWantNotPay(521),
        IdentDocNotPresent(524),
        XC_ObjectNotReady(529),
        PIN_IMEI_Wrong(532),
        WaitTime(555)               // Text : Wartezeit (min)?   // Packstück bleibt in der Auswahl   // Unterschrift nach eingabe anfordern  // Kyboard auf Num stellen
    }

    class EventDelivered(reason: EventDeliveredReason)
        : EventValue<EventDeliveredReason>(
            type = EventType.Delivered,
            reason = reason
    )

    class EventNotDelivered(reason: EventNotDeliveredReason
    ) : EventValue<EventNotDeliveredReason>(
            type = EventType.NotDelivered,
            reason = reason
    )

//    val event = EventType.Delivered.reasonType.enumConstants


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