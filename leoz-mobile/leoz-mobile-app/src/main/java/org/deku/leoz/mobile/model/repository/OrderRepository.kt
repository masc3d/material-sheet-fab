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
    fun save(orders: List<Order>): Completable {
        val sw = Stopwatch.createStarted()
        return store.withTransaction {
            // Store orders
            orders.forEach { order ->
                val existingOrder = this.select(OrderEntity::class)
                        .where(OrderEntity.ID.eq(order.id))
                        .get().firstOrNull()

                if (existingOrder != null) {
                    val orderParcelNumbers = order.parcels.map { it.number }.toHashSet()
                    val existingOrderParcelNumbers = existingOrder.parcels.map { it.number }.toHashSet()

                    val addedParcels = order.parcels.filter {
                        !existingOrderParcelNumbers.contains(it.number)
                    }

                    val removedParcels= existingOrder.parcels.filter {
                        !orderParcelNumbers.contains(it.number)
                    }

                    delete(removedParcels)
                    insert(addedParcels)
                } else {
                    insert(order)
                }
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
