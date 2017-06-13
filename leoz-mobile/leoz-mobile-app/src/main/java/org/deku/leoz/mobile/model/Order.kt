package org.deku.leoz.mobile.model

import org.deku.leoz.model.AdditionalInformationType
import org.deku.leoz.model.OrderClassification
import org.deku.leoz.model.ParcelService
import java.util.*

/**
 * Created by phpr on 31.05.2017.
 */
class Order (
        val id: String,
        val state: State = Order.State.PENDING,
        val classification: org.deku.leoz.model.OrderClassification,
        val parcel: List<Parcel> = mutableListOf(),
        val addresses: MutableList<Address>,
        val appointment: List<Appointment>,
        val carrier: org.deku.leoz.model.Carrier,
        val service: List<Service>,
        val information: MutableList<Information>? = null,
        val sort: Int
) {

    data class Parcel (
            val id: String,
            val labelReference: String?,
            val status: MutableList<Status>? = mutableListOf(),
            val length: Float = 0.0F,
            val height: Float = 0.0F,
            val width: Float = 0.0F,
            val weight: Float = 0.0F
    )

    data class Address (
            val classification: Classification,
            val addressLine1: String,
            val addressLine2: String = "",
            val addressLine3: String = "",
            val street: String,
            val streetNo: String = "",
            val zipCode: String,
            val city: String,
            val latitude: Double = 0.0,
            val longitude: Double = 0.0,
            val phone: String = ""
    ) {
        enum class Classification {
            PICKUP, DELIVERY, EXCHANGE_DELIVERY, EXCHANGE_PICKUP
        }
    }

    data class Status (
            val event: Long,
            val reason: Long,
            val date: Date,
            val geoLocation: Pair<Double, Double>,
            val recipient: String?,
            val information: String?
    )

    data class Appointment(
            val classification: Classification,
            val dateFrom: Date,
            val dateTo: Date
    ) {
        enum class Classification {
                PICKUP, DELIVERY
        }
    }

    data class Service (
        val classification: Classification,
        val service: List<ParcelService>
    ) {
        enum class Classification {
            DELIVERY_SERVICE, PICKUP_SERVICE
        }

        fun getParcelServiceListFromBinary(value: Long): List<ParcelService> {

            val parcelServiceList: MutableList<ParcelService> = mutableListOf()

            for (p: ParcelService in ParcelService.values()) {
                if ((p.serviceId and value) == p.serviceId) {
                    parcelServiceList.add(p)
                }
            }

            return if(parcelServiceList.size == 0) listOf(ParcelService.NO_ADDITIONAL_SERVICE) else parcelServiceList
        }

        fun getBinaryFromService(service: Service): Long {
            var value: Long = 0

            service.service.forEach {
                value + it.serviceId
            }

            return value
        }
    }

    data class Information (
            val classification: Classification,
            val additionalInformation: MutableList<AdditionalInformation>
    ) {
        enum class Classification {
            DELIVERY_INFO, PICKUP_INFO
        }

        data class AdditionalInformation (
                val type: AdditionalInformationType,
                val value: String
        )
    }

    enum class State {
        PENDING, DONE, FAILED
    }

    fun equalsAddress(order: Order): Boolean {
        val adrType: Address.Classification? = null

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
        var adrType: Address.Classification? = null

        when {
            this.classification == OrderClassification.DELIVERY-> adrType = Address.Classification.DELIVERY
            this.classification == OrderClassification.PICKUP-> adrType = Address.Classification.PICKUP
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
    //data class Dimension (val length: Double, val height: Double, val width: Double, val weight: Double)
}