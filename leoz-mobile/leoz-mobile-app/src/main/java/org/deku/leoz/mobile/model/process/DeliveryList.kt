package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.model.service.toOrder
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.rx.toHotIoObservable
import org.deku.leoz.mobile.service.LocationCache
import org.deku.leoz.model.*
import org.deku.leoz.service.internal.DeliveryListService
import org.deku.leoz.service.internal.OrderService
import org.deku.leoz.service.internal.ParcelServiceV1
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.mq.mqtt.channel
import sx.requery.ObservableQuery
import sx.requery.ObservableTupleQuery
import sx.rx.*

/**
 * Delivery list model
 * Created by masc on 18.06.17.
 */
class DeliveryList : CompositeDisposableSupplier {
    override val compositeDisposable by lazy { CompositeDisposable() }

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val db: Database by Kodein.global.lazy.instance()
    private val deliveryListServive: DeliveryListService by Kodein.global.lazy.instance()
    private val orderService: OrderService by Kodein.global.lazy.instance()
    private val locationCache: LocationCache by Kodein.global.lazy.instance()

    //region Repositories
    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()
    //endregion

    private val identity: Identity by Kodein.global.lazy.instance()
    private val login: Login by Kodein.global.lazy.instance()

    private val mqttChannels: MqttEndpoints by Kodein.global.lazy.instance()

    //region Self-observing queries
    private val loadedParcelsQuery = ObservableQuery<ParcelEntity>(
            name = "Loaded parcels",
            query = db.store.select(ParcelEntity::class)
                    .where(ParcelEntity.LOADING_STATE.eq(Parcel.LoadingState.LOADED))
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
                    .where(ParcelEntity.LOADING_STATE.eq(Parcel.LoadingState.PENDING))
                    .orderBy(ParcelEntity.MODIFICATION_TIME.desc())
                    .get()
    ).bind(this)

    private val missingParcelsQuery = ObservableQuery<ParcelEntity>(
            name = "Missing parcels",
            query = db.store.select(ParcelEntity::class)
                    .where(ParcelEntity.LOADING_STATE.eq(Parcel.LoadingState.MISSING))
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
     * Missing parcels
     */
    val missingParcels = missingParcelsQuery.result

    /**
     * Stops with loaded parcels
     */
    val stops = loadedParcels.map { it.value.flatMap { it.order.tasks }.mapNotNull { it.stop }.distinct() }
            .behave(this)

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

    val stopAmount = this.stops.map { it.count() }
            .behave(this)

    val weight = loadedParcels.map { it.value.sumByDouble { it.weight } }
            .behave(this)
    //endregion

    init {
    }

    val allowedEvents: List<EventNotDeliveredReason> by lazy {
        listOf(
                EventNotDeliveredReason.DAMAGED
                //TODO "Missing" reason is not present yet
        )
    }

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

            // Retrieve delivery list
            val deliveryListId = deliveryListNumber.value.toLong()
            val deliveryList = this.deliveryListServive.getById(id = deliveryListId)
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
                                val order = orderRepository.findById(it.orderId)

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
                    .subscribeOn(Schedulers.computation())
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
            val orders = this.orderService.get(parcelScan = unitNumber.value)
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
        return Completable.fromCallable {
            val createdOrderCount = orderRepository
                    .merge(listOf(order))
                    .blockingGet()

            // If this order existed already, we're done
            if (createdOrderCount == 0)
                return@fromCallable Unit

            order.tasks.forEach { task ->
                var stop = stopRepository.findStopForTask(task)

                if (stop != null) {
                    stop.tasks.add(task)
                    db.store.update(stop).blockingGet()
                } else {
                    stop = Stop.create(tasks = listOf(task))
                    db.store.insert(stop).blockingGet()
                }
            }
        }
                .subscribeOn(Schedulers.computation())
    }

    /**
     * Load order based on unit number and merge orders into local entity store
     * @param unitNumber Unit number
     */
    fun load(unitNumber: UnitNumber): Observable<Order> {
        return Observable.fromCallable {
            val orders = this.orderService.get(parcelScan = unitNumber.value)
                    .distinctOrders()
                    .map { it.toOrder() }

            db.store.withTransaction {
                orderRepository
                        .merge(orders)
                        .blockingGet()

                orders.first()
            }
                    .subscribeOn(Schedulers.computation())
                    .blockingGet()
        }
                .toHotIoObservable(log)
    }

    /**
     * Finalizes the loading process, marking all parcels with pending loading state as missing
     */
    fun finalize(): Completable {
        return db.store.withTransaction {
            // Set all pending parcels to MISSING
            val pendingParcels = parcelRepository.entities.filter { it.loadingState == Parcel.LoadingState.PENDING }

            pendingParcels.forEach {
                it.loadingState = Parcel.LoadingState.MISSING
                update(it)
            }

            // Set all stops which contain LOADED parcels to PENDING
            val pendingStops = parcelRepository.entities
                    .filter { it.loadingState == Parcel.LoadingState.LOADED }
                    .flatMap { it.order.tasks.mapNotNull { it.stop } }
                    .distinct()

            pendingStops.forEach {
                it.state = Stop.State.PENDING
                update(it)
            }

            // Reset state for remaining stops
            stopRepository.entities
                    .subtract(pendingStops)
                    .forEach {
                        it.state = Stop.State.NONE
                        update(it)
                    }
        }
                .toCompletable()
                .concatWith(Completable.fromCallable {
                    // Select parcels for which to send status events
                    val parcels = parcelRepository.entities.filter {
                        it.loadingState == Parcel.LoadingState.LOADED ||
                                it.loadingState == Parcel.LoadingState.MISSING
                    }

                    val lastLocation = this@DeliveryList.locationCache.lastLocation

                    // TODO: unify parcel message send, as this is replicated eg., in DeliveryStop
                    // Send compound parcel message with loading states
                    mqttChannels.central.main.channel().send(
                            ParcelServiceV1.ParcelMessage(
                                    userId = this.login.authenticatedUser?.id,
                                    nodeId = this.identity.uid.value,
                                    events = parcels.map {
                                        ParcelServiceV1.Event(
                                                event = when {
                                                    it.loadingState == Parcel.LoadingState.LOADED -> Event.IN_DELIVERY.value
                                                    else -> Event.DELIVERY_FAIL.value
                                                },
                                                reason = when {
                                                    it.isDamaged -> Reason.PARCEL_DAMAGED.id
                                                    it.loadingState == Parcel.LoadingState.MISSING -> Reason.PARCEL_MISSING.id
                                                    else -> Reason.NORMAL.id
                                                },
                                                parcelId = it.id,
                                                latitude = lastLocation?.altitude,
                                                longitude = lastLocation?.longitude
                                        )
                                    }.toTypedArray()
                            )
                    )
                })
                .subscribeOn(Schedulers.computation())
    }
}