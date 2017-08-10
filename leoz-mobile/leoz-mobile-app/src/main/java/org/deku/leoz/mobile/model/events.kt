package org.deku.leoz.mobile.model

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import org.deku.leoz.mobile.R
import org.deku.leoz.model.EventNotDeliveredReason

/**
 * Mobile specific event reason meta info
 */
data class NotDeliveredReasonMeta(
        val value: EventNotDeliveredReason,
        @StringRes val text: Int?,
        @DrawableRes val icon: Int
) {
    /**
     * Returns mobile translation and for unknown values the enum value name
     */
    fun textOrName(context: Context): String {
        return if (this.text != null) context.getString(this.text) else this.value.name
    }
}

private val meta = listOf(
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.ABSENT,
                R.string.event_reason_absent,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.REFUSED,
                R.string.service_receipt_acknowledgement,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.VACATION,
                R.string.service_selfpickup,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.ADDRESS_WRONG,
                R.string.service_cash_delivery,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.MOVED,
                R.string.service_pharmaceuticals,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.DAMAGED,
                R.string.service_ident_contract,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.XC_CODE_WRONG,
                R.string.service_submission,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.XC_OBJECT_DAMAGED,
                R.string.service_security_return,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.XC_OBJECT_NOT_READY,
                R.string.service_xchange,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.XC_OBJECT_WRONG,
                R.string.service_phone_receipt,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.SIGNATURE_REFUSED,
                R.string.service_documented_personal,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.NO_PAYMENT,
                R.string.service_department_delivery,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.NO_IDENT,
                R.string.service_fixed,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.PIN_IMEI_WRONG,
                R.string.service_fair,
                R.drawable.ic_event
        )
)

/**
 * Event meta lookup by reason
 */
private val metaByReason = mapOf(*meta.map { Pair(it.value, it) }.toTypedArray())

/**
 * Extension property to provide mobile meta structure
 */
val EventNotDeliveredReason.mobile: NotDeliveredReasonMeta
    get() = metaByReason.withDefault { NotDeliveredReasonMeta(this, null, R.drawable.ic_event) }.getValue(this)

