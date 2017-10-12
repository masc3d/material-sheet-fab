package org.deku.leoz.mobile.model.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.deku.leoz.mobile.model.entity.Order
import org.deku.leoz.mobile.model.entity.OrderEntity
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.entity.Stop
import org.slf4j.LoggerFactory
import sx.requery.scalar
import sx.time.plusHours
import java.util.*

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
    fun findById(id: Long): Maybe<OrderEntity> =
            store.select(OrderEntity::class)
                    .where(OrderEntity.ID.eq(id))
                    .get().observable().firstElement()

    /**
     * Determine if repository has outdated orders
     */
    fun hasOutdatedOrders(): Single<Boolean> {
        val MAX_AGE_HOURS = 20

        return store.select(OrderEntity.CREATION_TIME.min())
                .get()
                .scalar<Date>()
                .map {
                    it.plusHours(MAX_AGE_HOURS) < Date()
                }
                .defaultIfEmpty(false)
                .toSingle()
    }

    /**
     * Determine if repository has relevant orders.
     * The result will be true if there's orders, but no outdated ones.
     */
    fun hasRelevantOrders(): Single<Boolean> {
        return Single.zip(
                this.hasOutdatedOrders(),
                store.count(OrderEntity::class).get().single().map { it > 0 },
                BiFunction { hasOutdatedOrders: Boolean, hasOrders: Boolean ->
                    hasOrders && !hasOutdatedOrders
                }
        )
    }

    /**
     * Merge a batch of orders into the database.
     * Existing orders will have their parcel lists merged accordingly.
     */
    fun merge(orders: List<Order>): Single<Int> {
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
                            // Only override local state if we had one (not pending)
                            if (existing.state != Parcel.State.PENDING) {
                                it.state = existing.state
                            }

                            // Only override damaged state if it was already set locally
                            if (existing.isDamaged) {
                                it.isDamaged = true
                            }
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
     * Remove all orders (and referenced records, eg. tasks, addresses and stops)
     */
    fun removeAll(): Completable {
        return Completable.fromCallable {
            val store = store.toBlocking()
            store.select(OrderEntity::class)
                    .get()
                    .forEach {
                        store.delete(it)
                    }
        }
    }
}
