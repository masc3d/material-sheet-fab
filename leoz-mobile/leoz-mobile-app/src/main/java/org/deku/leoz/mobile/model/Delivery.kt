package org.deku.leoz.mobile.model

import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.DrawableRes
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.model.*
import org.deku.leoz.service.internal.DeliveryListService
import org.deku.leoz.service.internal.OrderService
import org.deku.leoz.mobile.Database
import org.deku.leoz.mobile.R
import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty
import sx.time.toCalendar
import java.util.*

/**
 * Created by 27694066 on 09.05.2017.
 */
class Delivery {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val db: Database by Kodein.global.lazy.instance()
    private val deliveryListService: DeliveryListService by Kodein.global.lazy.instance()
    private val orderService: OrderService by Kodein.global.lazy.instance()

    val newOrderProperty = ObservableRxProperty<Order?>(null)
    var newOrder: Order? by newOrderProperty

    val activeStopProperty = ObservableRxProperty<Stop?>(null)
    val activeStop: Stop? by activeStopProperty

//    /**
//     * Receives the result of a service workflow and publishes it to the subscribers.
//     * eg. IMEI Check OK/Failed, CASH (didn't) collected, correct/wrong ID-Document
//     */
//    val serviceCheckEventSubject by lazy { PublishSubject.create<ServiceCheck>() }
//    val serviceCheckEvent by lazy { serviceCheckEventSubject.hide() }

    val stopList: MutableList<Stop> = mutableListOf()
    val orderList: MutableList<Order> = mutableListOf()

    val deliveryVehicle: Vehicle? = null

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

    /**
     * When initiating, check for existing orders stored in the local DB and (re)load them into the variables.
     */
    init {
        this.load()
    }

