package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.OrderTask
import org.deku.leoz.mobile.model.entity.Stop
import org.deku.leoz.mobile.model.entity.StopEntity
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.model.repository.StopRepository
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.service.internal.DeliveryListService
import org.slf4j.LoggerFactory
import sx.mq.mqtt.channel
import sx.requery.ObservableQuery
import sx.rx.CompositeDisposableSupplier
import sx.rx.bind
import kotlin.properties.Delegates

/**
 * Delivery process model
 * Created by 27694066 on 09.05.2017.
 */
class Delivery : CompositeDisposableSupplier {
    override val compositeDisposable by lazy { CompositeDisposable() }

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val db: Database by Kodein.global.lazy.instance()

    private val deliveryList: DeliveryList by Kodein.global.lazy.instance()

    private val parcelRepository: ParcelRepository by Kodein.global.lazy.instance()
    private val stopRepository: StopRepository by Kodein.global.lazy.instance()

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
    )
            .bind(this)

    private val closedStopsQuery = ObservableQuery<StopEntity>(
            name = "Closed stops",
            query = db.store.select(StopEntity::class)
                    .where(StopEntity.STATE.eq(Stop.State.CLOSED))
                    .orderBy(StopEntity.POSITION.asc())
                    .get()
    )
            .bind(this)

    //endregion

    val pendingStops = this.pendingStopsQuery.result

    val closedStops = this.closedStopsQuery.result

    /**
     * The currently active stop.
     * Setting a stop active will also set its state to PENDING if it has no state
     */
    var activeStop: DeliveryStop? by Delegates.observable<DeliveryStop?>(null, { _, o, v ->
        o?.dispose()

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

    /**
     * Send the current delivery/tour stop order
     */
    fun sendStopOrderUpdate(): Completable {
        return Completable.fromAction {
            this.mqttEndpoints.central.main.channel().send(
                    DeliveryListService.StopOrderUpdateMessage(
                            nodeUid = identity.uid.value,
                            userId = login.authenticatedUser?.id ?: 0,
                            stops = this.pendingStops.blockingFirst().value.map { stop ->
                                DeliveryListService.Stop(
                                    tasks = stop.tasks.map {
                                        DeliveryListService.Task(
                                                orderId = it.order.id,
                                                stopType = when (it.type) {
                                                    OrderTask.TaskType.DELIVERY -> DeliveryListService.Task.Type.DELIVERY
                                                    OrderTask.TaskType.PICKUP -> DeliveryListService.Task.Type.PICKUP
                                                }
                                        )
                                    }
                                )
                            }
                    )
            )
        }
                .subscribeOn(db.scheduler)
    }

}