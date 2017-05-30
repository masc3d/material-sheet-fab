package org.deku.leoz.mobile.model

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.model.OrderClassification
import org.deku.leoz.model.ParcelService
import org.deku.leoz.service.internal.DeliveryListService
import org.deku.leoz.service.internal.OrderService
import org.deku.leoz.service.internal.entity.DeliveryList
import org.deku.leoz.service.internal.entity.Order
import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty

/**
 * Created by 27694066 on 09.05.2017.
 */
class Job {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val deliveryListService: DeliveryListService by Kodein.global.lazy.instance()
    private val orderService: OrderService by Kodein.global.lazy.instance()

//    val pendingStopProperty = ObservableRxProperty<List<Stop>>(mutableListOf())
//    val pendingStop: List<Stop> by pendingStopProperty
//
//    val doneStopProperty = ObservableRxProperty<List<Stop>>(mutableListOf())
//    val doneStop: List<Stop> by doneStopProperty

    val newOrderProperty = ObservableRxProperty<Stop.Order?>(null)
    var newOrder: Stop.Order? by newOrderProperty

    val activeStopProperty = ObservableRxProperty<Stop?>(null)
    val activeStop: Stop? by activeStopProperty

    val stopList: MutableList<Stop> = mutableListOf()
    val orderList: MutableList<Stop.Order> = mutableListOf()

    /**
     * Clean the existing Job.stopList
     * Merge existing orders in Job.orderList into the cleaned Job.stopList
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
    fun queryDeliveryList(listId: String): Boolean {
        try{
            val deliveryList: DeliveryList = deliveryListService.getById(id = listId)

            if (deliveryList.orders.isEmpty()) {
                return false
            }

            orderList.addAll(deliveryList.orders
                    .map {
                        Stop.Order(
                                it.orderID.toString(),
                                it.orderClassification,
                                it.Parcels.map {
                                    Stop.Order.Parcel(
                                            it.parcelID.toString(),
                                            it.parcelScanNumber,
                                            null,
                                            it.dimension
                                    )
                                },
                                mutableListOf(
                                        Stop.Order.Address(
                                                Stop.Order.Address.AddressClassification.DELIVERY,
                                                it.deliveryAddress.addressLine1,
                                                it.deliveryAddress.addressLine2 ?: "",
                                                it.deliveryAddress.street,
                                                it.deliveryAddress.streetNo ?: "",
                                                it.deliveryAddress.zipCode,
                                                it.deliveryAddress.city,
                                                it.deliveryAddress.geoLocation
                                        ),
                                        Stop.Order.Address(
                                                Stop.Order.Address.AddressClassification.PICKUP,
                                                it.pickupAddress.addressLine1,
                                                it.pickupAddress.addressLine2 ?: "",
                                                it.pickupAddress.street,
                                                it.pickupAddress.streetNo ?: "",
                                                it.pickupAddress.zipCode,
                                                it.pickupAddress.city,
                                                it.pickupAddress.geoLocation
                                        )
                                ),
                                mapOf(
                                        Pair(OrderClassification.Delivery, it.appointmentDelivery),
                                        Pair(OrderClassification.PickUp, it.appointmentPickup)
                                ),
                                it.carrier,
                                mapOf(
                                        Pair(OrderClassification.Delivery, it.deliveryService.services ?: listOf(ParcelService.NoAdditionalService))
                                ),
                                mapOf(
                                        Pair(OrderClassification.Delivery, it.deliveryInformation),
                                        Pair(OrderClassification.PickUp, it.pickupInformation)
                                ),
                                0
                        )
                    })

            return true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Should be called from the receiver of the order.
     * @param order Stop.Order
     */
    fun addOrder(order: Stop.Order) {
        newOrder = order
    }

    /**
     * @param order: The order which should be integrated into the stopList.
     * @return The Stop where the order has been integrated.
     * This method tries to find a stop in the existing stopList where the given order can be integrated.
     * If no suitable Stop has been found, a new stop with this order will be created.
     */
    fun integrateOrder(order: Stop.Order): Stop {
        val existingStopIndex = order.findSuitableStopIndex(stopList)

        if (!orderList.contains(order)) {
            orderList.add(order)
        }

        if (existingStopIndex > -1) {
            stopList[existingStopIndex].order.add(order)
            stopList[existingStopIndex].appointment = minOf(order.appointment[order.classification]!!, stopList[existingStopIndex].appointment, Comparator<Order.Appointment> { o1, o2 ->
                when {
                    o1.dateStart!!.before(o2.dateStart) -> 1
                    else -> 2
                }
            })
            return stopList[existingStopIndex]
        } else {
            stopList.add(
                    Stop(
                            order = mutableListOf(order),
                            address = order.getAddressOfInterest(),
                            appointment = order.appointment[order.classification]!!,
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

    fun findOrderByLabelReference(labelReference: String): List<Stop.Order> {
        return orderList.filter {
            it.parcel.filter {
                it.labelReference == labelReference
            }.isNotEmpty()
        }
    }
}