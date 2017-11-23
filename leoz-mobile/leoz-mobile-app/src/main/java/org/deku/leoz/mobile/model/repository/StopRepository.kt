package org.deku.leoz.mobile.model.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.deku.leoz.mobile.model.entity.*
import org.slf4j.LoggerFactory
import sx.requery.scalarOr
import java.util.*

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
     * Update stop including modification time
     */
    override fun update(entity: StopEntity): Single<StopEntity> {
        entity.modificationTime = Date()
        return super.update(entity)
    }

    /**
     * Updates stop states from parcel states
     * @param stops List of stops to update
     */
    fun updateStopStateFromParcels(stops: List<Stop>): Completable {
        return Completable.fromCallable {
            stops
                    .filter { it.state == Stop.State.NONE }
                    .forEach { stop ->
                        val parcels = stop.tasks.flatMap { it.order.parcels }

                        if (parcels.any { it.state == Parcel.State.LOADED }) {
                            stop.state = Stop.State.PENDING
                            this.update(stop as StopEntity)
                                    .blockingGet()
                        }
                    }
        }
    }

    fun updateStopStateFromParcels(stop: Stop): Completable =
            this.updateStopStateFromParcels(listOf(stop))

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

            this.updateStopStateFromParcels(stops)
                    .blockingGet()
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
     * @param stop The stop to move positionally
     * @param after The stop after which stop should be moved or null for first position
     * @param persist Persist the change against the entity store, otherwise merely applies
     * the change to this repository's reference cache
     */
    fun move(
            stop: Stop,
            after: Stop?,
            persist: Boolean = false
    ): Completable {
        return Completable.fromCallable {
            val store = this.store.toBlocking()

            val afterPosition = after?.position ?: 0.0

            // Get first stop where position is greater or null if there's none (last position)
            val nextAfter = when (persist) {
                true -> {
                    store
                            .select(StopEntity::class)
                            .where(StopEntity.POSITION.gt(afterPosition))
                            .orderBy(StopEntity.POSITION.asc())
                            .limit(1)
                            .get()
                            .firstOrNull()
                }
                false -> {
                    // If changes are not persisted, query reference cache instead for consistency
                    this.entities
                            .asSequence()
                            .filter { it.position > afterPosition }
                            .sortedBy { it.position }
                            .firstOrNull()
                }
            }


            when {
                nextAfter != null -> {
                    stop.position = (nextAfter.position + afterPosition) / 2
                }
                else -> {
                    // The stop after is the last one, simply increase position
                    stop.position = afterPosition + 1.0
                }
            }

            if (persist)
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
