package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.entity.ParcelEntity
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.entity.filterValuesByType
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.service.LocationCache
import org.deku.leoz.model.Event
import org.deku.leoz.model.Reason
import org.deku.leoz.service.internal.ParcelServiceV1
import org.slf4j.LoggerFactory
import sx.mq.mqtt.channel
import sx.rx.CompositeDisposableSupplier
import sx.rx.behave

/**
 * Delivery list model
 * Created by masc on 18.06.17.
 */
class VehicleUnloading : CompositeDisposableSupplier {
    override val compositeDisposable by lazy { CompositeDisposable() }

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val db: Database by Kodein.global.lazy.instance()
    private val schedulers: org.deku.leoz.mobile.rx.Schedulers by Kodein.global.lazy.instance()

    // Repositories
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()

    private val deliveryList: DeliveryList by Kodein.global.lazy.instance()
    private val identity: Identity by Kodein.global.lazy.instance()
    private val login: Login by Kodein.global.lazy.instance()

    private val locationCache: LocationCache by Kodein.global.lazy.instance()
    private val mqttChannels: MqttEndpoints by Kodein.global.lazy.instance()

    val parcels = Observable.combineLatest(
            deliveryList.loadedParcels.map { it.value },
            deliveryList.pendingParcels.map { it.value },
            BiFunction { t1: List<ParcelEntity>, t2: List<ParcelEntity> ->
                t1.plus(t2)
            }
    )
            .behave(this)

    //region Counters
    val parcelTotalAmount = this.parcels.map { it.count() }
            .distinctUntilChanged()
            .behave(this)

    val totalWeight = this.parcels.map { it.sumByDouble { it.weight } }
            .distinctUntilChanged()
            .behave(this)

    val parcelAmount = deliveryList.pendingParcels.map { it.value.count() }
            .distinctUntilChanged()
            .behave(this)

    val weight = deliveryList.pendingParcels.map { it.value.sumByDouble { it.weight } }
            .distinctUntilChanged()
            .behave(this)
    //endregion

    /**
     * Unload parcel
     * @param parcel Parcel to unload
     */
    fun unload(parcel: ParcelEntity): Completable {

        return if (parcel.state == Parcel.State.LOADED) {

            // When parcle is unloaded, it's state is set back to PENDING
            parcel.state = Parcel.State.PENDING

            val lastLocation = this.locationCache.lastLocation

            this.parcelRepository.update(parcel)
                    .toCompletable()
                    .concatWith(Completable.fromCallable {
                        mqttChannels.central.main.channel().send(
                                ParcelServiceV1.ParcelMessage(
                                        userId = this.login.authenticatedUser?.id,
                                        nodeId = this.identity.uid.value,
                                        events = listOf(parcel).map {
                                            ParcelServiceV1.Event(
                                                    event = Event.TOUR_UNLOADED.value,
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
        } else {
            Completable.complete()
        }
                .subscribeOn(schedulers.database)
    }

    /**
     * Finalizes the unloading process, marking all parcels which have not been unloaded as missing
     */
    fun finalize(): Completable {
        return db.store.withTransaction {
            // Set all pending parcels to MISSING
            val loadedParcels = parcelRepository.entities.filter { it.state == Parcel.State.LOADED }
            val stopsWithPendingParcels = parcelRepository.entities
                    .filter { it.state == Parcel.State.PENDING }
                    .flatMap { it.order.tasks.mapNotNull { it.stop } }
                    .filter { it.state != Stop.State.CLOSED }
                    .distinct()

            loadedParcels.forEach {
                it.state = Parcel.State.MISSING
                update(it)
            }
            stopsWithPendingParcels.forEach {
                it.state = Stop.State.NONE
                update(it)
            }
        }
                .subscribeOn(schedulers.database)
                .toCompletable()
    }
}