package org.deku.leoz.mobile.model.repository

import android.databinding.Observable
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
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
) : ObservingRepository<StopEntity>(StopEntity::class, store) {
    private val log = LoggerFactory.getLogger(this.javaClass)

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
     * Merges order task into existing stop or creates a new one
     * @param orderTask Order task
     */
    fun save(orderTask: OrderTask) {

    }

    /**
     * Merge a batch of stops into the database.
     * Stops which reference the same order tasks will be removed in order to avoid duplicates.
     */
    fun save(stops: List<Stop>): Completable {
        val sw = Stopwatch.createStarted()
        return store.withTransaction {
            stops.forEach { stop ->
                // Remove stops which reference the same order tasks to avoid duplicates
                delete(stop.tasks
                        .map { it.stop }
                        .distinct()
                        .filterNotNull()
                )

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
                .subscribeOn(Schedulers.computation())
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
        }
                .toCompletable()
                .subscribeOn(Schedulers.computation())
    }
}