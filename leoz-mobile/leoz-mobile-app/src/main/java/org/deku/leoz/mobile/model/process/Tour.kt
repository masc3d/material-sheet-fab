package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.log.user
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.repository.OrderRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.model.service.toOrder
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.rx.toHotIoObservable
import org.deku.leoz.model.DekuDeliveryListNumber
import org.deku.leoz.model.TourIdentification
import org.deku.leoz.model.UnitNumber
import org.deku.leoz.model.median
import org.deku.leoz.service.internal.OrderService
import org.deku.leoz.service.internal.TourServiceV1
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.log.slf4j.trace
import sx.mq.mqtt.channel
import sx.requery.ObservableQuery
import sx.requery.ObservableTupleQuery
import sx.rx.CompositeDisposableSupplier
import sx.rx.bind
import kotlin.properties.Delegates

/**
 * Delivery process model
 * Created by 27694066 on 09.05.2017.
 */
class Tour : CompositeDisposableSupplier {
    override val compositeDisposable by lazy { CompositeDisposable() }

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val db: Database by Kodein.global.lazy.instance()

    // Repositories
    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()

    // Services
    private val tourService: TourServiceV1 by Kodein.global.lazy.instance()
    private val orderService: OrderService by Kodein.global.lazy.instance()

    private val login: Login by Kodein.global.lazy.instance()
    private val identity: Identity by Kodein.global.lazy.instance()
    private val mqttEndpoints: MqttEndpoints by Kodein.global.lazy.instance()

    //region Self-observable queries
    private val pendingStopsQuery = ObservableQuery<StopEntity>(
            name = "Pending stops",
            query = db.store.select(StopEntity::class)
                    .where(StopEntity.STATE.eq(Stop.State.PENDING))
                    .orderBy(StopEntity.POSITION.asc())
                    .get()
    ).bind(this)

    private val closedStopsQuery = ObservableQuery<StopEntity>(
            name = "Closed stops",
            query = db.store.select(StopEntity::class)
                    .where(StopEntity.STATE.eq(Stop.State.CLOSED))
                    .orderBy(StopEntity.MODIFICATION_TIME.desc())
                    .get()
    ).bind(this)

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
    val deliveryListIds = deliveryListIdQuery.result

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
     * Load tour
     * @return mobile tour stops
     */
    fun store(tour: TourServiceV1.Tour): List<Stop> {
        val sw = Stopwatch.createStarted()

        log.trace { "Storing tour [${tour.uid}] with orders [${tour.orders?.count()}] parcels [${tour.orders?.flatMap { it.parcels }?.count()}]" }

        // Process orders
        return db.store.withTransaction {
            run {
                val orders = tour.orders
                        ?.distinctOrders()
                        ?: listOf()

                // masc20180405. it has been seen that app deadlocks in release
                // builds when requery @Bindable is used on `Order` entity.
                // TODO: investigate

                orderRepository
                        .merge(orders.map {
                            it.toOrder(tour.customId?.toLongOrNull())
                        })
                        .blockingGet()
            }

            // Process stops
            val stops = tour.stops
                    ?.map { srcStop ->
                        Stop.create(tasks = srcStop.tasks.map {
                            val order = orderRepository
                                    .findById(it.orderId)
                                    .blockingGet()

                            when {
                                order != null -> {
                                    when (it.taskType) {
                                        TourServiceV1.Task.Type.DELIVERY -> order.deliveryTask
                                        TourServiceV1.Task.Type.PICKUP -> order.pickupTask
                                    }
                                }
                                else -> {
                                    log.warn("Skipping order task. Referenced order [${it.orderId}] does not exist")
                                    null
                                }
                            }
                        }
                                .filterNotNull()
                        ).also {
                            // Add route meta data to stop
                            srcStop.route?.also { route ->
                                it.eta = route.eta?.median()
                            }
                        }
                    }
                    ?: listOf()

            stopRepository
                    .merge(stops)
                    .blockingAwait()

            log.trace("Stored tour in $sw")

            stops
        }
                .subscribeOn(db.scheduler)
                .blockingGet()
    }

    /**
     * Load tour by delivery list id
     * @param deliveryListNumber delivery list id
     * @return hot observable which completes with a list of stops
     */
    fun load(deliveryListNumber: DekuDeliveryListNumber): Observable<List<Stop>> {
        val sw = Stopwatch.createStarted()

        log.user { "Loads tour by delivery list id [${deliveryListNumber.value}]" }

        return Observable.fromCallable {
            val tour = tourService.getByCustomId(
                    customId = deliveryListNumber.value)

            this.store(tour)
        }
                .toHotIoObservable(log)
    }

    /**
     * Load tour by delivery list id
     * @param deliveryListNumber delivery list id
     * @return hot observable which completes with a list of stops
     */
    fun load(tourIdent: TourIdentification): Observable<List<Stop>> {
        val sw = Stopwatch.createStarted()

        log.user { "Loads tour [${tourIdent}]" }

        return Observable.fromCallable {
            val tour = tourService.getByUid("${tourIdent.uid}")

            this.store(tour)
        }
                .toHotIoObservable(log)
    }

    /**
     * Retrieve order for a single unit
     * @param unitNumber unit number
     */
    fun retrieveOrder(unitNumber: UnitNumber): Observable<Order> {
        return Observable.fromCallable {
            val orders = orderService.get(
                    labelRef = null,
                    custRef = null,
                    parcelScan = unitNumber.value
            )
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
        log.user { "Merges order [${order.id}]" }

        return db.store.withTransaction {
            orderRepository
                    .merge(listOf(order))
                    .blockingAwait()

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

    val pendingStops = this.pendingStopsQuery.result

    val closedStops = this.closedStopsQuery.result

    init {
        // Send tour update when pending stops change
        this.pendingStops.map { it.value }
                .subscribe { parcels ->
                    log.trace("Sending tour update")

                    // Send tour update
                    TourServiceV1.TourUpdate(tour = TourServiceV1.Tour(
                            nodeUid = identity.uid.value,
                            userId = login.authenticatedUser?.id?.toLong() ?: 0,
                            stops = parcels.map { stop ->
                                TourServiceV1.Stop(
                                        tasks = stop.tasks.map {
                                            TourServiceV1.Task(
                                                    orderId = it.order.id,
                                                    appointmentStart = it.appointmentStart,
                                                    appointmentEnd = it.appointmentEnd,
                                                    taskType = when (it.type) {
                                                        OrderTask.TaskType.DELIVERY -> TourServiceV1.Task.Type.DELIVERY
                                                        OrderTask.TaskType.PICKUP -> TourServiceV1.Task.Type.PICKUP
                                                    }
                                            )
                                        }
                                )
                            }
                    )).also {
                        this.mqttEndpoints.central.main.channel().sendAsync(it)
                                .subscribeOn(Schedulers.io())
                                .subscribeBy(onError = {
                                    log.error("Error sending tour update [${it.message}]", it)
                                })
                    }
                }
                .bind(this)
    }

    /**
     * The currently active stop.
     * Setting a stop active will also set its state to PENDING if it has no state
     */
    var activeStop: TourStop? by Delegates.observable<TourStop?>(null, { _, o, v ->
        o?.dispose()

        v?.entity?.address?.also {
            log.user { "Activates stop [$it]" }
        }

        if (v != null) {
            // If stop has no state, reset to pending
            if (v.entity.state == Stop.State.NONE) {
                v.entity.state = Stop.State.PENDING
                stopRepository.update(v.entity)
                        .subscribeOn(db.scheduler)
                        .subscribe()
            }
        }
    })
}