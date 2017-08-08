package org.deku.leoz.mobile.model.process

import android.content.Context
import org.deku.leoz.mobile.R
import org.deku.leoz.model.ParcelService

/**
 * Mobile service related model types and extensions
 * Created by masc on 08.08.17.
 */

/**
 * Service translations
 */
fun Context.getServiceText(service: ParcelService): String {
    return when (service) {
        ParcelService.SUITCASE_SHIPPING -> getString(R.string.service_suitcase)
        ParcelService.RECEIPT_ACKNOWLEDGEMENT -> getString(R.string.service_receipt_acknowledgement)
        ParcelService.SELF_PICKUP -> getString(R.string.service_selfpickup)
        ParcelService.CASH_ON_DELIVERY -> getString(R.string.service_cash_delivery)
        ParcelService.PHARMACEUTICALS -> getString(R.string.service_pharmaceuticals)
        ParcelService.IDENT_CONTRACT_SERVICE -> getString(R.string.service_ident_contract)
        ParcelService.SUBMISSION_PARTICIPATION -> getString(R.string.service_submission)
        ParcelService.SECURITY_RETURN -> getString(R.string.service_security_return)
        ParcelService.XCHANGE -> getString(R.string.service_xchange)
        ParcelService.PHONE_RECEIPT -> getString(R.string.service_phone_receipt)
        ParcelService.DOCUMENTED_PERSONAL_DELIVERY -> getString(R.string.service_documented_personal)
        ParcelService.DEPARTMENT_DELIVERY -> getString(R.string.service_department_delivery)
        ParcelService.FIXED_APPOINTMENT -> getString(R.string.service_fixed)
        ParcelService.FAIR_SERVICE -> getString(R.string.service_fair)
        ParcelService.SELF_COMPLETION_OF_DUTY_PAYMENT_AND_DOCUMENTS -> getString(R.string.service_self_completion_duty)
        ParcelService.PACKAGING_RECIRCULATION -> getString(R.string.service_packaging_recirculation)
        ParcelService.POSTBOX_DELIVERY -> getString(R.string.service_postbox_delivery)
        ParcelService.NO_ALTERNATIVE_DELIVERY -> getString(R.string.service_no_alternative)
        else -> return ""
    }
}