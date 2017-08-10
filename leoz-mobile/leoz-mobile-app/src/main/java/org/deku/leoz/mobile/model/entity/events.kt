package org.deku.leoz.mobile.model.entity

import android.content.Context
import org.deku.leoz.mobile.R
import org.deku.leoz.model.EventNotDeliveredReason

/** Get event text */
fun Context.getEventText(event: EventNotDeliveredReason): String? {
    return when (event) {
        EventNotDeliveredReason.ABSENT -> this.getString(R.string.event_reason_absent)
        EventNotDeliveredReason.REFUSED -> this.getString(R.string.event_reason_refuse)
        EventNotDeliveredReason.VACATION -> this.getString(R.string.event_reason_vacation)
        EventNotDeliveredReason.ADDRESS_WRONG -> this.getString(R.string.event_reason_address_wrong)
        EventNotDeliveredReason.MOVED -> this.getString(R.string.event_reason_moved)
        EventNotDeliveredReason.DAMAGED -> this.getString(R.string.event_reason_damaged)
        EventNotDeliveredReason.XC_CODE_WRONG -> this.getString(R.string.event_reason_xc_code_wrong)
        EventNotDeliveredReason.XC_OBJECT_DAMAGED -> this.getString(R.string.event_reason_xc_damaged)
        EventNotDeliveredReason.XC_OBJECT_NOT_READY -> this.getString(R.string.event_reason_xc_not_ready)
        EventNotDeliveredReason.XC_OBJECT_WRONG -> this.getString(R.string.event_reason_xc_wrong)
        EventNotDeliveredReason.SIGNATURE_REFUSED -> this.getString(R.string.event_reason_signature_refused)
        EventNotDeliveredReason.NO_PAYMENT -> this.getString(R.string.event_reason_could_want_not_pay)
        EventNotDeliveredReason.NO_IDENT -> this.getString(R.string.event_reason_no_ident_doc)
        EventNotDeliveredReason.PIN_IMEI_WRONG -> this.getString(R.string.event_reason_pin_imei_wrong)
        else -> event.name
    }
}