package org.deku.leoz.model

/**
 * Created by 27694066 on 17.05.2017.
 * Configure/Map delivery restrictions and particularities for the delivery/pickup process depending on service
 * The defaults reflect a standard delivery job
 */
data class ParcelServiceRestriction(
        val alternateDeliveryAllowed: Boolean = true,
        val personalDeliveryOnly: Boolean = false,
        val paperReceiptNeeded: Boolean = false,
        val identityCheckRequired: Boolean = false,
        val imeiCheckRequired: Boolean = false,
        val cash: Boolean = false,
        val summarizedDeliveryAllowed: Boolean = true
)