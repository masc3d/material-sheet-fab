package org.deku.leoz.model

/**
 * Event types
 */
enum class EventType(val id: Int, val reasonType: Class<*>) {
    DELIVERED(id = 1, reasonType = EventDeliveredReason::class.java),
    NOT_DELIVERED(id = 2, reasonType = EventNotDeliveredReason::class.java),
    LOADED(id = 3, reasonType = EventLoadedReason::class.java)
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
    DELIVERY_TOUR(1),
    HUB(2),
    SMALL_SORT(3)
}

enum class EventDeliveredReason(val reason: Reason) {
    NORMAL(Reason.NORMAL), // noticeRecipient  +unterschrift
    NEIGHBOR(Reason.NEIGHBOUR), // noticeNeighbour + 1 zeile Adresse  + unterschrift    nur wenn SKZ 536870912 (Keine Alternativzustellung) nicht gesetzt
    POSTBOX(Reason.POSTBOX)    // kein text + Foto             // nur wenn SKZ 268435456 (Briefkastenzustellung möglich) gesetzt
}

enum class EventNotDeliveredReason(val reason: Reason) {
    ABSENT(Reason.CUSTOMER_ABSENT),
    REFUSED(Reason.CUSTOMER_REFUSED), // 1 Text :                      Wer? / Warum?
    VACATION(Reason.CUSTOMER_VACATION), //  1Text + shortdate         : Wie lange?
    ADDRESS_WRONG(Reason.ADDRESS_WRONG), // Text               : Richtige Adresse?
    MOVED(Reason.CUSTOMER_MOVED), // Text                       : Neue Adresse?
    //TODO Unknown(510),
    DAMAGED(Reason.PARCEL_DAMAGED), //  Foto + text          Packstück bleibt in der Auswahl
    XC_CODE_WRONG(Reason.EXCHANGE_CODE_CHECK_FAILED),
    XC_OBJECT_DAMAGED(Reason.EXCHANGE_OBJECT_DAMAGED),
    XC_OBJECT_WRONG(Reason.EXCHANGE_OBJECT_WRONG),
    SIGNATURE_REFUSED(Reason.SIGNATURE_REFUSED), // Text : Warum?
    NO_PAYMENT(Reason.CUSTOMER_DID_OR_COULD_NOT_PAY),
    NO_IDENT(Reason.IDENT_DOCUMENT_NOT_THERE),
    XC_OBJECT_NOT_READY(Reason.EXCHANGE_OBJECT_NOT_READY),
    PIN_IMEI_WRONG(Reason.PIN_IMEI_CHECK_FAILED)
    //TODO WaitTime(555)               // Text : Wartezeit (min)?   // Packstück bleibt in der Auswahl   // Unterschrift nach eingabe anfordern  // Kyboard auf Num stellen
}

// Events

/**
 * Delivered event
 */
class EventDelivered(
        reason: EventDeliveredReason
) : EventValue<EventDeliveredReason>(
        event = EventType.DELIVERED,
        reason = reason
)

/**
 * Not delivered event
 */
class EventNotDelivered(reason: EventNotDeliveredReason
) : EventValue<EventNotDeliveredReason>(
        event = EventType.NOT_DELIVERED,
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

sealed class AdditionalInfo {
    object EmptyInfo : AdditionalInfo()

    data class WeightCorrectionInfo(
            val length: Int = 0,
            val width: Int = 0,
            val height: Int = 0,
            val weight: Double = 0.0) : AdditionalInfo()

    data class LoadingListInfo(
            val loadingListNo: Long = 0) : AdditionalInfo()

    data class DeliveredInfo(
            val recipient: String? = null,
            val signature: String? = null,
            val mimetype: String = "svg"
    ) : AdditionalInfo()

    data class DeliveredAtNeighborInfo(
            val name: String? = null,
            val address: String? = null,
            val signature: String? = null,
            val mimetype: String = "svg"
    ) : AdditionalInfo()

    data class NotDeliveredRefusedInfo(
            val cause: String? = null
    ) : AdditionalInfo()

    data class NotDeliveredInfo(
            val text: String? = null
    ) : AdditionalInfo()

    data class DamagedInfo(
            val description: String? = null,
            val photo: String? = null,
            val mimetype: String = "jpg"
    ) : AdditionalInfo()
}