package org.deku.leoz.mobile.model

import org.deku.leoz.model.OrderClassification
import org.deku.leoz.model.ParcelService
import org.deku.leoz.service.internal.entity.Order
import java.util.*
/**
 * Created by 27694066 on 10.05.2017.
 */
class Stop (
        val order: MutableList<Order>,
        val address: Order.Address,
        var appointment: org.deku.leoz.service.internal.entity.Order.Appointment,
        var sort: Int,
        val status: StopStatus = Stop.StopStatus.PENDING
) {

    enum class StopStatus {
        PENDING, DONE, FAILED
    }

    //Nested class Order
    class Order (
            val id: String,
            val classification: org.deku.leoz.model.OrderClassification,
            val parcel: List<Parcel> = mutableListOf(),
            val addresses: MutableList<Address>,
            val appointment: Map<OrderClassification, org.deku.leoz.service.internal.entity.Order.Appointment>,
            val carrier: org.deku.leoz.model.Carrier,
            val service: Map<OrderClassification, List<ParcelService>>,
            val additionalInformation: Map<OrderClassification, List<org.deku.leoz.service.internal.entity.Order.Information>>? = null,
            val sort: Int
    ) {
        data class Parcel (
                val id: String,
                val labelReference: String?,
                val status: MutableList<Status>? = mutableListOf(),
                val dimensions: org.deku.leoz.service.internal.entity.Order.Parcel.ParcelDimension?
        )

        fun equalsAddress(order: Order): Boolean {
            val adrType: Address.AddressClassification? = null

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
                this.classification == OrderClassification.Delivery -> adrType = Address.AddressClassification.DELIVERY
                this.classification == OrderClassification.PickUp -> adrType = Address.AddressClassification.PICKUP
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
                it.address == this.getAddressOfInterest()
            }
        }

        class Address (
                val classification: AddressClassification,
                val addressLine1: String,
                val addressLine2: String = "",
                val street: String,
                val streetNo: String = "",
                val zipCode: String,
                val city: String,
                val geoLocation: org.deku.leoz.service.internal.entity.Order.Address.GeoLocation?
        ) {
            enum class AddressClassification {
                PICKUP, DELIVERY, EXCHANGE_DELIVERY, EXCHANGE_PICKUP
            }
        }

        data class Status (val event: Long, val reason: Long, val date: Date, val geoLocation: Pair<Double, Double>, val recipient: String?, val information: String?)

        //data class Dimension (val length: Double, val height: Double, val width: Double, val weight: Double)
    }
}