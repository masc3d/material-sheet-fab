package org.deku.leoz.mobile.model

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.deku.leoz.model.Carrier
import org.deku.leoz.model.OrderClassification
import org.deku.leoz.model.ParcelService
import org.deku.leoz.service.internal.DeliveryListService
import org.deku.leoz.service.internal.OrderService
import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty
import java.util.*

/**
 * Created by 27694066 on 09.05.2017.
 */
class Delivery {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val deliveryListService: DeliveryListService by Kodein.global.lazy.instance()
    private val orderService: OrderService by Kodein.global.lazy.instance()

    val newOrderProperty = ObservableRxProperty<Order?>(null)
    var newOrder: Order? by newOrderProperty

    val activeStopProperty = ObservableRxProperty<Stop?>(null)
    val activeStop: Stop? by activeStopProperty

    /**
     * Receives the acquired information during the service workflow process, to be published to its subscribers.
     * eg. entered information of ID document
     */
    val informationSubject: Subject<ServiceInformation> = PublishSubject.create()

    /**
     * Receives the result of a service workflow and publishes it to the subscribers.
     * eg. IMEI Check OK/Failed, CASH (didn't) collected, correct/wrong ID-Document
     */
    val serviceCheckEventSubject by lazy { PublishSubject.create<ServiceCheck>() }
    val serviceCheckEvent by lazy { serviceCheckEventSubject.hide() }

    val stopList: MutableList<Stop> = mutableListOf()
    val orderList: MutableList<Order> = mutableListOf()

    /**
     * When initiating, check for existing orders stored in the local DB and (re)load them into the variables.
     */
    init {
        val addr = Order.Address(
                classification = Order.Address.Classification.DELIVERY,
                addressLine1 = "Prangenberg",
                addressLine2 = "DEK KURIER",
                addressLine3 = "3. Addresszeile",
                street = "Dörrwiese",
                streetNo = "2",
                zipCode = "36286",
                city = "Neuenstein",
                phone = "+49 6677 9582"
        )
        val appointment = Order.Appointment(
                classification = Order.Appointment.Classification.DELIVERY,
                dateFrom = Date(),
                dateTo = Date()
        )

        stopList.add(Stop(
                order = mutableListOf(
                        Order(
                                id = "1",
                                state = Order.State.PENDING,
                                classification = OrderClassification.DELIVERY,
                                parcel = listOf(
                                        Order.Parcel(
                                                id = "a",
                                                labelReference = "10000000001"
                                        ),
                                        Order.Parcel(
                                                id = "b",
                                                labelReference = "10000000002"
                                        ),
                                        Order.Parcel(
                                                id = "c",
                                                labelReference = "10000000003"
                                        )
                                ),
                                addresses = mutableListOf(addr),
                                appointment = listOf(appointment),
                                carrier = Carrier.DER_KURIER,
                                service = listOf(Order.Service(
                                        classification = Order.Service.Classification.DELIVERY_SERVICE,
                                        service = listOf(ParcelService.NO_ADDITIONAL_SERVICE))
                                ),
                                sort = 0
                        )
                ),
                address = addr,
                appointment = appointment,
                sort = 0,
                state = Stop.State.PENDING
        ))

        stopList.add(Stop(
                order = mutableListOf(
                        Order(
                                id = "2",
                                state = Order.State.PENDING,
                                classification = OrderClassification.DELIVERY,
                                parcel = listOf(Order.Parcel(
                                        id = "a",
                                        labelReference = "02000000001"
                                )),
                                addresses = mutableListOf(addr),
                                appointment = listOf(appointment),
                                carrier = Carrier.DER_KURIER,
                                service = listOf(Order.Service(
                                        classification = Order.Service.Classification.DELIVERY_SERVICE,
                                        service = listOf(ParcelService.NO_ADDITIONAL_SERVICE))
                                ),
                                sort = 0
                        )
                ),
                address = addr,
                appointment = appointment,
                sort = 0,
                state = Stop.State.PENDING
        ))

    }

    data class ServiceCheck(val type: CheckType, val success: Boolean) {
        enum class CheckType {
            IMEI_CHECKED, CASH_COLLECTED
        }
    }

    data class ServiceInformation(val type: Order.Information.Classification, val value: Order.Information.AdditionalInformation)

    fun addInformation(serviceInformation: ServiceInformation) {
        informationSubject.onNext(serviceInformation)
    }

    /**
     * Clean the existing Delivery.stopList
     * Merge existing orders in Delivery.orderList into the blank Delivery.stopList
     */
    fun initializeStopList() {
        stopList.clear()

        for (order in orderList) {
            integrateOrder(order)
        }
    }

    /**
     *
     */
    fun queryDeliveryList(listId: String): List<Order> {
        try{
            val deliveryList: DeliveryListService.DeliveryList = deliveryListService.getById(id = listId)

            if (deliveryList.orders.isEmpty()) {
                return listOf()
            }

            val mobileOrder = mapServiceOrder(deliveryList.orders)

            orderList.addAll(mobileOrder)

            return mobileOrder
        } catch (e: Exception) {
            log.error(e.message)
            return listOf()
        }
    }

    fun queryOrderServiceByLabel(ref: String): List<Order> {
        try {
            val order: List<OrderService.Order> = orderService.get(labelRef = ref)

            when (order.size) {
                0 -> {
                    return listOf()
                }
                1 -> {
                    orderList.addAll(mapServiceOrder(order))
                    return mapServiceOrder(order)
                }
                else -> {
                    return mapServiceOrder(order)
                }
            }
        } catch (e: Exception) {
            log.error(e.message)
            return listOf()
        }
    }

