package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.mq.MimeType
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.mq.sendFile
import org.deku.leoz.mobile.service.LocationCache
import org.deku.leoz.model.*
import org.deku.leoz.service.internal.ParcelServiceV1
import org.slf4j.LoggerFactory
import sx.mq.mqtt.channel
import sx.requery.ObservableQuery
import sx.rx.CompositeDisposableSupplier
import sx.rx.behave
import sx.rx.bind
import java.util.*

/**
 * Mobile delivery stop
 * Created by masc on 08.08.17.
 */
class DeliveryStop(
        val entity: StopEntity) : CompositeDisposableSupplier {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override val compositeDisposable = CompositeDisposable()

    private val db: Database by Kodein.global.lazy.instance()
    private val schedulers: org.deku.leoz.mobile.rx.Schedulers by Kodein.global.lazy.instance()

    private val locationCache: LocationCache by Kodein.global.lazy.instance()
    private val mqttEndpoints: MqttEndpoints by Kodein.global.lazy.instance()

    private val identity: Identity by Kodein.global.lazy.instance()
    private val login: Login by Kodein.global.lazy.instance()

    /**
     * Allowed events for this stop (all levels)
     */
    val allowedEvents: List<EventNotDeliveredReason> by lazy {
        mutableListOf(
                // Ordered by frequency of appearance

                EventNotDeliveredReason.REFUSED,
                EventNotDeliveredReason.ABSENT,
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
     * Allowed parcel level events
     */
    val allowedParcelEvents by lazy {
        this.allowedEvents.filter {
            when (it) {
                EventNotDeliveredReason.DAMAGED -> true
                else -> false
            }
        }
    }

    /**
     * Allowed order level events
     */
    val allowedOrderEvents by lazy {
        this.allowedEvents.filter {
            when (it) {
                EventNotDeliveredReason.REFUSED,
                EventNotDeliveredReason.XC_OBJECT_DAMAGED,
                EventNotDeliveredReason.XC_OBJECT_NOT_READY,
                EventNotDeliveredReason.XC_OBJECT_WRONG -> true
                else -> false
            }
        }
    }

    /**
     * Allowed stop level events
     */
    val allowedStopEvents by lazy {
        this.allowedEvents.subtract(this.allowedParcelEvents).toList()
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
     * Observable stop tasks query
     */
    private val stopOrderTasksQuery = ObservableQuery<OrderTaskEntity>(
            name = "Delivery stop tasks",
            query = db.store.select(OrderTaskEntity::class)
                    .where(OrderTaskEntity.STOP_ID.eq(entity.id))
                    .get()
    )
            .bind(this)

    /**
     * Observable stop parcels query
     */
    private val stopParcelsQuery = ObservableQuery<ParcelEntity>(
            name = "Delivery stop parcels",
            query = db.store.select(ParcelEntity::class)
                    .join(OrderTaskEntity::class)
                    .on(OrderTaskEntity.STOP_ID.eq(this.entity.id))
                    .and(OrderTaskEntity.ORDER_ID.eq(ParcelEntity.ORDER_ID))
                    .get()
    )
            .bind(this)

    /** Observable stop */
    val stop = stopQuery.result.map { it.value.first() }
            .behave(this)

    /**
     * All parcels of this stop. Also includes missing parcels
     * As requery has the shortcoming of not firing self observable queries via joins, have to merge manually
     * */
    val parcels = Observable.merge(
            this.stopParcelsQuery.result.map {
                it.value.sortedByDescending { it.modificationTime }
            },

            this.stopOrderTasksQuery.result.map {
                it.value.flatMap {
                    it.order.parcels
                            .sortedByDescending { it.modificationTime }
                            .map { it as ParcelEntity }
                }
                        .distinct()
            }
    )
            .behave(this)

    /** Stop orders */
    val orders = this.parcels.map { it.map { it.order as OrderEntity }.distinct() }
            .behave(this)

    /** Loaded parcels for this stop */
    val loadedParcels = this.parcels.map { it.filter { it.state == Parcel.State.LOADED } }
            .behave(this)

    /** Parcels pending delivery for this stop */
    val pendingParcels = this.loadedParcels.map { it.filter { it.reason == null } }
            .behave(this)

    /** Missing parcels */
    val missingParcels = this.parcels.map { it.filter { it.state == Parcel.State.MISSING } }
            .behave(this)

    /** Damaged parcels */
    val damagedParcels = this.parcels.map { it.filter { it.isDamaged } }
            .behave(this)

    /** Delivered parcels of this stop */
    val deliveredParcels = this.parcels.map { it.filter { it.state == Parcel.State.DELIVERED } }
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

    val parcelTotalAmount = this.parcels
            .map { it.filter { it.state != Parcel.State.MISSING }.count() }
            .distinctUntilChanged()
            .behave(this)

    val totalWeight = this.parcels.map { it.sumByDouble { it.weight } }
            .distinctUntilChanged()
            .behave(this)

    val deliveredOrdersAmount = this.orders.map { it.filter { it.parcels.all { it.state == Parcel.State.DELIVERED } }.count() }
            .distinctUntilChanged()
            .behave(this)

    val deliveredParcelAmount = this.deliveredParcels.map { it.count() }
            .distinctUntilChanged()
            .behave(this)

    val deliveredParcelsWeight = this.deliveredParcels.map { it.sumByDouble { it.weight } }
            .distinctUntilChanged()
            .behave(this)
    //endregion

    /** Signature handwriting as svg */
    var signatureSvg: String? = null

    /** Signature camera image */
    private var signatureOnPaperImageUid: UUID? = null

    /** Postbox camera image */
    private var postboxImageUid: UUID? = null

    /** Recipient name */
    var recipientName: String? = null

    /** Delivery reason type */
    var deliveredReason: EventDeliveredReason = EventDeliveredReason.NORMAL

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
     * Reset all closing stop related state variables
     */
    fun resetCloseStopState() {
        this.postboxImageUid = null
        this.signatureOnPaperImageUid = null
        this.signatureSvg = null
        this.deliveredReason = EventDeliveredReason.NORMAL
    }

    /**
     * Resets all parcels to pending state and removes all event information
     */
    fun reset(): Completable {
        this.resetCloseStopState()

        val stop = this.entity

        return db.store.withTransaction {
            stop.state = Stop.State.PENDING
            update(stop)

            stop.tasks
                    .flatMap { it.order.parcels }
                    .forEach { parcel ->
                        parcel.state = Parcel.State.LOADED
                        parcel.reason = null
                        update(parcel)
                    }
        }
                .toCompletable()
                .subscribeOn(schedulers.database)
    }

    /**
     * (Re-)open stop
     */
    private fun open() {
        val stop = this.entity
        if (stop.state == Stop.State.CLOSED) {
            stop.state = Stop.State.PENDING
            db.store.toBlocking().update(stop)
        }
    }

    /**
     * Deliver a single parcel
     */
    fun deliverParcel(parcel: ParcelEntity): Completable {
        val stop = this.entity

        return db.store.withTransaction {
            // In case the stop has been closed before, re-open on delivery
            open()

            // Check if all stop parcel have the (same) event
            val stopParcels = parcels.blockingFirst()

            if (parcel.reason != null) {
                val parcelsToReset = when {
                // Reset event for all stop parcels if event matches
                    stopParcels.all { it.reason == parcel.reason } -> {
                        stopParcels
                    }
                // Reset event for all parcels of this order if event matches
                    parcel.order.parcels.all { it.reason == parcel.reason } -> {
                        parcel.order.parcels
                    }
                    else -> listOf()
                }

                parcelsToReset
                        .filter {
                            it.state == Parcel.State.LOADED
                        }
                        .forEach {
                            it.reason = null
                            update(it)
                        }
            }

            /** Mark parcel delivered */
            parcel.state = Parcel.State.DELIVERED

            update(parcel)
        }
                .toCompletable()
                .subscribeOn(schedulers.database)
    }

    /**
     * Assign event reason to entire stop
     */
    fun assignStopLevelEvent(reason: EventNotDeliveredReason): Completable {
        val stop = this.entity

        return db.store.withTransaction {
            // In case the stop has been closed before, re-open on delivery
            open()

            parcels.blockingFirst().forEach {
                it.state = Parcel.State.LOADED
                it.reason = reason
                update(it)
            }
        }
                .toCompletable()
                .subscribeOn(schedulers.database)
    }

    /**
     * Assign event reason to entire stop
     */
    fun assignOrderLevelEvent(order: Order, reason: EventNotDeliveredReason): Completable {
        val stop = this.entity

        return db.store.withTransaction {
            // In case the stop has been closed before, re-open on delivery
            open()

            order.parcels.forEach {
                it.state = Parcel.State.LOADED
                it.reason = reason
                update(it)
            }
        }
                .toCompletable()
                .subscribeOn(schedulers.database)
    }


    /**
     * Sends the image and stores the file uid internally, which will be passed
     * with close stop ParcelMessage on finalize
     */
    fun deliverWithSignatureOnPaper(signatureOnPaperImageJpeg: ByteArray) {
        // Send file
        this.signatureOnPaperImageUid =
                mqttEndpoints.central.main.channel().sendFile(signatureOnPaperImageJpeg, MimeType.JPEG.value)
    }

    /**
     * Sends the postbox delivery image and stores the file uid internally,
     * lateron passing it with the ParcelMessage when closing/finalizing the stop
     */
    fun deliverToPostbox(postboxImageJpeg: ByteArray) {
        this.deliveredReason = EventDeliveredReason.POSTBOX

        // Send file
        this.postboxImageUid =
                mqttEndpoints.central.main.channel().sendFile(postboxImageJpeg, MimeType.JPEG.value)
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
                    mqttEndpoints.central.main.channel().send(
                            ParcelServiceV1.ParcelMessage(
                                    userId = this.login.authenticatedUser?.id,
                                    nodeId = this.identity.uid.value,
                                    deliveredInfo = when {
                                        signatureSvg != null -> ParcelServiceV1.ParcelMessage.DeliveredInfo(
                                                signature = signatureSvg,
                                                recipient = recipientName
                                        )
                                        else -> null
                                    },
                                    signatureOnPaperInfo = when {
                                        signatureOnPaperImageUid != null -> ParcelServiceV1.ParcelMessage.SignatureOnPaperInfo(
                                                pictureFileUid = this.signatureOnPaperImageUid,
                                                recipient = recipientName
                                        )
                                        else -> null
                                    },
                                    postboxDeliveryInfo = when {
                                        this.deliveredReason == EventDeliveredReason.POSTBOX -> ParcelServiceV1.ParcelMessage.PostboxDeliveryInfo(
                                                pictureFileUid = this.postboxImageUid
                                        )
                                        else -> null
                                    },
                                    events = parcels.map {
                                        ParcelServiceV1.Event(
                                                event = when {
                                                    it.state == Parcel.State.DELIVERED -> Event.DELIVERED.value
                                                    it.state == Parcel.State.MISSING -> Event.NOT_IN_DELIVERY.value
                                                    else -> Event.DELIVERY_FAIL.value
                                                },
                                                reason = when {
                                                    it.state == Parcel.State.DELIVERED -> {
                                                        when {
                                                            this.deliveredReason == EventDeliveredReason.POSTBOX -> Reason.POSTBOX.id
                                                            this.deliveredReason == EventDeliveredReason.NEIGHBOR -> Reason.NEIGHBOUR.id
                                                            else -> it.reason?.reason?.id ?: Reason.NORMAL.id
                                                        }
                                                    }
                                                    else -> it.reason?.reason?.id ?: Reason.NORMAL.id
                                                },
                                                parcelId = it.id,
                                                latitude = lastLocation?.latitude,
                                                longitude = lastLocation?.longitude,
                                                damagedInfo = when {
                                                    it.isDamaged -> {
                                                        ParcelServiceV1.Event.DamagedInfo(
                                                                pictureFileUids = it.meta
                                                                        .filterValuesByType(Parcel.DamagedInfo::class.java)
                                                                        .mapNotNull {
                                                                            it.pictureFileUid
                                                                        }
                                                                        .toTypedArray()
                                                        )
                                                    }
                                                    else -> null
                                                }
                                        )
                                    }.toTypedArray()
                            )
                    )
                    //endregion
                })
                .subscribeOn(schedulers.database)
    }
}