    //region MOCK DATA
    /**
     * Load stop data from database
     */
    fun load() {
        // TODO: move mock data to `MockDeliveryListService`, load data from db

        val deliveryAddresses = listOf(
                Address(
                        line1 = "Mathias Friedmannn",
                        line2 = "2. Addresszeile",
                        line3 = "3. Addresszeile",
                        street = "Dörrwiese.",
                        streetNo = "4",
                        zipCode = "36286",
                        city = "Neuenstein-Aua",
                        phone = "+49 6677 9582"
                ),
                Address(
                        line1 = "Philipp Prangenberg",
                        line2 = "2. Adresszeile",
                        line3 = "3. Addresszeile",
                        street = "An den Auewiesen",
                        streetNo = "25",
                        zipCode = "36286",
                        city = "Neuenstein-Obergeiß",
                        phone = "+49 6677 9582"
                ),
                Address(
                        line1 = "Jannik Trombach",
                        line2 = "2. Adresszeile",
                        line3 = "3. Addresszeile",
                        street = "Pappelweg",
                        streetNo = "2",
                        zipCode = "36286",
                        city = "Gittersdorf",
                        phone = "+49 6677 9585"
                ),
                Address(
                        line1 = "Lisa Himmel",
                        line2 = "2. Adresszeile",
                        line3 = "3. Addresszeile",
                        street = "Am Wendeberg",
                        streetNo = "5",
                        zipCode = "36251",
                        city = "Bad Hersfeld",
                        phone = "+49 6677 9595"
                )
        )

        val pickupAddresses = listOf(
                Address(
                        line1 = "Lisa Himmel",
                        line2 = "2. Adresszeile",
                        line3 = "3. Addresszeile",
                        street = "Am Wendeberg",
                        streetNo = "5",
                        zipCode = "36251",
                        city = "Bad Hersfeld",
                        phone = "+49 6677 9595"
                ),
                Address(
                        line1 = "Jannik Trombach",
                        line2 = "2. Adresszeile",
                        line3 = "3. Addresszeile",
                        street = "Pappelweg",
                        streetNo = "2",
                        zipCode = "36286",
                        city = "Gittersdorf",
                        phone = "+49 6677 9585"
                ),
                Address(
                        line1 = "Philipp Prangenberg",
                        line2 = "2. Adresszeile",
                        line3 = "3. Addresszeile",
                        street = "An den Auewiesen",
                        streetNo = "25",
                        zipCode = "36286",
                        city = "Neuenstein-Obergeiß",
                        phone = "+49 6677 9582"
                ),
                Address(
                        line1 = "Mathias Friedmannn",
                        line2 = "2. Addresszeile",
                        line3 = "3. Addresszeile",
                        street = "Dörrwiese.",
                        streetNo = "4",
                        zipCode = "36286",
                        city = "Neuenstein-Aua",
                        phone = "+49 6677 9582"
                )
        )

        val dateFrom = Date().toCalendar()
        dateFrom.set(Calendar.HOUR_OF_DAY, 8)
        dateFrom.set(Calendar.MINUTE, 0)

        val dateTo = Date().toCalendar()
        dateTo.set(Calendar.HOUR_OF_DAY, 12)
        dateTo.set(Calendar.MINUTE, 0)

        for (i in 0..3) {
            dateFrom.set(Calendar.HOUR_OF_DAY, 8 + i * 2)
            dateTo.set(Calendar.HOUR_OF_DAY, 12 + i * 2)

            val deliveryAppointment = Order.Appointment(
                    dateFrom = Date(dateFrom.timeInMillis),
                    dateTo = Date(dateTo.timeInMillis)
            )

            stopList.add(Stop(
                    orders = mutableListOf(
                            Order(
                                    id = "1",
                                    state = Order.State.LOADED,
                                    classification = OrderClassification.DELIVERY,
                                    parcel = listOf(
                                            Parcel(
                                                    id = "a",
                                                    labelRef = "1000000000$i"
                                            ),
                                            Parcel(
                                                    id = "b",
                                                    labelRef = "1000000001$i"
                                            ),
                                            Parcel(
                                                    id = "c",
                                                    labelRef = "1000000002$i"
                                            )
                                    ),
                                    deliveryAddress = deliveryAddresses[i],
                                    pickupAddress = pickupAddresses[i],
                                    deliveryAppointment = deliveryAppointment,
                                    carrier = Carrier.DER_KURIER,
                                    services = listOf(Order.Service(
                                            classification = Order.Service.Classification.DELIVERY_SERVICE,
                                            service = listOf(ParcelService.NO_ADDITIONAL_SERVICE))
                                    )
                            )
                    ),
                    address = deliveryAddresses[i],
                    appointment = deliveryAppointment,
                    state = Stop.State.PENDING
            ))

            stopList.add(Stop(
                    orders = mutableListOf(
                            Order(
                                    id = "2",
                                    state = Order.State.LOADED,
                                    classification = OrderClassification.DELIVERY,
                                    parcel = listOf(Parcel(
                                            id = "a",
                                            labelRef = "0200000000$i"
                                    )),
                                    deliveryAddress = deliveryAddresses[i],
                                    pickupAddress = pickupAddresses[i],
                                    deliveryAppointment = deliveryAppointment,
                                    carrier = Carrier.DER_KURIER,
                                    services = listOf(Order.Service(
                                            classification = Order.Service.Classification.DELIVERY_SERVICE,
                                            service = listOf(ParcelService.NO_ADDITIONAL_SERVICE))
                                    )
                            )
                    ),
                    address = deliveryAddresses[i],
                    appointment = deliveryAppointment,
                    state = Stop.State.PENDING
            ))
        }

        stopList.forEach {
            orderList.addAll(it.orders)
        }
    }
    //endregion

    data class Vehicle(
            val type: VehicleType,
            @DrawableRes val icon: Int
    )

    /**
     * Clean the existing Delivery.stopList
     * Merge existing orders in Delivery.orderList into the blank Delivery.stopList
     */
    fun initializeStopList() {
        stopList.clear()

        for (order in orderList) {
            integrateOrder(order)
        }
    }

    /**
     *
     */
    fun queryDeliveryList(listId: Long): List<Order> {
        try {
            val deliveryList: DeliveryListService.DeliveryList = deliveryListService.getById(id = listId)

            if (deliveryList.orders.isEmpty()) {
                return listOf()
            }

            val mobileOrder = deliveryList.orders.map { it.toOrder() }

            orderList.addAll(mobileOrder)

            return mobileOrder
        } catch (e: Exception) {
            log.error(e.message)
            return listOf()
        }
    }

    fun queryOrderServiceByLabel(ref: String): List<Order> {
        try {
            val order: List<OrderService.Order> = orderService.get(labelRef = ref)

            val orders = order.map { it.toOrder() }

            when (order.size) {
                0 -> {
                    return listOf()
                }
                1 -> {
                    orderList.addAll(orders)
                    return orders
                }
                else -> {
                    return orders
                }
            }
        } catch (e: Exception) {
            log.error(e.message)
            return listOf()
        }
    }


    /**
     * Should be called from the receiver of the order.
     * @param order Stop.Order
     */
    fun addOrder(order: Order) {
        newOrder = order
    }

