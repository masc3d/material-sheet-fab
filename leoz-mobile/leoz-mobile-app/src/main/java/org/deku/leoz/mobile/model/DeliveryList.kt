package org.deku.leoz.mobile.model

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    /**
     * Loads delivery list data from remote peer into local database
     * @return Hot observable which completes with a list of stops
     */
    fun load(): Observable<List<Stop>> {
        return Observable.fromCallable {
            val deliveryList = this.deliveryListServive.getById("1")

            // Map service orders to mobile orders
            val orders = deliveryList.orders.map { it.toOrder() }

            // Map orders to stops
            // TODO: needs refinement
            val stops = orders.map { order ->
                Stop(
                        orders = mutableListOf(order),
                        address = order.addresses.first(),
                        appointment = order.appointment.first()
                )
            }

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