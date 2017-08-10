package org.deku.leoz.mobile.model.process

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
 * Helper extension for creating parcel service meta pair
 */
private fun ParcelService.createMetaPair(
        @StringRes text: Int,
        @DrawableRes icon: Int
): Pair<ParcelService, ParcelServiceMeta> {
    return Pair(this, ParcelServiceMeta(
            value = this,
            text = text,
            icon = icon
    ))
}

/**
 * Service meta lookup map
 */
private val metaByService = mapOf<ParcelService, ParcelServiceMeta>(
        ParcelService.SUITCASE_SHIPPING.createMetaPair(
                R.string.service_suitcase,
                R.drawable.ic_service
        ),
        ParcelService.RECEIPT_ACKNOWLEDGEMENT.createMetaPair(
                R.string.service_receipt_acknowledgement,
                R.drawable.ic_service_confirmation
        ),
        ParcelService.SELF_PICKUP.createMetaPair(
                R.string.service_selfpickup,
                R.drawable.ic_service
        ),
        ParcelService.CASH_ON_DELIVERY.createMetaPair(
                R.string.service_cash_delivery,
                R.drawable.ic_service_cash
        ),
        ParcelService.PHARMACEUTICALS.createMetaPair(
                R.string.service_pharmaceuticals,
                R.drawable.ic_service_pharma
        ),
        ParcelService.IDENT_CONTRACT_SERVICE.createMetaPair(
                R.string.service_ident_contract,
                R.drawable.ic_service_ident
        ),
        ParcelService.SUBMISSION_PARTICIPATION.createMetaPair(
                R.string.service_submission,
                R.drawable.ic_service
        ),
        ParcelService.SECURITY_RETURN.createMetaPair(
                R.string.service_security_return,
                R.drawable.ic_service_security_return
        ),
        ParcelService.XCHANGE.createMetaPair(
                R.string.service_xchange,
                R.drawable.ic_service_exchange
        ),
        ParcelService.PHONE_RECEIPT.createMetaPair(
                R.string.service_phone_receipt,
                R.drawable.ic_service_phone
        ),
        ParcelService.DOCUMENTED_PERSONAL_DELIVERY.createMetaPair(
                R.string.service_documented_personal,
                R.drawable.ic_service_personal_document
        ),
        ParcelService.DEPARTMENT_DELIVERY.createMetaPair(
                R.string.service_department_delivery,
                R.drawable.ic_service_department_delivery
        ),
        ParcelService.FIXED_APPOINTMENT.createMetaPair(
                R.string.service_fixed,
                R.drawable.ic_service
        ),
        ParcelService.FAIR_SERVICE.createMetaPair(
                R.string.service_fair,
                R.drawable.ic_service_trade_fair
        ),
        ParcelService.SELF_COMPLETION_OF_DUTY_PAYMENT_AND_DOCUMENTS.createMetaPair(
                R.string.service_self_completion_duty,
                R.drawable.ic_service
        ),
        ParcelService.PACKAGING_RECIRCULATION.createMetaPair(
                R.string.service_packaging_recirculation,
                R.drawable.ic_service_packaging
        ),
        ParcelService.POSTBOX_DELIVERY.createMetaPair(
                R.string.service_postbox_delivery,
                R.drawable.ic_service_postbox
        ),
        ParcelService.NO_ALTERNATIVE_DELIVERY.createMetaPair(
                R.string.service_no_alternative,
                R.drawable.ic_service_no_alternative_delivery
        )
)

/**
 * Extension property to provide mobile meta structure
 */
val ParcelService.mobile: ParcelServiceMeta
    get() = metaByService.withDefault { ParcelServiceMeta(this, null, R.drawable.ic_service) }.getValue(this)