    /**
     * @param order: The order which should be integrated into the stopList.
     * @return The Stop where the order has been integrated.
     * This method tries to find a stop in the existing stopList where the given order can be integrated.
     * If no suitable Stop has been found, a new stop with this order will be created.
     */
    fun integrateOrder(order: Order): Stop {
        val existingStopIndex = order.findSuitableStopIndex(stopList)

        if (!orderList.contains(order)) {
            orderList.add(order)
        }

        if (existingStopIndex > -1) {
            val stop = stopList[existingStopIndex]
            stop.orders.add(order)
            TODO("To be fixed before use")
            /* TODO
            stopList[existingStopIndex].appointment = minOf(order.appointment[order.classification]!!, stopList[existingStopIndex].appointment, Comparator<Order.Appointment> { o1, o2 ->
                when {
                    o1.dateStart!!.before(o2.dateStart) -> 1
                    else -> 2
                }
            }) */
            return stop
        } else {
            stopList.add(
                    Stop(
                            orders = mutableListOf(order),
                            address = order.getAddressOfInterest(),
                            appointment = when (order.classification) {
                                OrderClassification.PICKUP -> order.pickupAppointment
                                OrderClassification.DELIVERY -> order.deliveryAppointment
                                else -> throw IllegalStateException()
                            } ?: throw IllegalStateException()
                    )
            )
            return stopList.last()
        }
    }

    fun findStopByLabelReference(labelReference: String): List<Stop> {
        return stopList.filter {
            it.orders.filter {
                it.parcel.filter {
                    it.labelRef == labelReference
                }.isNotEmpty()
            }.isNotEmpty()
        }
    }

    fun findOrderByLabelReference(labelReference: String): List<Order> {
        return orderList.filter {
            it.parcel.filter {
                it.labelRef == labelReference
            }.isNotEmpty()
        }
    }

    fun countParcelsToBeVehicleLoaded(): Int {
        var count = 0
        count += orderList.filter { it.state == Order.State.PENDING }
                .flatMap { it.parcel }
                .filter { it.state == Parcel.State.PENDING }.size
        return count
    }

    fun countParcelsToBeVehicleUnLoaded(): Int {
        var count = 0
        count += orderList.filter { it.state == Order.State.FAILED || it.state == Order.State.LOADED }
                .flatMap { it.parcel }
                .filter { it.state == Parcel.State.FAILED || it.state == Parcel.State.LOADED }.size
        return count
    }
}

fun Stop.deliver(reason: EventDeliveredReason, recipient: String, signature: Bitmap? = null) {
    this.orders.forEach {
        it.state = Order.State.DONE
        it.parcel.forEach {
            it.state = Parcel.State.DONE
        }
    }
}

fun Context.getServiceText(service: ParcelService): String {
    return when(service) {
        ParcelService.SUITCASE_SHIPPING -> getString(R.string.service_suitcase)
        ParcelService.RECEIPT_ACKNOWLEDGEMENT -> getString(R.string.service_receipt_acknowledgement)
        ParcelService.SELF_PICKUP -> getString(R.string.service_selfpickup)
        ParcelService.CASH_ON_DELIVERY -> getString(R.string.service_cash_delivery)
        ParcelService.PHARMACEUTICALS -> getString(R.string.service_pharmaceuticals)
        ParcelService.IDENT_CONTRACT_SERVICE -> getString(R.string.service_ident_contract)
        ParcelService.SUBMISSION_PARTICIPATION -> getString(R.string.service_submission)
        ParcelService.SECURITY_RETURN -> getString(R.string.service_security_return)
        ParcelService.XCHANGE -> getString(R.string.service_xchange)
        ParcelService.PHONE_RECEIPT -> getString(R.string.service_phone_receipt)
        ParcelService.DOCUMENTED_PERSONAL_DELIVERY -> getString(R.string.service_documented_personal)
        ParcelService.DEPARTMENT_DELIVERY -> getString(R.string.service_department_delivery)
        ParcelService.FIXED_APPOINTMENT -> getString(R.string.service_fixed)
        ParcelService.FAIR_SERVICE -> getString(R.string.service_fair)
        ParcelService.SELF_COMPLETION_OF_DUTY_PAYMENT_AND_DOCUMENTS -> getString(R.string.service_self_completion_duty)
        ParcelService.PACKAGING_RECIRCULATION -> getString(R.string.service_packaging_recirculation)
        ParcelService.POSTBOX_DELIVERY -> getString(R.string.service_postbox_delivery)
        ParcelService.NO_ALTERNATIVE_DELIVERY -> getString(R.string.service_no_alternative)
        else -> return ""
    }
}