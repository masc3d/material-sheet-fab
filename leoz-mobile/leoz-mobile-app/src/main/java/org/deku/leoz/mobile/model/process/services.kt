package org.deku.leoz.mobile.model.process

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import org.deku.leoz.mobile.R
import org.deku.leoz.model.ParcelService

/**
 * Mobile specific parcel service meta info
 */
data class ParcelServiceMeta(
        @StringRes val text: Int,
        @DrawableRes val icon: Int
)

/**
 * Service meta lookup map
 */
private val metaByService = mapOf<ParcelService, ParcelServiceMeta>(
        Pair(ParcelService.SUITCASE_SHIPPING, ParcelServiceMeta(
                R.string.service_suitcase,
                R.drawable.ic_service)
        ),
        Pair(ParcelService.RECEIPT_ACKNOWLEDGEMENT, ParcelServiceMeta(
                R.string.service_receipt_acknowledgement,
                R.drawable.ic_service_confirmation)
        ),
        Pair(ParcelService.SELF_PICKUP, ParcelServiceMeta(
                R.string.service_selfpickup,
                R.drawable.ic_service)
        ),
        Pair(ParcelService.CASH_ON_DELIVERY, ParcelServiceMeta(
                R.string.service_cash_delivery,
                R.drawable.ic_service_cash)
        ),
        Pair(ParcelService.PHARMACEUTICALS, ParcelServiceMeta(
                R.string.service_pharmaceuticals,
                R.drawable.ic_service_pharma)
        ),
        Pair(ParcelService.IDENT_CONTRACT_SERVICE, ParcelServiceMeta(
                R.string.service_ident_contract,
                R.drawable.ic_service_ident)
        ),
        Pair(ParcelService.SUBMISSION_PARTICIPATION, ParcelServiceMeta(
                R.string.service_submission,
                R.drawable.ic_service)
        ),
        Pair(ParcelService.SECURITY_RETURN, ParcelServiceMeta(
                R.string.service_security_return,
                R.drawable.ic_service_security_return)
        ),
        Pair(ParcelService.XCHANGE, ParcelServiceMeta(
                R.string.service_xchange,
                R.drawable.ic_service_exchange)
        ),
        Pair(ParcelService.PHONE_RECEIPT, ParcelServiceMeta(
                R.string.service_phone_receipt,
                R.drawable.ic_service_phone)
        ),
        Pair(ParcelService.DOCUMENTED_PERSONAL_DELIVERY, ParcelServiceMeta(
                R.string.service_documented_personal,
                R.drawable.ic_service_personal_document)
        ),
        Pair(ParcelService.DEPARTMENT_DELIVERY, ParcelServiceMeta(
                R.string.service_department_delivery,
                R.drawable.ic_service_department_delivery)
        ),
        Pair(ParcelService.FIXED_APPOINTMENT, ParcelServiceMeta(
                R.string.service_fixed,
                R.drawable.ic_service)
        ),
        Pair(ParcelService.FAIR_SERVICE, ParcelServiceMeta(
                R.string.service_fair,
                R.drawable.ic_service_trade_fair)
        ),
        Pair(ParcelService.SELF_COMPLETION_OF_DUTY_PAYMENT_AND_DOCUMENTS, ParcelServiceMeta(
                R.string.service_self_completion_duty,
                R.drawable.ic_service)
        ),
        Pair(ParcelService.PACKAGING_RECIRCULATION, ParcelServiceMeta(
                R.string.service_packaging_recirculation,
                R.drawable.ic_service_packaging)
        ),
        Pair(ParcelService.POSTBOX_DELIVERY, ParcelServiceMeta(
                R.string.service_postbox_delivery,
                R.drawable.ic_service_postbox)
        ),
        Pair(ParcelService.NO_ALTERNATIVE_DELIVERY, ParcelServiceMeta(
                R.string.service_no_alternative,
                R.drawable.ic_service_no_alternative_delivery)
        )
)

/**
 * Extension property to provide mobile meta structure
 */
val ParcelService.mobile: ParcelServiceMeta
    get() = metaByService.withDefault { ParcelServiceMeta(R.string.unknown, R.drawable.ic_service) }.getValue(this)

