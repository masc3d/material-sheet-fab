package org.deku.leoz.mobile.model

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import org.deku.leoz.mobile.R
import org.deku.leoz.model.ParcelService

/**
 * Mobile specific parcel service meta info
 */
data class ParcelServiceMeta(
        val value: ParcelService,
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

/**
 * Service meta lookup map
 */
private val meta = listOf(
        ParcelServiceMeta(
                ParcelService.SUITCASE_SHIPPING,
                R.string.service_suitcase,
                R.drawable.ic_service
        ),
        ParcelServiceMeta(
                ParcelService.RECEIPT_ACKNOWLEDGEMENT,
                R.string.service_receipt_acknowledgement,
                R.drawable.ic_service_confirmation
        ),
        ParcelServiceMeta(
                ParcelService.SELF_PICKUP,
                R.string.service_selfpickup,
                R.drawable.ic_service
        ),
        ParcelServiceMeta(
                ParcelService.CASH_ON_DELIVERY,
                R.string.service_cash_delivery,
                R.drawable.ic_service_cash
        ),
        ParcelServiceMeta(
                ParcelService.PHARMACEUTICALS,
                R.string.service_pharmaceuticals,
                R.drawable.ic_service_pharma
        ),
        ParcelServiceMeta(
                ParcelService.IDENT_CONTRACT_SERVICE,
                R.string.service_ident_contract,
                R.drawable.ic_service_ident
        ),
        ParcelServiceMeta(ParcelService.SUBMISSION_PARTICIPATION,
                R.string.service_submission,
                R.drawable.ic_service
        ),
        ParcelServiceMeta(
                ParcelService.SECURITY_RETURN,
                R.string.service_security_return,
                R.drawable.ic_service_security_return
        ),
        ParcelServiceMeta(
                ParcelService.XCHANGE,
                R.string.service_xchange,
                R.drawable.ic_service_exchange
        ),
        ParcelServiceMeta(
                ParcelService.PHONE_RECEIPT,
                R.string.service_phone_receipt,
                R.drawable.ic_service_phone
        ),
        ParcelServiceMeta(
                ParcelService.DOCUMENTED_PERSONAL_DELIVERY,
                R.string.service_documented_personal,
                R.drawable.ic_service_personal_document
        ),
        ParcelServiceMeta(
                ParcelService.DEPARTMENT_DELIVERY,
                R.string.service_department_delivery,
                R.drawable.ic_service_department_delivery
        ),
        ParcelServiceMeta(
                ParcelService.FIXED_APPOINTMENT,
                R.string.service_fixed,
                R.drawable.ic_service
        ),
        ParcelServiceMeta(
                ParcelService.FAIR_SERVICE,
                R.string.service_fair,
                R.drawable.ic_service_trade_fair
        ),
        ParcelServiceMeta(
                ParcelService.SELF_COMPLETION_OF_DUTY_PAYMENT_AND_DOCUMENTS,
                R.string.service_self_completion_duty,
                R.drawable.ic_service
        ),
        ParcelServiceMeta(
                ParcelService.PACKAGING_RECIRCULATION,
                R.string.service_packaging_recirculation,
                R.drawable.ic_service_packaging
        ),
        ParcelServiceMeta(
                ParcelService.POSTBOX_DELIVERY,
                R.string.service_postbox_delivery,
                R.drawable.ic_service_postbox
        ),
        ParcelServiceMeta(
                ParcelService.NO_ALTERNATIVE_DELIVERY,
                R.string.service_no_alternative,
                R.drawable.ic_service_no_alternative_delivery
        )
)

/**
 * Event meta lookup by reason
 */
private val metaByService = mapOf(*meta.map { Pair(it.value, it) }.toTypedArray())

/**
 * Extension property to provide mobile meta structure
 */
val ParcelService.mobile: ParcelServiceMeta
    get() = metaByService.withDefault { ParcelServiceMeta(this, null, R.drawable.ic_service) }.getValue(this)
