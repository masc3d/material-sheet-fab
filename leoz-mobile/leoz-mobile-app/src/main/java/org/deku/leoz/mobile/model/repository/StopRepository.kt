package org.deku.leoz.mobile.model.repository

import android.databinding.Observable
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.deku.leoz.mobile.model.entity.*
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.rx.ObservableRxProperty

/**
 * Stop repository
 * Created by masc on 20.07.17.
 */
class StopRepository(
        private val store: KotlinReactiveEntityStore<Persistable>
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    val stopsProperty = ObservableRxProperty(listOf<StopEntity>())
    val stops by stopsProperty

    /**
     * Self observable order query
     */
    private val _stops = store.select(StopEntity::class)
            .get()
            .observableResult()
            .subscribeBy(
                    onNext = {
                        val stops = it.toList()
                        log.trace("STOPS CHANGED [${stops.count()}]")
                        this.stopsProperty.set(stops)

                        stops.forEach {
                            it.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                                override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                                    log.trace("STOP FIELD CHANGED [${propertyId}]")
                                }
                            })
                        }
                    },
                    onError = {
                        log.error(it.message, it)
                    }
            )

    /**
     * Save a batch of orders, replacing existing orders if applicable
     */
    fun save(stops: List<Stop>): Completable {
        val sw = Stopwatch.createStarted()
        return store.withTransaction {
            // Store orders
            stops.forEach { stop ->
                insert(stop)
            }
        }
                .toCompletable()
                .doOnComplete {
                    val orderCount = store.count(OrderEntity::class).get().call()
                    val taskCount = store.count(OrderTaskEntity::class).get().call()
                    val addressCount = store.count(AddressEntity::class).get().call()
                    val parcelCount = store.count(ParcelEntity::class).get().call()
                    val stopCount = store.count(StopEntity::class).get().call()
                    log.trace("Saved stops in $sw :: orders [${orderCount}] stops [${stopCount}] tasks [${taskCount}] addresses [${addressCount}] parcels [${parcelCount}]")
                }
    }

    /**
     * Remove all orders
     */
    fun removeAll(): Completable {
        return store.withTransaction {
            select(StopEntity::class)
                    .get()
                    .forEach {
                        delete(it)
                    }
        }.toCompletable()
    }
}
