package org.deku.leoz.mobile.model

import android.content.Context
import android.graphics.Color
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem
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
    fun textOrName(context: Context): String =
            if (this.text != null) context.getString(this.text) else this.value.name
}

private val meta = listOf(
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.ABSENT,
                R.string.event_reason_absent,
                R.drawable.ic_absent
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.REFUSED,
                R.string.event_reason_refuse,
                R.drawable.ic_refused
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.VACATION,
                R.string.event_reason_vacation,
                R.drawable.ic_vacation
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.ADDRESS_WRONG,
                R.string.event_reason_address_wrong,
                R.drawable.ic_wrong_address
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.MOVED,
                R.string.event_reason_moved,
                R.drawable.ic_wrong_address
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.DAMAGED,
                R.string.event_reason_damaged,
                R.drawable.ic_damaged
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.XC_CODE_WRONG,
                R.string.event_reason_xc_code_wrong,
                R.drawable.ic_service_exchange
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.XC_OBJECT_DAMAGED,
                R.string.event_reason_xc_damaged,
                R.drawable.ic_service_exchange
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.XC_OBJECT_NOT_READY,
                R.string.event_reason_xc_not_ready,
                R.drawable.ic_service_exchange
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.XC_OBJECT_WRONG,
                R.string.event_reason_xc_wrong,
                R.drawable.ic_service_exchange
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.SIGNATURE_REFUSED,
                R.string.event_reason_signature_refused,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.NO_PAYMENT,
                R.string.event_reason_could_want_not_pay,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.NO_IDENT,
                R.string.event_reason_no_ident_doc,
                R.drawable.ic_event
        ),
        NotDeliveredReasonMeta(
                EventNotDeliveredReason.PIN_IMEI_WRONG,
                R.string.event_reason_pin_imei_wrong,
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

/**
 * Extension for creating a material dialog list item from an event.
 * The referring event will be start as the list item's tag.
 */
fun EventNotDeliveredReason.toMaterialSimpleListItem(context: Context): MaterialSimpleListItem =
        MaterialSimpleListItem.Builder(context)
                .content(this.mobile.textOrName(context))
                .iconPaddingRes(R.dimen.icon_padding)
                .icon(this.mobile.icon)
                .id(this.reason.id.toLong())
                .backgroundColor(Color.WHITE)
                .tag(this)
                .build()
