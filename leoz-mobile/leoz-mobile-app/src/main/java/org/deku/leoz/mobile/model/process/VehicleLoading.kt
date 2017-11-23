package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.entity.ParcelEntity
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.entity.filterValuesByType
import org.deku.leoz.mobile.model.repository.OrderRepository
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
class VehicleLoading : CompositeDisposableSupplier {
    override val compositeDisposable by lazy { CompositeDisposable() }

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val db: Database by Kodein.global.lazy.instance()

    private val deliveryList: DeliveryList by Kodein.global.lazy.instance()
    private val locationCache: LocationCache by Kodein.global.lazy.instance()

    //region Repositories
    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()
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
    val orderTotalAmount = deliveryList.parcels.map { it.value.map { it.order }.distinct().count() }
            .distinctUntilChanged()
            .behave(this)

    val stopTotalAmount = deliveryList.parcels.map { it.value.flatMap { it.order.tasks }.mapNotNull { it.stop }.distinct().count() }
            .distinctUntilChanged()
            .behave(this)

    val parcelTotalAmount = deliveryList.parcels.map { it.value.count() }
            .distinctUntilChanged()
            .behave(this)

    val totalWeight = deliveryList.parcels.map { it.value.sumByDouble { it.weight } }
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
     * Load parcel(s)
     * @param parcels Parcel(s) to load
     */
    fun load(parcels: List<ParcelEntity>): Completable {
        return Completable.fromAction {
            val lastLocation = this@VehicleLoading.locationCache.lastLocation

            db.store.withTransaction {
                parcels.forEach { parcel ->
                    parcel.state = Parcel.State.LOADED

                    parcelRepository
                            .update(parcel)
                            .blockingGet()

                    stopRepository.updateStopStateFromParcels(
                        stop = parcel.order.tasks.map { it.stop }.filterNotNull().first()
                    )
                            .blockingGet()

                    // Send compound parcel message with loading states
                    mqttChannels.central.main.channel().send(
                            ParcelServiceV1.ParcelMessage(
                                    userId = this@VehicleLoading.login.authenticatedUser?.id,
                                    nodeId = this@VehicleLoading.identity.uid.value,
                                    events = parcels.map {
                                        ParcelServiceV1.Event(
                                                event = Event.IN_DELIVERY.value,
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
                }
            }
                    .blockingGet()
        }
                .subscribeOn(db.scheduler)

    }

    fun load(parcel: ParcelEntity): Completable = this.load(listOf(parcel))
}