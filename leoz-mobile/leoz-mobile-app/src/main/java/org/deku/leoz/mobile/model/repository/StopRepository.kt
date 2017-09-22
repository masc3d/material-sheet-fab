package org.deku.leoz.mobile.model.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.deku.leoz.mobile.model.entity.*
import org.slf4j.LoggerFactory
import sx.requery.scalarOr

/**
 * Stop repository
 * Created by masc on 20.07.17.
 */
class StopRepository(
        private val store: KotlinReactiveEntityStore<Persistable>
) : ObservingRepository<StopEntity>(StopEntity::class, store) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Find stop by id
     * @param id Stop id
     */
    fun findById(id: Int): Maybe<StopEntity> {
        return store.select(StopEntity::class)
                .where(StopEntity.ID.eq(id))
                .get().observable().firstElement()
    }

    /**
     * Finds a suitabtle existing stop compatible with this task
     * @param task Order task
     */
    fun findStopForTask(task: OrderTask): Maybe<Stop> {
        return store.select(OrderTaskEntity::class)
                .get()
                .observable()
                .filter {
                    it.stop != null &&
                            it.services.size == task.services.size &&
                            it.services.containsAll(task.services) &&
                            it.hasCompatibleAppointmentsWith(task) &&
                            it.address.isCompatibleStopAddressFor(task.address)
                }
                .map { it.stop ?: throw IllegalArgumentException() }
                .firstElement()
    }

    /**
     * Merge a batch of stops into the database and initialize positions appropriately
     * Stops which reference the same order tasks will be removed in order to avoid duplicates.
     */
    fun merge(stops: List<Stop>): Completable {
        return Completable.fromCallable {
            val store = this.store.toBlocking()

            var position = store.select(StopEntity.POSITION.max())
                    .get()
                    .scalarOr(0.0)
                    .let {
                        Math.floor(it + 1.0)
                    }

            stops.forEach { stop ->
                stop.position = position
                position += 1.0
                store.insert(stop)
            }

            val mappedStops = store.select(OrderEntity::class)
                    .get()
                    .flatMap { it.tasks }
                    .mapNotNull { it.stop }

            // Remove all stops without task references (cleanup)
            store.select(StopEntity::class)
                    .get()
                    .subtract(mappedStops)
                    .toList()
                    .forEach {
                        store.delete(it)
                    }
        }
    }

    /**
     * Merges one stop into another
     * @param source Source stop
     * @param target Target stop
     */
    fun mergeInto(source: Stop, target: Stop): Completable {
        return Completable.fromCallable {
            val store = this.store.toBlocking()

            target.tasks.addAll(source.tasks)

            store.delete(source)
            store.update(target)
        }
    }

    /**
     * Updates stop order, placing a stop after another one
     * @param after Stop
     */
    fun move(stop: Stop, after: Stop?): Completable {
        return Completable.fromCallable {
            val store = this.store.toBlocking()

            val afterPosition = after?.position ?: 0.0

            // Get first stop where position is greater or null if there's none (last position)
            val nextAfter = store
                    .select(StopEntity::class)
                    .where(StopEntity.POSITION.gt(afterPosition))
                    .orderBy(StopEntity.POSITION.asc())
                    .limit(1)
                    .get()
                    .firstOrNull()

            when {
                nextAfter != null -> {
                    stop.position = (nextAfter.position + afterPosition) / 2
                }
                else -> {
                    // The stop after is the last one, simply increase position
                    stop.position = afterPosition + 1.0
                }
            }

            store.update(stop)
        }
    }

    /**
     * Remove all orders
     */
    fun removeAll(): Completable {
        return Completable.fromCallable {
            val store = this.store.toBlocking()

            store.select(StopEntity::class)
                    .get()
                    .forEach {
                        store.delete(it)
                    }
        }
    }

    /**
     * Merge one stop into another
     * @param other Stop to merge into
     */
    fun StopEntity.mergeInto(other: Stop) {
        this@StopRepository.mergeInto(source = this, target = other)
    }
}
