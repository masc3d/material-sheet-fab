package org.deku.leoz.mobile.model.repository

import android.databinding.Observable
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import org.deku.leoz.mobile.model.entity.*
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.requery.scalarOr
import sx.rx.ObservableRxProperty

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
    fun findById(id: Int): Stop? {
        return store.select(StopEntity::class)
                .where(StopEntity.ID.eq(id))
                .get()
                .firstOrNull()
    }
    
    /**
     * Finds a suitabtle existing stop compatible with this task
     * @param task Order task
     */
    fun findStopForTask(task: OrderTask): Stop? {
        return entities
                .flatMap { it.tasks }
                .firstOrNull {
                    it.services.size == task.services.size &&
                            it.services.containsAll(task.services) &&
                            it.hasCompatibleAppointmentsWith(task) &&
                            it.address.isCompatibleStopAddressFor(task.address)
                }
                ?.stop
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
}