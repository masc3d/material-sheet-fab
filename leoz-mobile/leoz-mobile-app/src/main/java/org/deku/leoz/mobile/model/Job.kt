package org.deku.leoz.mobile.model

import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty

/**
 * Created by 27694066 on 09.05.2017.
 */
class Job {
    private val log = LoggerFactory.getLogger(this.javaClass)

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

        orderList.sortBy {
            it.sort
        }

        for (order in orderList) {
            integrateOrder(order)
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
            stopList[existingStopIndex].appointment = minOf(order.appointment, stopList[existingStopIndex].appointment)
            return stopList[existingStopIndex]
        } else {
            stopList.add(
                    Stop(
                            order = mutableListOf(order),
                            address = order.getAddressOfInterest(),
                            appointment = order.appointment,
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