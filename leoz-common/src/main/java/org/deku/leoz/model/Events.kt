package org.deku.leoz.model

/**
 * Event types
 */
enum class EventType(val id: Int, val reasonType: Class<*>) {
    Delivered(id = 1, reasonType = EventDeliveredReason::class.java),
    NotDelivered(id = 2, reasonType = EventNotDeliveredReason::class.java),
    Loaded(id = 3, reasonType = EventLoadedReason::class.java)
}

/**
 * Event value, consists of an event type and referring reason enum value
 */
abstract class EventValue<R : Enum<R>>(
        val event: EventType = EventType.valueOf(value = ""),
        val reason: R? = null

)

// Event reasons
enum class EventLoadedReason(val id: Int) {
    DeliveryTour(1),
    HUB(2),
    SmallSort(3)
}


enum class EventDeliveredReason(val reason: Reason) {
    Normal(Reason.NORMAL), // noticeRecipient  +unterschrift
    Neighbor(Reason.NEIGHBOUR), // noticeNeighbour + 1 zeile Adresse  + unterschrift    nur wenn SKZ 536870912 (Keine Alternativzustellung) nicht gesetzt
    Postbox(Reason.POSTBOX)    // kein text + Foto             // nur wenn SKZ 268435456 (Briefkastenzustellung möglich) gesetzt
}

enum class EventNotDeliveredReason(val reason: Reason) {
    Absent(Reason.CUSTOMER_ABSENT),
    Refuse(Reason.CUSTOMER_REFUSED), // 1 Text :                      Wer? / Warum?
    Vacation(Reason.CUSTOMER_VACATION), //  1Text + shortdate         : Wie lange?
    AddressWrong(Reason.ADDRESS_WRONG), // Text               : Richtige Adresse?
    Moved(Reason.CUSTOMER_MOVED), // Text                       : Neue Adresse?
    //TODO Unknown(510),
    Damaged(Reason.PARCEL_DAMAGED), //  Foto + text          Packstück bleibt in der Auswahl
    XC_CodeWrong(Reason.EXCHANGE_CODE_CHECK_FAILED),
    XC_ObjectDamaged(Reason.EXCHANGE_OBJECT_DAMAGED),
    XC_ObjectWrong(Reason.EXCHANGE_OBJECT_WRONG),
    SignatureRefused(Reason.SIGNATURE_REFUSED), // Text : Warum?
    CouldWantNotPay(Reason.CUSTOMER_DID_OR_COULD_NOT_PAY),
    IdentDocNotPresent(Reason.IDENT_DOCUMENT_NOT_THERE),
    XC_ObjectNotReady(Reason.EXCHANGE_OBJECT_NOT_READY),
    PIN_IMEI_Wrong(Reason.PIN_IMEI_CHECK_FAILED)
    //TODO WaitTime(555)               // Text : Wartezeit (min)?   // Packstück bleibt in der Auswahl   // Unterschrift nach eingabe anfordern  // Kyboard auf Num stellen
}

// Events

/**
 * Delivered event
 */
class EventDelivered(
        reason: EventDeliveredReason
) : EventValue<EventDeliveredReason>(
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

/**
 * TODO: Just a draft/experiment (PHPR)
 */
class EventReasonAssosiation {
    val eventReasonMap = mapOf(
            Pair(
                    Event.DELIVERED,
                    listOf(
                            Reason.NORMAL,
                            Reason.NEIGHBOUR,
                            Reason.POSTBOX
                    )
            ),

            Pair(
                    Event.DELIVERY_FAIL,
                    listOf(
                            Reason.CUSTOMER_ABSENT,
                            Reason.CUSTOMER_REFUSED, // 1 Text :                      Wer? / Warum?
                            Reason.CUSTOMER_VACATION, //  1Text + shortdate         : Wie lange?
                            Reason.ADDRESS_WRONG, // Text               : Richtige Adresse?
                            Reason.CUSTOMER_MOVED, // Text                       : Neue Adresse?
                            //TODO Unknown(510),
                            Reason.PARCEL_DAMAGED, //  Foto + text          Packstück bleibt in der Auswahl
                            Reason.EXCHANGE_CODE_CHECK_FAILED,
                            Reason.EXCHANGE_OBJECT_DAMAGED,
                            Reason.EXCHANGE_OBJECT_WRONG,
                            Reason.SIGNATURE_REFUSED, // Text : Warum?
                            Reason.CUSTOMER_DID_OR_COULD_NOT_PAY,
                            Reason.IDENT_DOCUMENT_NOT_THERE,
                            Reason.EXCHANGE_OBJECT_NOT_READY,
                            Reason.PIN_IMEI_CHECK_FAILED
                    )
            )
    )
}