package org.deku.leoz.mobile.model

import org.deku.leoz.enums.ParcelService
import java.util.*

/**
 * Created by 27694066 on 10.05.2017.
 */
class Stop (
        val order: Order,
        val address: Order.Address,
        val appointment: Date
) {

    //Nested class Order
    class Order (
            val classification: OrderClassification,
            val parcel: MutableList<Parcel> = mutableListOf(),
            val adresses: MutableList<Address>,
            val appointment: Date,
            val carrier: Carrier,
            val service: MutableList<ParcelService>,
            val additionalInformation: MutableList<AdditionalInformation> = mutableListOf()
    ) {
        enum class OrderClassification {
            PICKUP, DELIVERY, EXCHANGE_DELIVERY, EXCHANGE_PICKUP
        }

        data class Parcel (
                val labelReference: String?,
                val status: MutableList<Status>? = mutableListOf(),
                val dimensions: Dimension?
        )

        enum class Carrier {
            DERKURIER
        }

        class Address (val classification: AddressClassification, val addressLine1: String, val addressLineNo1: String = "", val zipCode: String, val city: String, val geoLocation: Pair<Double, Double>?) {
            enum class AddressClassification {
                PICKUP, DELIVERY, EXCHANGE
            }
        }

        class AdditionalInformation (type: AdditionalInformationType, information: String) {
            enum class AdditionalInformationType {
                IMEI, IDENTITYCARDID, LOADINGLISTINFORMATION
            }
        }

        class Status {

        }

        data class Dimension (val length: Double, val height: Double, val width: Double, val weight: Double)
    }
}