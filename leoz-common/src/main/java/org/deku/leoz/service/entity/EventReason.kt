package org.deku.leoz.service.entity

/**
 * Created by 27694066 on 24.05.2017.
 */
/**
 * @param requireAdditionalInformation Should the user enter additional information? e.g. EventReason "Waiting for Customer" is set, the user should enter the amount of "wasted" time.
 * @param requirePhoto Is it required to take a photo of the parcel, after setting this EventReason? e.g. "Parcel damaged"
 * @param deliverable Is it still possible to deliver this order in the current process after setting this EventReason?
 * @param finalEventReason Is the order locked for further processing for today after setting this EventReason? e.g. the customer decline this parcel first, "Customer decline" is set and after that the customer changes his mind. Processing this order again is not possible then, if "Customer decline" is set to "finalEventReason = true"
 * @param descriptionPublic Should the description be shown to the customer (in the signature conclusion)?
 * @param notification A popup dialog with the given message to be shown?
 * @param description Map<languageCode: String, description: String>. The localized descriptive string of this Event/Reason combination.
 */
data class EventReason (
        val event: Event,
        val reason: Reason,
        val requireAdditionalInformation: Boolean,
        val requirePhoto: Boolean,
        val deliverable: Boolean,
        val finalEventReason: Boolean,
        val descriptionPublic: Boolean,
        val notification: String = "",
        val description: Map<String, String>) {

    data class Event (val eventCode: Int, val name: Map<String, String>)

    data class Reason(val reasonCode: Int, val name: Map<String, String>)

}