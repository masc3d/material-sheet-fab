package org.deku.leoz.mobile.model

import org.deku.leoz.enums.ParcelService
import java.util.*

/**
 * Created by 27694066 on 10.05.2017.
 */
class Stop (
        val order: MutableList<Order>,
        val address: Order.Address,
        var appointment: String,
        var sort: Int
) {

    //Nested class Order
    class Order (
            val id: String,
            val classification: OrderClassification,
            val parcel: MutableList<Parcel> = mutableListOf(),
            val addresses: MutableList<Address>,
            val appointment: String,
            val carrier: Carrier,
            val service: MutableList<ParcelService>,
            val additionalInformation: MutableList<AdditionalInformation> = mutableListOf(),
            val sort: Int
    ) {
        enum class OrderClassification {
            PICKUP, DELIVERY, EXCHANGE_DELIVERY, EXCHANGE_PICKUP, DIRECT_PICKUP, DIRECT_DELIVERY
        }

        data class Parcel (
                val id: String,
                val labelReference: String?,
                val status: MutableList<Status>? = mutableListOf(),
                val dimensions: Dimension?
        )

        enum class Carrier {
            DERKURIER
        }

        fun equalsAddress(order: Order): Boolean {
            var adrType: Address.AddressClassification? = null

            if (this.classification != order.classification) {
                throw IllegalArgumentException()
            }



            val address1: Address = order.addresses.first {
                it.classification == adrType
            }

            val address2: Address = this.addresses.first {
                it.classification == adrType
            }

            return address1 == address2
        }

        fun getAddressOfInterest(): Address {
            var adrType: Address.AddressClassification? = null

            when {
                this.classification == OrderClassification.DELIVERY -> adrType = Address.AddressClassification.DELIVERY
                this.classification == OrderClassification.PICKUP -> adrType = Address.AddressClassification.PICKUP
            }

            val address: Address = this.addresses.first {
                it.classification == adrType
            }

            return address
        }

        /**
         * @param stopList The stopList this method should use to iterate
         * @return If a existing stop has been found, the stop is returned. If not, it will be null
         */
        fun findSuitableStop(stopList: MutableList<Stop>): Stop? {
//            return stopList.firstOrNull {
//                it.address.equals(this.getAddressOfInterest())
//            }

            return if (findSuitableStopIndex(stopList) == -1) null else stopList[findSuitableStopIndex(stopList)]
        }


        fun findSuitableStopIndex(stopList: MutableList<Stop>): Int {
            return stopList.indexOfFirst {
                it.address.equals(this.getAddressOfInterest())
            }
        }

        class Address (val classification: AddressClassification, val addressLine1: String, val addressLineNo1: String = "", val addressLine2: String = "", val addressLineNo2: String = "", val street: String, val streetNo: String = "", val zipCode: String, val city: String, val geoLocation: Pair<Double, Double>?) {
            enum class AddressClassification {
                PICKUP, DELIVERY, EXCHANGE_DELIVERY, EXCHANGE_PICKUP
            }
        }

        data class AdditionalInformation (val type: AdditionalInformationType, val information: String) {
            enum class AdditionalInformationType {
                IMEI, IDENTITYCARDID, LOADINGLISTINFORMATION
            }
        }

        data class Status (val event: Long, val reason: Long, val date: Date, val geoLocation: Pair<Double, Double>, val recipient: String?, val information: String?) {

        }

        data class Dimension (val length: Double, val height: Double, val width: Double, val weight: Double)
    }
}