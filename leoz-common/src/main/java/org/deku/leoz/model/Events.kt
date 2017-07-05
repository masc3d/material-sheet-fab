package org.deku.leoz.model

/**
 * Event types
 */
enum class EventType(val id: Int, val reasonType: Class<*>) {
    Delivered(id = 1, reasonType = EventDeliveredReason::class.java),
    NotDelivered(id = 2, reasonType = EventNotDeliveredReason::class.java)
}

/**
 * Event value, consists of an event type and referring reason enum value
 */
abstract class EventValue<R : Enum<R>>(
        val event: EventType,
        val reason: R
)

// Event reasons

enum class EventDeliveredReason(val id: Int) {
    Normal(1),
    Neighbor(2), // nur wenn SKZ 536870912 (Keine Alternativzustellung) nicht gesetzt
    Postbox(3)                  // nur wenn SKZ 268435456 (Briefkastenzustellung möglich) gesetzt
}

enum class EventNotDeliveredReason(val id: Int) {
    Absent(500),
    Refuse(503), // Text : Wer? / Warum?
    Vacation(504), // Text : Wie lange?
    AddressWrong(507), // Text : Richtige Adresse?
    Moved(509), // Text : Neue Adresse?
    Unknown(510),
    Damaged(513), // Packstück bleibt in der Auswahl
    XC_CodeWrong(516),
    XC_ObjectDamaged(517),
    XC_ObjectWrong(518),
    SignatureRefused(519), // Text : Warum?
    CouldWantNotPay(521),
    IdentDocNotPresent(524),
    XC_ObjectNotReady(529),
    PIN_IMEI_Wrong(532),
    WaitTime(555)               // Text : Wartezeit (min)?   // Packstück bleibt in der Auswahl   // Unterschrift nach eingabe anfordern  // Kyboard auf Num stellen
}

// Events

/**
 * Delivered event
 */
class EventDelivered(reason: EventDeliveredReason)
    : EventValue<EventDeliveredReason>(
        event = EventType.Delivered,
        reason = reason
)

/**
 * Not delivered event
 */
class EventNotDelivered(reason: EventNotDeliveredReason
) : EventValue<EventNotDeliveredReason>(
        event = EventType.NotDelivered,
        reason = reason
)