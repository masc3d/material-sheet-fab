package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.*
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.model.service.toOrder
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.rx.toHotIoObservable
import org.deku.leoz.model.DekuDeliveryListNumber
import org.deku.leoz.model.DekuUnitNumber
import org.deku.leoz.model.UnitNumber
import org.deku.leoz.service.internal.DeliveryListService
import org.deku.leoz.service.internal.OrderService
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.mq.mqtt.channel
import sx.requery.ObservableQuery
import sx.requery.ObservableTupleQuery
import sx.rx.CompositeDisposableSupplier
import sx.rx.bind

/**
 * Delivery list model
 * Created by masc on 18.06.17.
 */
class DeliveryList : CompositeDisposableSupplier {
    override val compositeDisposable by lazy { CompositeDisposable() }

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val db: Database by Kodein.global.lazy.instance()

    // Repositories
    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()

    //region Self-observing queries
    private val parcelsQuery = ObservableQuery<ParcelEntity>(
            name = "Delivery list parcels",
            query = db.store.select(ParcelEntity::class)
                    .where(ParcelEntity.STATE.ne(Parcel.State.DELIVERED))
                    .orderBy(ParcelEntity.MODIFICATION_TIME.desc())
                    .get()
    ).bind(this)

    private val loadedParcelsQuery = ObservableQuery<ParcelEntity>(
            name = "Loaded parcels",
            query = db.store.select(ParcelEntity::class)
                    .where(ParcelEntity.STATE.eq(Parcel.State.LOADED))
                    .orderBy(ParcelEntity.MODIFICATION_TIME.desc())
                    .get()
    ).bind(this)

    private val damagedParcelsQuery = ObservableQuery<ParcelEntity>(
            name = "Damaged parcels",
            query = db.store.select(ParcelEntity::class)
                    .where(ParcelEntity.DAMAGED.eq(true))
                    .orderBy(ParcelEntity.MODIFICATION_TIME.desc())
                    .get()
    ).bind(this)

    private val pendingParcelsQuery = ObservableQuery<ParcelEntity>(
            name = "Pending parcels",
            query = db.store.select(ParcelEntity::class)
                    .where(ParcelEntity.STATE.eq(Parcel.State.PENDING))
                    .orderBy(ParcelEntity.MODIFICATION_TIME.desc())
                    .get()
    ).bind(this)

    private val deliveryListIdQuery = ObservableTupleQuery<Long>(
            name = "Delivery list ids",
            query = db.store.select(OrderEntity.DELIVERY_LIST_ID)
                    .distinct()
                    .from(OrderEntity::class)
                    .where(OrderEntity.DELIVERY_LIST_ID.notNull())
                    .get(),
            transform = { it.get<Long>(0) }
    ).bind(this)
    //endregion

    /**
     * Behavioral observable list of delivery list ids
     */
    val ids = deliveryListIdQuery.result

    /**
     * Damaged parcels
     */
    val damagedParcels = damagedParcelsQuery.result

    /**
     * Loaded parcels
     */
    val loadedParcels = loadedParcelsQuery.result

    /**
     * Pending parcels
     */
    val pendingParcels = pendingParcelsQuery.result

    /**
     * Delivery list parcels (all non-delivered parcels)
     */
    val parcels = parcelsQuery.result

    /**
     * Extension method for filtering distinct service orders
     */
    private fun List<OrderService.Order>.distinctOrders(): List<OrderService.Order> {
        return this.groupBy {
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
    }

    /**
     * Extension method for filtering distinct stops
     */
    private fun List<Stop>.distinctStops(): List<Stop> {
        return this.flatMap { stop ->
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
    }

    /**
     * Loads delivery list data from remote peer and merge into local database
     * @param deliveryListNumber Delivery list id
     * @return Hot observable which completes with a list of stops
     */
    fun load(deliveryListNumber: DekuDeliveryListNumber): Observable<List<Stop>> {
        val sw = Stopwatch.createStarted()

        return Observable.fromCallable {

            val deliveryListService = Kodein.global.instance<DeliveryListService>()

            // Retrieve delivery list
            val deliveryListId = deliveryListNumber.value.toLong()
            val deliveryList = deliveryListService.getById(id = deliveryListId)
            log.trace("Delivery list loaded in $sw orders [${deliveryList.orders.count()}] parcels [${deliveryList.orders.flatMap { it.parcels }.count()}]")

            // Process orders
            db.store.withTransaction {
                run {
                    val orders = deliveryList.orders
                            .distinctOrders()

                    orderRepository
                            .merge(orders.map {
                                it.toOrder(deliveryListId)
                            })
                            .blockingGet()
                }

                // Process stops
                val stops = deliveryList.stops.map {
                    Stop.create(
                            tasks = it.tasks.map {
                                val order = orderRepository
                                        .findById(it.orderId)
                                        .blockingGet()

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
                        .distinctStops()

                stopRepository
                        .merge(stops)
                        .blockingAwait()

                log.trace("Delivery list transformed and stored in $sw")

                stops
            }
                    .subscribeOn(db.scheduler)
                    .blockingGet()
        }
                .toHotIoObservable(log)
    }

    /**
     * Retrieve order for a single unit
     * @param unitNumber Unit number
     */
    fun retrieveOrder(unitNumber: UnitNumber): Observable<Order> {
        return Observable.fromCallable {
            val orderService = Kodein.global.instance<OrderService>()

            val orders = orderService.get(parcelScan = unitNumber.value)
                    .distinctOrders()
                    .map { it.toOrder() }

            orders.first()
        }
                .toHotIoObservable(log)
    }

    /**
     * Merge a single order into the entity store
     */
    fun mergeOrder(order: Order): Completable {
        return db.store.withTransaction {
            val createdOrderCount = orderRepository
                    .merge(listOf(order))
                    .blockingGet()

            val task = order.deliveryTask
            var stop = stopRepository
                    .findStopForTask(task)
                    .blockingGet()

            if (stop != null) {
                stop.tasks.add(task)
                update(stop)
            } else {
                stop = Stop.create(tasks = listOf(task))
                stopRepository
                        .merge(listOf(stop))
                        .blockingAwait()
            }
        }
                .toCompletable()
                .subscribeOn(db.scheduler)
    }
}