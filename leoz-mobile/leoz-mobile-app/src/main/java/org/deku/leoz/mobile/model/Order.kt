package org.deku.leoz.mobile.model

import org.deku.leoz.model.AdditionalInformationType
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
        val information: MutableList<Information>? = null,
        val sort: Int
) {

    data class Parcel (
            val id: String,
            var state: Parcel.State = Parcel.State.PENDING,
            val labelReference: String?,
            val status: MutableList<Status>? = mutableListOf(),
            val length: Float = 0.0F,
            val height: Float = 0.0F,
            val width: Float = 0.0F,
            val weight: Float = 0.0F
    ) {
        enum class State{
            PENDING, LOADED, MISSING, DONE, FAILED
        }
    }

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

    fun parcelVehicleLoading(parcel: Order.Parcel): Boolean {
        val parcel = this.parcel.firstOrNull { it == parcel } ?: throw IllegalArgumentException("Parcel [${parcel.id}] is not part of the order [${this.id}]")

        parcel.state = Order.Parcel.State.LOADED

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
        return this.parcel.firstOrNull { it.labelReference == ref }
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
                Order.Parcel(
                        id = it.id.toString(),
                        labelReference = it.number,
                        status = null,
                        length = it.dimension?.length?.toFloat() ?: 0.0F,
                        height = it.dimension?.height?.toFloat() ?: 0.0F,
                        width = it.dimension?.width?.toFloat() ?: 0.0F,
                        weight = it.dimension?.weight?.toFloat() ?: 0.0F
                )
            },
            addresses = mutableListOf(
                    Order.Address(
                            classification = Order.Address.Classification.DELIVERY,
                            addressLine1 = this.deliveryAddress.addressLine1,
                            addressLine2 = this.deliveryAddress.addressLine2 ?: "",
                            addressLine3 = this.deliveryAddress.addressLine3 ?: "",
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
                            addressLine1 = this.pickupAddress.addressLine1,
                            addressLine2 = this.pickupAddress.addressLine2 ?: "",
                            addressLine3 = this.pickupAddress.addressLine3 ?: "",
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
                            service = this.deliveryService.services ?: listOf(ParcelService.NO_ADDITIONAL_SERVICE)
                    ),
                    Order.Service(
                            classification = Order.Service.Classification.PICKUP_SERVICE,
                            service = this.pickupService.services ?: listOf(ParcelService.NO_ADDITIONAL_SERVICE)
                    )
            ),
            information = mutableListOf(
                    Order.Information(
                            classification = Order.Information.Classification.DELIVERY_INFO,
                            additionalInformation = this.deliveryInformation!!.additionalInformation!!.map {
                                Order.Information.AdditionalInformation(
                                        type = it.additionalInformationType!!,
                                        value = it.information ?: ""
                                )
                            }.toMutableList()
                    ),
                    Order.Information(
                            classification = Order.Information.Classification.PICKUP_INFO,
                            additionalInformation = this.pickupInformation!!.additionalInformation!!.map {
                                Order.Information.AdditionalInformation(
                                        type = it.additionalInformationType!!,
                                        value = it.information ?: ""
                                )
                            }.toMutableList()
                    )
            ),
            sort = 0
    )
}

