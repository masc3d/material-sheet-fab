package org.deku.leoz.mobile.model.entity

import android.content.Context
import org.deku.leoz.mobile.R
import org.deku.leoz.model.EventNotDeliveredReason

/** Get event text */
fun Context.getEventText(event: EventNotDeliveredReason): String? {
    return when (event) {
        EventNotDeliveredReason.Absent -> this.getString(R.string.event_reason_absent)
        EventNotDeliveredReason.Refuse -> this.getString(R.string.event_reason_refuse)
        EventNotDeliveredReason.Vacation -> this.getString(R.string.event_reason_vacation)
        EventNotDeliveredReason.AddressWrong -> this.getString(R.string.event_reason_address_wrong)
        EventNotDeliveredReason.Moved -> this.getString(R.string.event_reason_moved)
        EventNotDeliveredReason.Damaged -> this.getString(R.string.event_reason_damaged)
        EventNotDeliveredReason.XC_CodeWrong -> this.getString(R.string.event_reason_xc_code_wrong)
        EventNotDeliveredReason.XC_ObjectDamaged -> this.getString(R.string.event_reason_xc_damaged)
        EventNotDeliveredReason.XC_ObjectNotReady -> this.getString(R.string.event_reason_xc_not_ready)
        EventNotDeliveredReason.XC_ObjectWrong -> this.getString(R.string.event_reason_xc_wrong)
        EventNotDeliveredReason.SignatureRefused -> this.getString(R.string.event_reason_signature_refused)
        EventNotDeliveredReason.CouldWantNotPay -> this.getString(R.string.event_reason_could_want_not_pay)
        EventNotDeliveredReason.IdentDocNotPresent -> this.getString(R.string.event_reason_no_ident_doc)
        EventNotDeliveredReason.PIN_IMEI_Wrong -> this.getString(R.string.event_reason_pin_imei_wrong)
        else -> event.name
    }
}