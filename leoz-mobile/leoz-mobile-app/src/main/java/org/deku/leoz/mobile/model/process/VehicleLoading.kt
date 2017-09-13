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
class VehicleLoading : CompositeDisposableSupplier {
    override val compositeDisposable by lazy { CompositeDisposable() }

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val db: Database by Kodein.global.lazy.instance()
    private val schedulers: org.deku.leoz.mobile.rx.Schedulers by Kodein.global.lazy.instance()

    private val deliveryList: DeliveryList by Kodein.global.lazy.instance()
    private val locationCache: LocationCache by Kodein.global.lazy.instance()

    //region Repositories
    private val orderRepository: OrderRepository by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()
    //endregion

    private val identity: Identity by Kodein.global.lazy.instance()
    private val login: Login by Kodein.global.lazy.instance()

    private val mqttChannels: MqttEndpoints by Kodein.global.lazy.instance()

    /**
     * Stops with loaded parcels
     */
    val stops = deliveryList.loadedParcels.map { it.value.flatMap { it.order.tasks }.mapNotNull { it.stop }.distinct() }
            .behave(this)

    //region Counters
    val orderTotalAmount = orderRepository.entitiesProperty.map { it.value.count() }
            .distinctUntilChanged()
            .behave(this)

    val stopTotalAmount = stopRepository.entitiesProperty.map { it.value.count() }
            .distinctUntilChanged()
            .behave(this)

    val parcelTotalAmount = parcelRepository.entitiesProperty.map { it.value.count() }
            .distinctUntilChanged()
            .behave(this)

    val totalWeight = parcelRepository.entitiesProperty.map { it.value.sumByDouble { it.weight } }
            .distinctUntilChanged()
            .behave(this)

    val orderAmount = deliveryList.loadedParcels.map { it.value.map { it.order }.distinct().count() }
            .distinctUntilChanged()
            .behave(this)

    val parcelAmount = deliveryList.loadedParcels.map { it.value.count() }
            .distinctUntilChanged()
            .behave(this)

    val stopAmount = this.stops.map { it.count() }
            .distinctUntilChanged()
            .behave(this)

    val weight = deliveryList.loadedParcels.map { it.value.sumByDouble { it.weight } }
            .distinctUntilChanged()
            .behave(this)
    //endregion

    /**
     * Finalizes the loading process, marking all parcels with pending loading state as missing
     */
    fun finalize(): Completable {
        return db.store.withTransaction {
            // Set all pending parcels to MISSING
            val pendingParcels = parcelRepository.entities.filter { it.state == Parcel.State.PENDING }

            pendingParcels.forEach {
                it.state = Parcel.State.MISSING
                update(it)
            }

            // Set all stops which contain LOADED parcels to PENDING
            val stopsWithLoadedParcels = parcelRepository.entities
                    .filter { it.state == Parcel.State.LOADED }
                    .flatMap { it.order.tasks.mapNotNull { it.stop } }
                    .filter { it.state != Stop.State.CLOSED }
                    .distinct()

            stopsWithLoadedParcels.forEach {
                it.state = Stop.State.PENDING
                update(it)
            }

            // Reset state for remaining stops
            stopRepository.entities
                    .subtract(stopsWithLoadedParcels)
                    .filter { it.state != Stop.State.CLOSED }
                    .forEach {
                        it.state = Stop.State.NONE
                        update(it)
                    }
        }
                .toCompletable()
                .concatWith(Completable.fromCallable {
                    // Select parcels for which to send status events
                    val parcels = parcelRepository.entities.filter {
                        it.state == Parcel.State.LOADED ||
                                it.state == Parcel.State.MISSING
                    }

                    val lastLocation = this@VehicleLoading.locationCache.lastLocation

                    // Send compound parcel message with loading states
                    mqttChannels.central.main.channel().send(
                            ParcelServiceV1.ParcelMessage(
                                    userId = this.login.authenticatedUser?.id,
                                    nodeId = this.identity.uid.value,
                                    events = parcels.map {
                                        ParcelServiceV1.Event(
                                                event = when {
                                                    it.state == Parcel.State.LOADED -> Event.IN_DELIVERY.value
                                                    it.state == Parcel.State.MISSING -> Event.NOT_IN_DELIVERY.value
                                                    else -> Event.DELIVERY_FAIL.value
                                                },
                                                reason = Reason.NORMAL.id,
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
                })
                .subscribeOn(schedulers.database)
    }
}