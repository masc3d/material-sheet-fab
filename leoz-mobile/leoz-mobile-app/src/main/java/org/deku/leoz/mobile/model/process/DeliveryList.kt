package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.*
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.model.service.toOrder
import org.deku.leoz.model.EventNotDeliveredReason
import org.deku.leoz.service.internal.DeliveryListService
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.requery.ObservableQuery
import sx.rx.*

/**
 * Delivery list model
 * Created by masc on 18.06.17.
 */
class DeliveryList : CompositeDisposableSupplier {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override val compositeDisposable by lazy { CompositeDisposable() }

    private val db: Database by Kodein.global.lazy.instance()
    private val deliveryListServive: DeliveryListService by Kodein.global.lazy.instance()

    //region Repositories
    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()
    //endregion

    //region Self-observing queries
    private val loadedParcelsQuery = ObservableQuery<ParcelEntity>(
            db.store.select(ParcelEntity::class)
                    .where(ParcelEntity.LOADING_STATE.eq(Parcel.State.LOADED))
                    .orderBy(ParcelEntity.MODIFICATION_TIME.desc())
                    .get()
    ).bind(this)

    private val damagedParcelsQuery = ObservableQuery<ParcelEntity>(
            db.store.select(ParcelEntity::class)
                    .where(ParcelEntity.DAMAGED.eq(true))
                    .orderBy(ParcelEntity.MODIFICATION_TIME.desc())
                    .get()
    ).bind(this)

    private val pendingParcelsQuery = ObservableQuery<ParcelEntity>(
            db.store.select(ParcelEntity::class)
                    .where(ParcelEntity.LOADING_STATE.eq(Parcel.State.PENDING))
                    .orderBy(ParcelEntity.MODIFICATION_TIME.desc())
                    .get()
    ).bind(this)
    //endregion

    /**
     * Damaged parcels
     */
    val damagedParcels = damagedParcelsQuery.result

    /**
     * Loaded parcels
     */
    val loadedParcels = loadedParcelsQuery.result

    val pendingParcels = pendingParcelsQuery.result

    //region Counters
    val orderTotalAmount = orderRepository.entitiesProperty.map { it.value.count() }
            .behave(this)

    val stopTotalAmount = stopRepository.entitiesProperty.map { it.value.count() }
            .behave(this)

    val parcelTotalAmount = parcelRepository.entitiesProperty.map { it.value.count() }
            .behave(this)

    val totalWeight = parcelRepository.entitiesProperty.map { it.value.sumByDouble { it.weight } }
            .behave(this)

    val orderAmount = loadedParcels.map { it.value.map { it.order }.distinct().count() }
            .behave(this)

    val parcelAmount = loadedParcels.map { it.value.count() }
            .behave(this)

    val stopAmount = loadedParcels.map { it.value.flatMap { it.order.tasks }.map { it.stop }.distinct().count() }
            .behave(this)

    val weight = loadedParcels.map { it.value.sumByDouble { it.weight } }
            .behave(this)
    //endregion

    init {
        log.info("COMPOSITES ${compositeDisposable.size()}")
    }

    val allowedEvents: List<EventNotDeliveredReason> by lazy {
        listOf(
                EventNotDeliveredReason.Damaged
                //TODO "Missing" reason is not present yet
        )
    }

    /**
     * Loads delivery list data from remote peer into local database
     * @param deliveryListId Delivery list id
     * @return Hot observable which completes with a list of stops
     */
    fun load(deliveryListId: Long): Observable<List<Stop>> {
        return Observable.fromCallable {
            val sw = Stopwatch.createStarted()

            // Retrieve delivery list
            val deliveryList = this.deliveryListServive.getById(id = deliveryListId)
            log.trace("Delivery list loaded in $sw orders [${deliveryList.orders.count()}] parcels [${deliveryList.orders.flatMap { it.parcels }.count()}]")

            // Process orders
            run {
                //region Post process orders, filter out duplicates
                val filteredOrders = deliveryList.orders.groupBy {
                    it.id
                }.map {
                    if (it.value.count() > 1) {
                        log.warn("Duplicate order id [${it.key}] parcel counts [${it.value.map { it.parcels.count() }.joinToString(", ")}]")
                    }
                    it.value.maxBy {
                        // In case of duplicates, prefer order with most parcels
                        it.parcels.count()
                    }
                }.filterNotNull()
                //endregion

                this.orderRepository
                        .save(filteredOrders.map {
                            it.toOrder()
                        })
                        .blockingGet()
            }

            // Process stops
            val stops = deliveryList.stops.map {
                Stop.create(
                        stopTasks = it.tasks.map {
                            val order = this.orderRepository.findById(it.orderId)

                            when {
                                order != null -> {
                                    when (it.stopType) {
                                        DeliveryListService.Task.Type.DELIVERY -> order.deliveryTask
                                        DeliveryListService.Task.Type.PICKUP -> order.pickupTask
                                    }
                                }
                                else -> {
                                    log.warn("Skipping order task. Referenced order [${it.orderId}] does not exist")
                                    null
                                }
                            }
                        }.filterNotNull()
                )
            }

            //region Post process stops, filter duplicates´
            val filteredStops = stops.flatMap { stop ->
                stop.tasks.map { task ->
                    Pair(stop, task)
                }
            }.groupBy {
                // Group by order task id to find duplicates
                it.second.id
            }.map {
                if (it.value.count() > 1) {
                    log.warn("Duplicate stop task [${it.key}], filtering all but the last one")
                }
                it.value.last().first
            }
            //endregion

            this.stopRepository
                    .save(filteredStops)
                    .blockingGet()

            log.trace("Delivery list transformed and stored in $sw")

            stops
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    log.error(it.message)
                }
                .toHotReplay()
    }
}