package org.deku.leoz.mobile.model.repository

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.Single
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.Order
import org.deku.leoz.mobile.model.entity.OrderEntity
import org.slf4j.LoggerFactory

/**
 * Order repository
 * Created by masc on 20.07.17.
 */
class OrderRepository(
        private val store: KotlinReactiveEntityStore<Persistable>
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Save a batch of orders, replacing existing orders if applicable
     */
    fun save(orders: List<Order>): Completable {
        return store.withTransaction {
            orders.forEach { order ->
                val existingOrder = this.select(OrderEntity::class)
                        .where(OrderEntity.ID.eq(order.id))
                        .get().firstOrNull()

                if (existingOrder != null) {
                    log.warn("Duplicate order [${order.id}]")
                    delete(existingOrder)
                }

                insert(order)
            }
        }.toCompletable()
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
