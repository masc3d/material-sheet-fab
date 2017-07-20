package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.*
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.repository.OrderRepository
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
    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()

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

            val deliveryList = this.deliveryListServive.getById(id = deliveryListId)

            fun logCount() {
                log.trace("orders [${db.store.count(OrderEntity::class).get().call()}] tasks [${db.store.count(OrderTaskEntity::class).get().call()}] addresses [${db.store.count(AddressEntity::class).get().call()}] parcels [${db.store.count(ParcelEntity::class).get().call()}]")
            }

            log.trace("Delivery list loaded in $sw orders [${deliveryList.orders.count()}] parcels [${deliveryList.orders.flatMap { it.parcels }.count()}]")

            sw.restart()

            this.orderRepository.removeAll().blockingGet()

            logCount()

            this.orderRepository.save(
                    deliveryList.orders.map {
                        it.toOrder()
                    }
            ).blockingGet()

            log.trace("Delivery list transformed and stored in db in $sw")

            logCount()

            listOf<Stop>()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    log.error(it.message)
                }
                .toHotReplay()
    }
}