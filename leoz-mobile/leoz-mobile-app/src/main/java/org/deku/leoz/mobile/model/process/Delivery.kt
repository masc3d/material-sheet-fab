package org.deku.leoz.mobile.model.process

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.model.*
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.model.entity.*
import org.deku.leoz.mobile.model.entity.Parcel
import org.deku.leoz.mobile.model.repository.ParcelRepository
import org.deku.leoz.mobile.ui.ScreenFragment
import org.deku.leoz.mobile.ui.screen.NeighbourDeliveryScreen
import org.deku.leoz.mobile.ui.screen.PostboxDeliveryScreen
import org.deku.leoz.mobile.ui.screen.SignatureScreen
import org.slf4j.LoggerFactory
import sx.requery.ObservableQuery
import sx.rx.CompositeDisposableSupplier
import sx.rx.ObservableRxProperty
import sx.rx.behave
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

    //region Self-observable queries
    private val pendingStopsQuery = ObservableQuery<StopEntity>(
            name = "Pending stops",
            query = db.store.select(StopEntity::class)
                    .where(StopEntity.STATE.eq(Stop.State.PENDING))
                    .orderBy(StopEntity.POSITION.asc())
                    .get()
    )
            .bind(this)
    //endregion

    val pendingStops = this.pendingStopsQuery.result

    val closedStops = this.deliveryList.stops.map { it.filter { it.state == Stop.State.CLOSED } }
            .behave(this)

    val undeliveredParcels = parcelRepository.entitiesProperty.map { it.value.filter { it.deliveryState == Parcel.DeliveryState.UNDELIVERED } }
            .behave(this)

    var activeStop: DeliveryStop? by Delegates.observable<DeliveryStop?>(null, { p, o, v ->
        o?.dispose()
    })

    val nextDeliveryScreenProperty = ObservableRxProperty<ScreenFragment<*>?>(null)
    val nextDeliveryScreenSubject = PublishSubject.create<ScreenFragment<*>>()
    var nextDeliveryScreen: ScreenFragment<*>? by nextDeliveryScreenProperty

//    val serviceCheckList: List<ServiceCheck> = listOf(
//            ServiceCheck(type = ServiceCheck.CheckType.CASH),
//            ServiceCheck(type = ServiceCheck.CheckType.IMEI_PIN)
//    )

//    val serviceCheckList: List<Delivery.ServiceCheck> by lazy {
//        ServiceCheck.CheckType.values().map {
//            Delivery.ServiceCheck(type = it)
//        }
//
//        data class ServiceCheck(val type: CheckType, var done: Boolean = false, var success: Boolean = false) {
//            enum class CheckType {
//                IMEI_PIN, CASH, IDENT, XC, SXC
//            }
//        }

    fun sign(stopId: Int, reason: EventDeliveredReason, recipient: String = "") {
        when (reason) {
            EventDeliveredReason.NORMAL -> {
                nextDeliveryScreenSubject.onNext(
                        SignatureScreen().also {
                            it.parameters = SignatureScreen.Parameters(
                                    stopId = stopId,
                                    deliveryReason = reason,
                                    recipient = recipient
                            )
                        })
            }

            EventDeliveredReason.NEIGHBOR -> {
                nextDeliveryScreenSubject.onNext(NeighbourDeliveryScreen.create(stopId = stopId))
            }

            EventDeliveredReason.POSTBOX -> {
                nextDeliveryScreenSubject.onNext(PostboxDeliveryScreen.create(stopId))
            }

            else -> throw NotImplementedError("Reason [${reason.name}]  not implemented.")
        }
    }
//
//    data class ServiceCheck(val service: ParcelService, var done: Boolean = false, var success: Boolean = false)
//
//    fun getNextServiceCheck(): ServiceCheck? {
//        return if (serviceCheckList.isNotEmpty()) serviceCheckList.firstOrNull { !it.done } else null
//    }
//
//    val serviceCheckList: List<ServiceCheck> by lazy {
//        val list: MutableList<ServiceCheck> = mutableListOf()
//        val parcelServiceList: List<ParcelService> = this.getServiceOfInterest().service
//
//        parcelServiceList.forEach {
//            when (it) {
//                ParcelService.CASH_ON_DELIVERY,
//                ParcelService.RECEIPT_ACKNOWLEDGEMENT,
//                ParcelService.PHARMACEUTICALS,
//                ParcelService.IDENT_CONTRACT_SERVICE,
//                ParcelService.SUBMISSION_PARTICIPATION,
//                ParcelService.SECURITY_RETURN,
//                ParcelService.XCHANGE,
//                ParcelService.PHONE_RECEIPT,
//                ParcelService.DOCUMENTED_PERSONAL_DELIVERY,
//                ParcelService.SELF_COMPLETION_OF_DUTY_PAYMENT_AND_DOCUMENTS,
//                ParcelService.PACKAGING_RECIRCULATION -> list.add(org.deku.leoz.mobile.model.Order.ServiceCheck(service = it))
//                else -> {
//
//                }
//            }
//        }
//
//        list.toList()
//    }
}