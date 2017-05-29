package org.deku.leoz.model

/**
 * Created by 27694066 on 17.05.2017.
 * Configure/Map delivery restrictions and particularities for the delivery/pickup process depending on service
 */
data class ParcelServiceRestriction(

        //Default properties for a standard delivery job

        val alternateDeliveryAllowed: Boolean = true,
        val personalDeliveryOnly: Boolean = false,
        val paperReceiptNeeded: Boolean = false,
        val identityCheckRequired: Boolean = false,
        val imeiCheckRequired: Boolean = false,
        val cash: Boolean = false
) {

}