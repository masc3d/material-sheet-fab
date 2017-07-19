package org.deku.leoz.mobile.model

import org.deku.leoz.mobile.model.entity.Address
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.entity.create
import org.deku.leoz.model.OrderClassification
import org.deku.leoz.model.ParcelService
import org.deku.leoz.service.internal.OrderService
import java.util.*

/**
 * Created by phpr on 31.05.2017.
 */
class Order(
        val id: String,
        var state: State = Order.State.PENDING,
        val classification: org.deku.leoz.model.OrderClassification,
        val parcel: List<Parcel> = mutableListOf(),
        val deliveryAddress: Address,
        val deliveryAppointment: Appointment? = null,
        val deliveryNotice: String? = null,
        val pickupAddress: Address,
        val pickupAppointment: Appointment? = null,
        val pickupNotice: String? = null,
        val carrier: org.deku.leoz.model.Carrier,
        val services: List<Service>
) {

    val serviceCheckList: List<ServiceCheck> by lazy {
        val list: MutableList<ServiceCheck> = mutableListOf()
        val parcelServiceList: List<ParcelService> = this.getServiceOfInterest().service

        parcelServiceList.forEach {
            when (it) {
                ParcelService.CASH_ON_DELIVERY,
                ParcelService.RECEIPT_ACKNOWLEDGEMENT,
                ParcelService.PHARMACEUTICALS,
                ParcelService.IDENT_CONTRACT_SERVICE,
                ParcelService.SUBMISSION_PARTICIPATION,
                ParcelService.SECURITY_RETURN,
                ParcelService.XCHANGE,
                ParcelService.PHONE_RECEIPT,
                ParcelService.DOCUMENTED_PERSONAL_DELIVERY,
                ParcelService.SELF_COMPLETION_OF_DUTY_PAYMENT_AND_DOCUMENTS,
                ParcelService.PACKAGING_RECIRCULATION -> list.add(ServiceCheck(service = it))
                else -> {

                }
            }
        }

        list.toList()
    }

    data class ServiceCheck(val service: ParcelService, var done: Boolean = false, var success: Boolean = false)

    fun getNextServiceCheck(): ServiceCheck? {
        return if (serviceCheckList.isNotEmpty()) serviceCheckList.firstOrNull { !it.done } else null
    }

    data class Status(
            val event: Long,
            val reason: Long,
            val date: Date,
            val geoLocation: Pair<Double, Double>,
            val recipient: String?,
            val information: String?
    )

    data class Appointment(
            val dateFrom: Date,
            val dateTo: Date
    ) {
        enum class Classification {
            PICKUP, DELIVERY
        }
    }

    data class Service(
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

            return if (parcelServiceList.size == 0) listOf(ParcelService.NO_ADDITIONAL_SERVICE) else parcelServiceList
        }

        fun getBinaryFromService(service: Service): Long {
            var value: Long = 0

            service.service.forEach {
                value + it.serviceId
            }

            return value
        }
    }

    data class Information(
            val classification: Classification,
            val additionalInformation: String
    ) {
        enum class Classification {
            DELIVERY_INFO, PICKUP_INFO
        }
    }

    enum class State(val key: String) {
        PENDING("PENDING"),
        LOADED("LOADED"),
        DONE("DONE"),
        FAILED("FAILED")
    }

//    fun equalsAddress(order: Order): Boolean {
//        val adrType: Address.Classification? = null
//
//        if (this.classification != order.classification) {
//            throw IllegalArgumentException()
//        }
//
//
//
//        val address1: Address = order.addresses.first {
//            it.classification == adrType
//        }
//
//        val address2: Address = this.addresses.first {
//            it.classification == adrType
//        }
//
//        return address1 == address2
//    }

    fun getAddressOfInterest(): Address {
        return when (this.classification) {
            OrderClassification.DELIVERY -> this.deliveryAddress
            OrderClassification.PICKUP -> this.pickupAddress
            else -> {
                throw IllegalStateException()
            }
        }
    }

    fun getAppointmentOfInterest(): Appointment {
        return when (this.classification) {
            OrderClassification.PICKUP -> this.deliveryAppointment
            OrderClassification.DELIVERY -> this.pickupAppointment
            else -> null
        } ?: throw IllegalStateException()
    }

    fun getServiceOfInterest(): Service {
        return when (this.classification) {
            OrderClassification.PICKUP -> this.services.first { it.classification == Service.Classification.PICKUP_SERVICE }
            OrderClassification.DELIVERY -> this.services.first { it.classification == Service.Classification.DELIVERY_SERVICE }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    /**
     * @param stopList The stopList this method should use to iterate
     * @return If a existing stop has been found, the stop is returned. If not, it will be null
     */
//    fun findSuitableStop(stopList: MutableList<Stop>): Stop? {
////            return stopList.firstOrNull {
////                it.address.equals(this.getAddressOfInterest())
////            }
//
//        return if (findSuitableStopIndex(stopList) == -1) null else stopList[findSuitableStopIndex(stopList)]
//    }


//    fun findSuitableStopIndex(stopList: MutableList<Stop>): Int {
//        return stopList.indexOfFirst {
//            it.address == this.getAddressOfInterest()
//        }
//    }
    //data class Dimension (val length: Double, val height: Double, val width: Double, val weight: Double)

    fun parcelVehicleLoading(parcel: Parcel): Boolean {
        val parcel = this.parcel.firstOrNull { it == parcel }
                ?: throw IllegalArgumentException("Parcel [${parcel.id}] is not part of the order [${this.id}]")

        parcel.state = Parcel.State.LOADED

        var allSet = true
        this.parcel.forEach {
            if (it.state == Parcel.State.PENDING) {
                allSet = false
            }
        }

        if (allSet && this.state == Order.State.PENDING)
            this.state = Order.State.LOADED

        return true
    }

    fun findParcelByReference(ref: String): Parcel? {
        return this.parcel.firstOrNull { it.number == ref }
    }

//    fun toStop(): Stop {
//        return Stop(
//                orders = mutableListOf(this),
//                address = this.getAddressOfInterest(),
//                appointment = this.getAppointmentOfInterest()
//        )
//    }
}