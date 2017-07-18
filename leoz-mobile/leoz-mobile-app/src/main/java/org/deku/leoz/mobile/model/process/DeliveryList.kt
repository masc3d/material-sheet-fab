package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.toOrder
import org.deku.leoz.model.EventNotDeliveredReason
import org.deku.leoz.service.internal.DeliveryListService
import org.slf4j.LoggerFactory
import sx.rx.toHotReplay

/**
 * Delivery list model
 * Created by masc on 18.06.17.
 */
class DeliveryList {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val deliveryListServive: DeliveryListService by Kodein.global.lazy.instance()

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

    /**
     * Loads delivery list data from remote peer into local database
     * @param deliveryListId Delivery list id
     * @return Hot observable which completes with a list of stops
     */
    fun load(deliveryListId: Long): Observable<List<Stop>> {
        return Observable.fromCallable {
            val deliveryList = this.deliveryListServive.getById(id = deliveryListId)

            // Map service orders to mobile orders
            val orders = deliveryList.orders?.map { it.toOrder() } ?: listOf()

            // Map orders to stops
            // TODO: needs refinement
//            val stops = orders.map { order ->
//                Stop(
//                        orders = mutableListOf(order),
//                        address = order.deliveryAddress,
//                        appointment = order.deliveryAppointment ?: TODO()
//                )
//            }
//
//            stops
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