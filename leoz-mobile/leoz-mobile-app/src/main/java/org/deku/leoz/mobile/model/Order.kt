package org.deku.leoz.mobile.model

import org.deku.leoz.model.OrderClassification
import org.deku.leoz.model.ParcelService
import org.deku.leoz.service.internal.OrderService
import java.util.*

/**
 * Created by phpr on 31.05.2017.
 */
class Order (
        val id: String,
        var state: State = Order.State.PENDING,
        val classification: org.deku.leoz.model.OrderClassification,
        val parcel: List<Parcel> = mutableListOf(),
        val addresses: MutableList<Address>,
        val appointment: List<Appointment>,
        val carrier: org.deku.leoz.model.Carrier,
        val services: List<Service>,
        val information: MutableList<Information>? = null
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

    data class Address (
            val classification: Classification,
            val line1: String,
            val line2: String = "",
            val line3: String = "",
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

    fun getAppointmentOfInterest(): Appointment {

        when (this.classification) {
            OrderClassification.PICKUP -> return this.appointment.first { it.classification == Appointment.Classification.PICKUP }
            OrderClassification.DELIVERY -> return this.appointment.first { it.classification == Appointment.Classification.DELIVERY }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    fun getServiceOfInterest(): Service {
        when (this.classification) {
            OrderClassification.PICKUP -> return this.services.first { it.classification == Service.Classification.PICKUP_SERVICE }
            OrderClassification.DELIVERY -> return this.services.first { it.classification == Service.Classification.DELIVERY_SERVICE }
            else -> {
                throw IllegalStateException()
            }
        }
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

    fun parcelVehicleLoading(parcel: Parcel): Boolean {
        val parcel = this.parcel.firstOrNull { it == parcel } ?: throw IllegalArgumentException("Parcel [${parcel.id}] is not part of the order [${this.id}]")

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
        return this.parcel.firstOrNull { it.labelRef == ref }
    }

    fun toStop(): Stop {
        return Stop(
                orders = mutableListOf(this),
                address = this.getAddressOfInterest(),
                appointment = this.getAppointmentOfInterest()
        )
    }
}

/**
 * Transofrm order service entity to mobile model
 * Created by masc on 18.06.17.
 */
fun OrderService.Order.toOrder(): Order {
    return Order(
            id = this.id.toString(),
            state = Order.State.PENDING,
            classification = this.orderClassification,
            parcel = this.parcels.map {
                Parcel(
                        id = it.id.toString(),
                        labelRef = it.number,
                        length = it.dimension.length?.toDouble() ?: 0.0,
                        height = it.dimension.height?.toDouble() ?: 0.0,
                        width = it.dimension.width?.toDouble() ?: 0.0,
                        weight = it.dimension.weight ?: 0.0
                )
            },
            addresses = mutableListOf(
                    Order.Address(
                            classification = Order.Address.Classification.DELIVERY,
                            line1 = this.deliveryAddress.line1,
                            line2 = this.deliveryAddress.line2 ?: "",
                            line3 = this.deliveryAddress.line3 ?: "",
                            street = this.deliveryAddress.street,
                            streetNo = this.deliveryAddress.streetNo ?: "",
                            zipCode = this.deliveryAddress.zipCode,
                            city = this.deliveryAddress.city,
                            latitude = this.deliveryAddress.geoLocation?.latitude ?: 0.0,
                            longitude = this.deliveryAddress.geoLocation?.longitude ?: 0.0,
                            phone = this.deliveryAddress.phoneNumber ?: ""
                    ),
                    Order.Address(
                            classification = Order.Address.Classification.PICKUP,
                            line1 = this.pickupAddress.line1,
                            line2 = this.pickupAddress.line2 ?: "",
                            line3 = this.pickupAddress.line3 ?: "",
                            street = this.pickupAddress.street,
                            streetNo = this.pickupAddress.streetNo ?: "",
                            zipCode = this.pickupAddress.zipCode,
                            city = this.pickupAddress.city,
                            latitude = this.pickupAddress.geoLocation?.latitude ?: 0.0,
                            longitude = this.pickupAddress.geoLocation?.longitude ?: 0.0,
                            phone = this.pickupAddress.phoneNumber ?: ""
                    )
            ),
            appointment = listOf(),
            carrier = this.carrier,
            services = listOf(
                    Order.Service(
                            classification = Order.Service.Classification.DELIVERY_SERVICE,
                            service = this.deliveryServices ?: listOf(ParcelService.NO_ADDITIONAL_SERVICE)
                    ),
                    Order.Service(
                            classification = Order.Service.Classification.PICKUP_SERVICE,
                            service = this.pickupServices ?: listOf(ParcelService.NO_ADDITIONAL_SERVICE)
                    )
            ),
            information = mutableListOf(
                    Order.Information(
                            classification = Order.Information.Classification.DELIVERY_INFO,
                            additionalInformation = this.deliveryNotice ?: ""
                    ),
                    Order.Information(
                            classification = Order.Information.Classification.PICKUP_INFO,
                            additionalInformation = this.pickupNotice ?: ""
                    )
            )
    )
}

