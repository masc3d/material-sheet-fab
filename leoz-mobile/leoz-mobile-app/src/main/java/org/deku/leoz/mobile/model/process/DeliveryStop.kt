package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.service.LocationCache
import org.deku.leoz.model.Event
import org.deku.leoz.model.EventNotDeliveredReason
import org.deku.leoz.model.ParcelService
import org.deku.leoz.model.Reason
import org.deku.leoz.service.internal.ParcelServiceV1
import org.slf4j.LoggerFactory
import sx.mq.mqtt.channel
import sx.requery.ObservableQuery
import sx.rx.CompositeDisposableSupplier
import sx.rx.behave
import sx.rx.bind

/**
 * Mobile delivery stop
 * Created by masc on 08.08.17.
 */
class DeliveryStop(
        val entity: StopEntity) : CompositeDisposableSupplier {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override val compositeDisposable = CompositeDisposable()

    private val db: Database by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()

    private val locationCache: LocationCache by Kodein.global.lazy.instance()
    private val mqttChannels: MqttEndpoints by Kodein.global.lazy.instance()

    private val identity: Identity by Kodein.global.lazy.instance()
    private val login: Login by Kodein.global.lazy.instance()

    /**
     * Allowed events for this stop
     */
    val allowedEvents: List<EventNotDeliveredReason> by lazy {
        mutableListOf(
                EventNotDeliveredReason.ABSENT,
                EventNotDeliveredReason.REFUSED,
                EventNotDeliveredReason.VACATION,
                EventNotDeliveredReason.ADDRESS_WRONG,
                EventNotDeliveredReason.MOVED,
                EventNotDeliveredReason.DAMAGED
        )
                .also {
                    if (services.contains(ParcelService.XCHANGE)) {
                        it.addAll(listOf(
                                EventNotDeliveredReason.XC_OBJECT_DAMAGED,
                                EventNotDeliveredReason.XC_OBJECT_NOT_READY,
                                EventNotDeliveredReason.XC_OBJECT_WRONG
                        ))
                    }
                    if (services.contains(ParcelService.CASH_ON_DELIVERY)) {
                        it.add(
                                EventNotDeliveredReason.NO_PAYMENT)
                    }
                    if (services.contains(ParcelService.IDENT_CONTRACT_SERVICE)) {
                        it.add(
                                EventNotDeliveredReason.NO_IDENT)
                    }
                }
                .distinct()
    }

    /**
     * Observable stop query
     */
    private val stopQuery = ObservableQuery<StopEntity>(
            name = "Delivery stop",
            query = db.store.select(StopEntity::class)
                    .where(StopEntity.ID.eq(entity.id))
                    .get()
    )
            .bind(this)

    /**
     * Observable stop parcels query
     */
    private val stopParcelsQuery = ObservableQuery<ParcelEntity>(
            name = "Delivery stop parcels",
            query = db.store.select(ParcelEntity::class)
                    .where(ParcelEntity.ORDER_ID.`in`(this.entity.tasks.map { it.order.id }))
                    .orderBy(ParcelEntity.MODIFICATION_TIME.desc())
                    .get()
    )
            .bind(this)

    /** Observable stop */
    val stop = stopQuery.result.map { it.value.first() }
            .behave(this)

    /** All parcels of this stop. Also includes missing parcels */
    val parcels = this.stopParcelsQuery.result.map { it.value }
            .behave(this)

    /** Stop orders */
    val orders = this.parcels.map { it.map { it.order as OrderEntity }.distinct() }
            .behave(this)

    /** Loaded parcels for this stop */
    val loadedParcels = this.parcels.map {
        /** Automatically excludes missing parcels */
        it.filter { it.loadingState == Parcel.LoadingState.LOADED }
    }

    /** Parcels pending delivery for this stop */
    val pendingParcels = this.loadedParcels.map { it.filter { it.deliveryState == Parcel.DeliveryState.PENDING } }
            .behave(this)

    /** Delivered parcels of this stop */
    val deliveredParcels = this.parcels.map { it.filter { it.deliveryState == Parcel.DeliveryState.DELIVERED } }
            .behave(this)

    /** Undelivered parcels of this stop */
    val undeliveredParcels = this.parcels.map { it.filter { it.deliveryState == Parcel.DeliveryState.UNDELIVERED } }
            .behave(this)

    val parcelsByEvent by lazy {
        mapOf(
                *this.allowedEvents.map { reason ->
                    Pair(
                            reason,
                            this.parcels.map { it.filter { it.reason == reason } }
                                    .behave(this)
                    )
                }.toTypedArray()
        )
    }

    //region Counters
    val orderTotalAmount = this.orders.map { it.count() }
            .distinctUntilChanged()
            .behave(this)

    val parcelTotalAmount = this.loadedParcels.map { it.count() }
            .distinctUntilChanged()
            .behave(this)

    val totalWeight = this.parcels.map { it.sumByDouble { it.weight } }
            .distinctUntilChanged()
            .behave(this)

    val deliveredOrdersAmount = this.orders.map { it.filter { it.parcels.all { it.deliveryState == Parcel.DeliveryState.DELIVERED } }.count() }
            .distinctUntilChanged()
            .behave(this)

    val deliveredParcelAmount = this.deliveredParcels.map { it.count() }
            .distinctUntilChanged()
            .behave(this)

    val deliveredParcelsWeight = this.deliveredParcels.map { it.sumByDouble { it.weight } }
            .distinctUntilChanged()
            .behave(this)
//endregion

    /** Signature as svg */
    var signatureSvg: String? = null

    /** Recipient name */
    var recipientName: String? = null

    /**
     * Services for this stop
     */
    val services by lazy {
        // Normally, we'd expect all services to match, just to make sure.
        this.orders.blockingFirst()
                .flatMap { it.tasks }
                .flatMap { it.services }
                .distinct()
    }

    val isSignatureRequired: Boolean
        get() = this.deliveredParcels.blockingFirst().count() > 0


    val canClose: Boolean
        get() =
            pendingParcels.blockingFirst().count() == 0 &&
                    entity.state == Stop.State.PENDING

    val canCloseWithEvent: Boolean
        get() = this.canClose && deliveredParcels.blockingFirst().count() == 0

    val canCloseWithDelivery: Boolean
        get() = this.canClose && deliveredParcels.blockingFirst().count() > 0

    val canCloseWithDeliveryToNeighbor: Boolean
        get() = this.canCloseWithDelivery && !this.services.contains(ParcelService.NO_ALTERNATIVE_DELIVERY)

    val canCloseWithDeliveryToPostbox: Boolean
        get() = this.services.contains(ParcelService.POSTBOX_DELIVERY) &&
                !this.services.contains(ParcelService.NO_ALTERNATIVE_DELIVERY)

    /**
     * Cash amount to collect for this stop
     */
    val cashAmountToCollect by lazy {
        this.orders.blockingFirst()
                .mapNotNull { it.meta.firstValueByTypeOrNull(Order.CashService::class.java)?.cashAmount }
                .sum()
    }

    /**
     * Resets all parcels to pending state and removes all event information
     */
    fun reset(): Completable {
        val stop = this.entity

        return db.store.withTransaction {
            stop.state = Stop.State.PENDING
            update(stop)

            stop.tasks
                    .flatMap { it.order.parcels }
                    .forEach { parcel ->
                        parcel.deliveryState = Parcel.DeliveryState.PENDING
                        parcel.reason = null
                        update(parcel)
                    }
        }
                .toCompletable()
                .subscribeOn(Schedulers.computation())
    }

    /**
     * Deliver a single parcel
     */
    fun deliver(parcel: ParcelEntity): Completable {
        val stop = this.entity

        return db.store.withTransaction {
            // In case the stop has been closed before, re-open on delivery
            if (stop.state == Stop.State.CLOSED) {
                stop.state = Stop.State.PENDING
                update(stop)
            }

            // TODO: support order level events
            parcels.blockingFirst()
                    .filter {
                        it.deliveryState == Parcel.DeliveryState.UNDELIVERED
                    }
                    .forEach {
                        it.deliveryState = Parcel.DeliveryState.PENDING
                        it.reason = null
                        update(it)
                    }

            /** Mark parcel delivered */
            parcel.deliveryState = Parcel.DeliveryState.DELIVERED

            /** In case the parcel was missing, make sure to correct the loading state */
            parcel.loadingState = Parcel.LoadingState.LOADED

            update(parcel)
        }
                .toCompletable()
                .subscribeOn(Schedulers.computation())
    }

    /**
     * Assign event reason to entire stop
     */
    fun assignEventReason(reason: EventNotDeliveredReason): Completable {
        val stop = this.entity

        return db.store.withTransaction {
            // In case the stop has been closed before, re-open on delivery
            if (stop.state == Stop.State.CLOSED) {
                stop.state = Stop.State.PENDING
                update(stop)
            }

            parcels.blockingFirst().forEach {
                it.deliveryState = Parcel.DeliveryState.UNDELIVERED
                it.reason = reason
                update(it)
            }
        }
                .toCompletable()
                .subscribeOn(Schedulers.computation())
    }

    fun finalize(): Completable {
        val stop = this.entity

        return db.store.withTransaction {
            // Close the stop persistently
            stop.state = Stop.State.CLOSED
            update(stop)
        }
                .toCompletable()
                .concatWith(Completable.fromCallable {
                    //region Send status events on stop close
                    val lastLocation = this@DeliveryStop.locationCache.lastLocation

                    val parcels = stop.tasks.flatMap { it.order.parcels }

                    // TODO: unify parcel message send, as this is replicated eg., in DeliveryStop
                    // Send compound closing stop parcel message
                    mqttChannels.central.main.channel().send(
                            ParcelServiceV1.ParcelMessage(
                                    userId = this.login.authenticatedUser?.id,
                                    nodeId = this.identity.uid.value,
                                    deliveredInfo = ParcelServiceV1.ParcelMessage.DeliveredInfo(
                                            signature = signatureSvg,
                                            recipient = recipientName
                                    ),
                                    events = parcels.map {
                                        ParcelServiceV1.Event(
                                                event = when {
                                                    it.deliveryState == Parcel.DeliveryState.DELIVERED -> Event.DELIVERED.value
                                                    else -> Event.DELIVERY_FAIL.value
                                                },
                                                reason = it.reason?.reason?.id ?: Reason.NORMAL.id,
                                                parcelId = it.id,
                                                latitude = lastLocation?.latitude,
                                                longitude = lastLocation?.longitude
                                        )
                                    }.toTypedArray()
                            )
                    )
                    //endregion
                })
                .subscribeOn(Schedulers.computation())
    }
}