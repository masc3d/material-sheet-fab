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
 * Order repository
 * Created by masc on 20.07.17.
 */
class OrderRepository(
        private val store: KotlinReactiveEntityStore<Persistable>
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    val ordersProperty = ObservableRxProperty(listOf<OrderEntity>())
    val orders by ordersProperty

    /**
     * Self observable order query
     */
    private val _orders = store.select(OrderEntity::class)
            .get()
            .observableResult()
            .subscribeBy(
                    onNext = {
                        val orders = it.toList()
                        log.trace("ORDERS CHANGED [${orders.count()}]")
                        this.ordersProperty.set(orders)

                        orders.forEach {
                            it.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                                override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                                    log.trace("ORDER FIELD CHANGED [${propertyId}]")
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
    fun save(orders: List<Order>): Completable {
        val sw = Stopwatch.createStarted()
        return store.withTransaction {
            // Filter orders, remove duplicates eg.
            val filteredOrders = orders.groupBy {
                it.id
            }.map {
                if (it.value.count() > 1) {
                    log.warn("Duplicate order id [${it.key}] parcel counts [${it.value.map { it.parcels.count() }.joinToString(", ")}]")
                }
                it.value.maxBy {
                    it.parcels.count()
                }
            }.filterNotNull()

            // Store orders
            filteredOrders.forEach { order ->
                val existingOrder = this.select(OrderEntity::class)
                        .where(OrderEntity.ID.eq(order.id))
                        .get().firstOrNull()

                if (existingOrder != null) {
                    delete(existingOrder)
                }

                insert(order)
            }
        }
                .toCompletable()
                .doOnComplete {
                    val orderCount = store.count(OrderEntity::class).get().call()
                    val taskCount = store.count(OrderTaskEntity::class).get().call()
                    val addressCount = store.count(AddressEntity::class).get().call()
                    val parcelCount = store.count(ParcelEntity::class).get().call()
                    log.trace("Saved orders in $sw :: orders [${orderCount}] tasks [${taskCount}] addresses [${addressCount}] parcels [${parcelCount}]")
                }
    }

    /**
     * Remove all orders
     */
    fun removeAll(): Completable {
        return store.withTransaction {
            select(OrderEntity::class)
                    .get()
                    .forEach {
                        delete(it)
                    }
        }.toCompletable()
    }
}
