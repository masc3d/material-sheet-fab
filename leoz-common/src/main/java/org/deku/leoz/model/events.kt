package org.deku.leoz.model

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