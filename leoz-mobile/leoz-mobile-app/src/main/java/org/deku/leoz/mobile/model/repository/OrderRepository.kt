package org.deku.leoz.mobile.model.repository

import android.databinding.Observable
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers
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
) : ObservingRepository<OrderEntity>(OrderEntity::class, store) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Find order by id
     */
    fun findById(id: Long): Order? {
        return store.select(OrderEntity::class).where(OrderEntity.ID.eq(id)).get().firstOrNull()
    }

    /**
     * Merge a batch of orders into the database.
     * Existing orders will have their parcel lists merged accordingly.
     */
    fun merge(orders: List<Order>): Single<Int> {
        val sw = Stopwatch.createStarted()
        return Single.fromCallable {
            var created = 0

            val store = this.store.toBlocking()

            // Store orders
            orders.forEach { order ->
                val existingOrder = store.select(OrderEntity::class)
                        .where(OrderEntity.ID.eq(order.id))
                        .get().firstOrNull()

                if (existingOrder != null) {
                    // On existing orders, perform sanity checks, verifying this merge is sane, otherwise throw
                    if (existingOrder.tasks.mapNotNull { it.stop }.any { it.state == Stop.State.CLOSED })
                        throw IllegalStateException("Existing order tasks reference stops which are already closed")

                    val existingOrderParcelNumbers = existingOrder.parcels
                            .groupBy { it.number }
                            .mapValues { it.value.first() }

                    order.parcels.forEach {
                        val existing = existingOrderParcelNumbers.get(it.number)
                        if (existing != null) {
                            it.loadingState = existing.loadingState
                            it.deliveryState = existing.deliveryState
                        }
                    }

                    // Delete old order tasks
                    store.delete(existingOrder)
                    store.insert(order)
                } else {
                    store.insert(order)
                    created++
                }
            }

            created
        }
    }

    /**
     * Remove all orders
     */
    fun removeAll(): Completable {
        return Completable.fromCallable {
            val store = store.toBlocking()
            store.select(OrderEntity::class)
                    .get()
                    .forEach {
                        store.delete(it)
                    }

            store.select(OrderTaskEntity::class)
                    .get()
                    .forEach {
                        store.delete(it)
                    }
        }
    }
}
