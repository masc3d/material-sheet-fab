package org.deku.leoz.mobile.model

import org.deku.leoz.mobile.R
import org.deku.leoz.model.EventNotDeliveredReason

/**
 * Created by phpr on 04.07.2017.
 */
enum class FailureReason(val reason: EventNotDeliveredReason, val stringRes: Int, val iconRes: Int? = null, val listInDelivery: Boolean = true, val listInVehicleLoading: Boolean = false) {
    ABSENT(
            reason = EventNotDeliveredReason.Absend,
            stringRes = R.string.event_reason_absent,
            listInDelivery = true,
            listInVehicleLoading = false
    ),
    REFUSE(
            reason = EventNotDeliveredReason.Refuse,
            stringRes = R.string.event_reason_refuse,
            listInDelivery = true,
            listInVehicleLoading = false
    ),
    VACATION(
            reason = EventNotDeliveredReason.Vacation,
            stringRes = R.string.event_reason_vacation,
            listInDelivery = true,
            listInVehicleLoading = false
    ),
    ADDRESS_WRONG(
            reason = EventNotDeliveredReason.AdressWrong,
            stringRes = R.string.event_reason_address_wrong,
            listInDelivery = true,
            listInVehicleLoading = false
    ),
    MOVED(
            reason = EventNotDeliveredReason.Moved,
            stringRes = R.string.event_reason_moved,
            listInDelivery = true,
            listInVehicleLoading = false
    ),
    UNKNOWN(
            reason = EventNotDeliveredReason.Unknown,
            stringRes = R.string.event_reason_unknown,
            listInDelivery = false,
            listInVehicleLoading = false
    ),
    DAMAGED(
            reason = EventNotDeliveredReason.Damaged,
            stringRes = R.string.event_reason_damaged,
            listInDelivery = true,
            listInVehicleLoading = true
    ),
    XC_CODE_WRONG(
            reason = EventNotDeliveredReason.XC_CodeWrong,
            stringRes = R.string.event_reason_xc_code_wrong,
            listInDelivery = true,
            listInVehicleLoading = false
    ),
    XC_OBJECT_DAMAGED(
            reason = EventNotDeliveredReason.XC_ObjectDamaged,
            stringRes = R.string.event_reason_xc_damaged,
            listInDelivery = true,
            listInVehicleLoading = false
    ),
    XC_OBJECT_WRONG(
            reason = EventNotDeliveredReason.XC_ObjectWrong,
            stringRes = R.string.event_reason_xc_wrong,
            listInDelivery = true,
            listInVehicleLoading = false
    ),
    SIGNATURE_REFUSED(
            reason = EventNotDeliveredReason.SignatureRefused,
            stringRes = R.string.event_reason_signature_refused,
            listInDelivery = false,
            listInVehicleLoading = false
    ),
    COULD_NOT_OR_WANT_NOT_PAY(
            reason = EventNotDeliveredReason.CouldWantNotPay,
            stringRes = R.string.event_reason_could_want_not_pay,
            listInDelivery = true,
            listInVehicleLoading = false
    ),
    IDENT_DOC_NOT_PRESENT(
            reason = EventNotDeliveredReason.IdentDocNotPresent,
            stringRes = R.string.event_reason_no_ident_doc,
            listInDelivery = true,
            listInVehicleLoading = false
    ),
    XC_OBJECT_NOT_READY(
            reason = EventNotDeliveredReason.XC_ObjectNotReady,
            stringRes = R.string.event_reason_xc_not_ready,
            listInDelivery = true,
            listInVehicleLoading = false
    ),
    PIN_IMEI_WRONG(
            reason = EventNotDeliveredReason.PIN_IMEI_Wrong,
            stringRes = R.string.event_reason_pin_imei_wrong,
            listInDelivery = false
    ),
    WAIT_TIME(
            reason = EventNotDeliveredReason.WaitTime,
            stringRes = 0,
            listInDelivery = false,
            listInVehicleLoading = false
    )
}