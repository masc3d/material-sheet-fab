package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.*
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.model.service.toOrder
import org.deku.leoz.model.EventNotDeliveredReason
import org.deku.leoz.service.internal.DeliveryListService
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.rx.toHotReplay

/**
 * Delivery list model
 * Created by masc on 18.06.17.
 */
class DeliveryList {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val db: Database by Kodein.global.lazy.instance()
    private val deliveryListServive: DeliveryListService by Kodein.global.lazy.instance()

    // Repositories
    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()

    // TODO: lazily calculate those values when loading state changes
    val stopAmount: Int = 0

    val stopTotalAmount: Int = 0

    val orderAmount: Int = 0

    val orderTotalAmount: Int = 0

    val parcelAmount: Int = 0

    val parcelTotalAmount: Int = 0

    val weight: Double = 0.0

    val totalWeight: Double = 0.0

    val allowedEvents: List<EventNotDeliveredReason> by lazy {
        listOf(
                EventNotDeliveredReason.Damaged
                //TODO "Missing" reason is not present yet
        )
    }

//    fun parcelVehicleLoading(parcel: Parcel): Boolean {
//        val parcel = this.parcel.firstOrNull { it == parcel }
//                ?: throw IllegalArgumentException("Parcel [${parcel.id}] is not part of the order [${this.id}]")
//
//        parcel.state = Parcel.State.LOADED
//
//        var allSet = true
//        this.parcel.forEach {
//            if (it.state == Parcel.State.PENDING) {
//                allSet = false
//            }
//        }
//
//        if (allSet && this.state == Order.State.PENDING)
//            this.state = Order.State.LOADED
//
//        return true
//    }

//    /**
//     * @param stopList The stopList this method should use to iterate
//     * @return If a existing stop has been found, the stop is returned. If not, it will be null
//     */
//    fun findSuitableStop(stopList: MutableList<Stop>): Stop? {
//            return stopList.firstOrNull {
//                it.address.equals(this.getAddressOfInterest())
//            }
//
//        return if (findSuitableStopIndex(stopList) == -1) null else stopList[findSuitableStopIndex(stopList)]
//    }


//    fun findSuitableStopIndex(stopList: MutableList<Stop>): Int {
//        return stopList.indexOfFirst {
//            it.address == this.getAddressOfInterest()
//        }
//    }


    /**
     * Loads delivery list data from remote peer into local database
     * @param deliveryListId Delivery list id
     * @return Hot observable which completes with a list of stops
     */
    fun load(deliveryListId: Long): Observable<List<Stop>> {
        return Observable.fromCallable {
            val sw = Stopwatch.createStarted()

            // Retrieve delivery list
            val deliveryList = this.deliveryListServive.getById(id = deliveryListId)
            log.trace("Delivery list loaded in $sw orders [${deliveryList.orders.count()}] parcels [${deliveryList.orders.flatMap { it.parcels }.count()}]")

            // Process orders
            run {
                //region Post process orders, filter out duplicates
                val filteredOrders = deliveryList.orders.groupBy {
                    it.id
                }.map {
                    if (it.value.count() > 1) {
                        log.warn("Duplicate order id [${it.key}] parcel counts [${it.value.map { it.parcels.count() }.joinToString(", ")}]")
                    }
                    it.value.maxBy {
                        it.parcels.count()
                    }
                }.filterNotNull()
                //endregion

                // Save & replace orders
                this.orderRepository
                        .removeAll()
                        .blockingGet()

                this.orderRepository
                        .save(filteredOrders.map {
                            it.toOrder()
                        })
                        .blockingGet()
            }

            // Process stops
            val stops = deliveryList.stops.map {
                Stop.create(
                        stopTasks = it.tasks.map {
                            val order = this.orderRepository.findById(it.orderId)

                            when {
                                order != null -> {
                                    when (it.stopType) {
                                        DeliveryListService.Task.Type.DELIVERY -> order.deliveryTask
                                        DeliveryListService.Task.Type.PICKUP -> order.pickupTask
                                    }
                                }
                                else -> {
                                    log.warn("Skipping order task. Referenced order [${it.orderId}] does not exist")
                                    null
                                }
                            }
                        }.filterNotNull()
                )
            }

            //region Post process stops, filter duplicates´
            val filteredStops = stops.flatMap { stop ->
                // Create a pair of sotp id -> task for all tasks
                stop.tasks.map { task ->
                    Pair(stop, task)
                }
            }.groupBy {
                // Group by order task id to find duplicates
                it.second.id
            }.map {
                if (it.value.count() > 1) {
                    log.warn("Duplicate stop task [${it.key}], filtering all but the last one")
                }
                it.value.last().first
            }
            //endregion

            this.stopRepository
                    .save(filteredStops)
                    .blockingGet()

            log.trace("Delivery list transformed and stored in db in $sw")

            stops
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    log.error(it.message)
                }
                .toHotReplay()
    }
}