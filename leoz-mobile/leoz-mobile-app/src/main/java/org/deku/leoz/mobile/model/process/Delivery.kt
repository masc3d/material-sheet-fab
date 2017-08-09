package org.deku.leoz.mobile.model.process

import android.support.annotation.DrawableRes
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

    val activeStopProperty = ObservableRxProperty<Stop?>(null)
    val activeStop: Stop? by activeStopProperty

    val nextDeliveryScreenProperty = ObservableRxProperty<ScreenFragment<*>?>(null)
    val nextDeliveryScreenSubject = PublishSubject.create<ScreenFragment<*>>()
    var nextDeliveryScreen: ScreenFragment<*>? by nextDeliveryScreenProperty

//    /**
//     * Receives the result of a service workflow and publishes it to the subscribers.
//     * eg. IMEI Check OK/Failed, CASH (didn't) collected, correct/wrong ID-Document
//     */
//    val serviceCheckEventSubject by lazy { PublishSubject.create<ServiceCheck>() }
//    val serviceCheckEvent by lazy { serviceCheckEventSubject.hide() }

    val orderList: MutableList<Order> = mutableListOf()

    val allowedEvents: List<EventNotDeliveredReason> by lazy {
        listOf(
                EventNotDeliveredReason.Absent,
                EventNotDeliveredReason.Refuse,
                EventNotDeliveredReason.Vacation,
                EventNotDeliveredReason.AddressWrong,
                EventNotDeliveredReason.Moved,
                EventNotDeliveredReason.Damaged,
                EventNotDeliveredReason.XC_ObjectDamaged,
                EventNotDeliveredReason.XC_ObjectNotReady,
                EventNotDeliveredReason.XC_ObjectWrong,
                EventNotDeliveredReason.CouldWantNotPay,
                EventNotDeliveredReason.IdentDocNotPresent
        )
    }

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
            EventDeliveredReason.Normal -> {
                nextDeliveryScreenSubject.onNext(
                        SignatureScreen().also {
                            it.parameters = SignatureScreen.Parameters(
                                    stopId = stopId,
                                    deliveryReason = reason,
                                    recipient = recipient
                            )
                        })
            }

            EventDeliveredReason.Neighbor -> {
                nextDeliveryScreenSubject.onNext(NeighbourDeliveryScreen.create(stopId = stopId))
            }

            EventDeliveredReason.Postbox -> {
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


    data class Vehicle(
            val type: VehicleType,
            @DrawableRes val icon: Int
    )

    /**
     * Clean the existing Delivery.stopList
     * Merge existing orders in Delivery.orderList into the blank Delivery.stopList
     */
    //    fun initializeStopList() {
//        stopList.clear()
//
//        for (order in orderList) {
//            integrateOrder(order)
//        }
//    }

    /**
     *
     */
    //    fun queryDeliveryList(listId: Long): List<Order> {
//        try {
//            val deliveryList: DeliveryListService.DeliveryList = deliveryListService.getById(id = listId)
//
//            if (deliveryList.orders.isEmpty()) {
//                return listOf()
//            }
//
//            val mobileOrder = deliveryList.orders.map { it.toOrder() }
//
//            orderList.addAll(mobileOrder)
//
//            return mobileOrder
//        } catch (e: Exception) {
//            log.error(e.message)
//            return listOf()
//        }
//    }

//    fun queryOrderServiceByLabel(ref: String): List<Order> {
//        try {
//            val order: List<OrderService.Order> = orderService.get(labelRef = ref)
//
//            val orders = order.map { it.toOrder() }
//
//            when (order.size) {
//                0 -> {
//                    return listOf()
//                }
//                1 -> {
//                    orderList.addAll(orders)
//                    return orders
//                }
//                else -> {
//                    return orders
//                }
//            }
//        } catch (e: Exception) {
//            log.error(e.message)
//            return listOf()
//        }
//    }

//    /**
//     * @param order: The order which should be integrated into the stopList.
//     * @return The Stop where the order has been integrated.
//     * This method tries to find a stop in the existing stopList where the given order can be integrated.
//     * If no suitable Stop has been found, a new stop with this order will be created.
//     */
//    fun integrateOrder(order: Order): Stop {
//        val existingStopIndex = order.findSuitableStopIndex(stopList)
//
//        if (!orderList.contains(order)) {
//            orderList.add(order)
//        }
//
//        if (existingStopIndex > -1) {
//            val stop = stopList[existingStopIndex]
//            stop.orders.add(order)
//            TODO("To be fixed before use")
//            /* TODO
//            stopList[existingStopIndex].appointment = minOf(order.appointment[order.classification]!!, stopList[existingStopIndex].appointment, Comparator<Order.Appointment> { o1, o2 ->
//                when {
//                    o1.dateStart!!.before(o2.dateStart) -> 1
//                    else -> 2
//                }
//            }) */
//            return stop
//        } else {
//            stopList.add(
//                    Stop(
//                            orders = mutableListOf(order),
//                            address = order.getAddressOfInterest(),
//                            appointment = when (order.classification) {
//                                OrderClassification.PICKUP -> order.pickupAppointment
//                                OrderClassification.DELIVERY -> order.deliveryAppointment
//                                else -> throw IllegalStateException()
//                            } ?: throw IllegalStateException()
//                    )
//            )
//            return stopList.last()
//        }
//    }
}

//fun Stop.deliver(reason: EventDeliveredReason, recipient: String, signature: Bitmap? = null) {
//    this.orders.forEach {
//        it.state = Order.State.DONE
//        it.parcels.forEach {
//            it.state = Parcel.State.DONE
//        }
//    }
//}