    fun mapServiceOrder(orderList: List<OrderService.Order>): List<Order> {
        return orderList.map {
            Order(
                    id = it.orderID.toString(),
                    state = Order.State.PENDING,
                    classification = it.orderClassification,
                    parcel = it.Parcels.map {
                        Order.Parcel(
                                id = it.parcelID.toString(),
                                labelReference = it.parcelScanNumber,
                                status = null,
                                length = it.dimension?.length?.toFloat() ?: 0.0F,
                                height = it.dimension?.height?.toFloat() ?: 0.0F,
                                width = it.dimension?.width?.toFloat() ?: 0.0F,
                                weight = it.dimension?.weight?.toFloat() ?: 0.0F
                        )
                    },
                    addresses = mutableListOf(
                            Order.Address(
                                    Order.Address.Classification.DELIVERY,
                                    it.deliveryAddress.addressLine1,
                                    it.deliveryAddress.addressLine2 ?: "",
                                    it.deliveryAddress.addressLine3 ?: "",
                                    it.deliveryAddress.street,
                                    it.deliveryAddress.streetNo ?: "",
                                    it.deliveryAddress.zipCode,
                                    it.deliveryAddress.city,
                                    it.deliveryAddress.geoLocation?.latitude ?: 0.0,
                                    it.deliveryAddress.geoLocation?.longitude ?: 0.0,
                                    it.deliveryAddress.telefonNumber ?: ""
                            ),
                            Order.Address(
                                    Order.Address.Classification.PICKUP,
                                    it.pickupAddress.addressLine1,
                                    it.pickupAddress.addressLine2 ?: "",
                                    it.pickupAddress.addressLine3 ?: "",
                                    it.pickupAddress.street,
                                    it.pickupAddress.streetNo ?: "",
                                    it.pickupAddress.zipCode,
                                    it.pickupAddress.city,
                                    it.pickupAddress.geoLocation?.latitude ?: 0.0,
                                    it.pickupAddress.geoLocation?.longitude ?: 0.0,
                                    it.deliveryAddress.telefonNumber ?: ""
                            )
                    ),
                    appointment = listOf(),
                    carrier = it.carrier,
                    service = listOf(
                            Order.Service(
                                    classification = Order.Service.Classification.DELIVERY_SERVICE,
                                    service = it.deliveryService.services ?: listOf(ParcelService.NO_ADDITIONAL_SERVICE)
                            ),
                            Order.Service(
                                    classification = Order.Service.Classification.PICKUP_SERVICE,
                                    service = it.pickupService.services ?: listOf(ParcelService.NO_ADDITIONAL_SERVICE)
                            )
                    ),
                    information = mutableListOf(
                            Order.Information(
                                    classification = Order.Information.Classification.DELIVERY_INFO,
                                    additionalInformation = it.deliveryInformation!!.AdditionalInformation!!.map {
                                        Order.Information.AdditionalInformation(
                                                type = it.AdditionalInformationType!!,
                                                value = it.information ?: ""
                                        )
                                    }.toMutableList()
                            ),
                            Order.Information(
                                    classification = Order.Information.Classification.PICKUP_INFO,
                                    additionalInformation = it.pickupInformation!!.AdditionalInformation!!.map {
                                        Order.Information.AdditionalInformation(
                                                type = it.AdditionalInformationType!!,
                                                value = it.information ?: ""
                                        )
                                    }.toMutableList()
                            )
                    ),
                    sort = 0
            )
        }
    }

    /**
     * Should be called from the receiver of the order.
     * @param order Stop.Order
     */
    fun addOrder(order: Order) {
        newOrder = order
    }

    /**
     * @param order: The order which should be integrated into the stopList.
     * @return The Stop where the order has been integrated.
     * This method tries to find a stop in the existing stopList where the given order can be integrated.
     * If no suitable Stop has been found, a new stop with this order will be created.
     */
    fun integrateOrder(order: Order): Stop {
        val existingStopIndex = order.findSuitableStopIndex(stopList)

        if (!orderList.contains(order)) {
            orderList.add(order)
        }

        if (existingStopIndex > -1) {
            stopList[existingStopIndex].order.add(order)
            TODO("To be fixed before use")
            /* TODO
            stopList[existingStopIndex].appointment = minOf(order.appointment[order.classification]!!, stopList[existingStopIndex].appointment, Comparator<Order.Appointment> { o1, o2 ->
                when {
                    o1.dateStart!!.before(o2.dateStart) -> 1
                    else -> 2
                }
            }) */
            return stopList[existingStopIndex]
        } else {
            stopList.add(
                    Stop(
                            order = mutableListOf(order),
                            address = order.getAddressOfInterest(),
                            appointment = order.appointment.first {
                                (it.classification == Order.Appointment.Classification.DELIVERY && order.classification == OrderClassification.DELIVERY) || (it.classification == Order.Appointment.Classification.PICKUP && order.classification == OrderClassification.PICKUP)
                            },
                            sort = stopList.last().sort + 1
                    )
            )
            return stopList.last()
        }
    }

    fun findStopByLabelReference(labelReference: String): List<Stop> {
        return stopList.filter {
            it.order.filter {
                it.parcel.filter {
                    it.labelReference == labelReference
                }.isNotEmpty()
            }.isNotEmpty()
        }
    }

    fun findOrderByLabelReference(labelReference: String): List<Order> {
        return orderList.filter {
            it.parcel.filter {
                it.labelReference == labelReference
            }.isNotEmpty()
        }
    }
}