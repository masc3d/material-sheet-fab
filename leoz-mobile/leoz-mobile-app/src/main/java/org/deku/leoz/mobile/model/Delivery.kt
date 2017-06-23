package org.deku.leoz.mobile.model

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.deku.leoz.model.Carrier
import org.deku.leoz.model.OrderClassification
import org.deku.leoz.model.ParcelService
import org.deku.leoz.service.internal.DeliveryListService
import org.deku.leoz.service.internal.OrderService
import org.slf4j.LoggerFactory
import sx.rx.ObservableRxProperty
import sx.time.toCalendar
import java.util.*

/**
 * Created by 27694066 on 09.05.2017.
 */
class Delivery {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val deliveryListService: DeliveryListService by Kodein.global.lazy.instance()
    private val orderService: OrderService by Kodein.global.lazy.instance()

    val newOrderProperty = ObservableRxProperty<Order?>(null)
    var newOrder: Order? by newOrderProperty

    val activeStopProperty = ObservableRxProperty<Stop?>(null)
    val activeStop: Stop? by activeStopProperty

    /**
     * Receives the acquired information during the service workflow process, to be published to its subscribers.
     * eg. entered information of ID document
     */
    val informationSubject: Subject<ServiceInformation> = PublishSubject.create()

    /**
     * Receives the result of a service workflow and publishes it to the subscribers.
     * eg. IMEI Check OK/Failed, CASH (didn't) collected, correct/wrong ID-Document
     */
    val serviceCheckEventSubject by lazy { PublishSubject.create<ServiceCheck>() }
    val serviceCheckEvent by lazy { serviceCheckEventSubject.hide() }

    val stopList: MutableList<Stop> = mutableListOf()
    val orderList: MutableList<Order> = mutableListOf()

    /**
     * When initiating, check for existing orders stored in the local DB and (re)load them into the variables.
     */
    init {
        this.load()
    }

    /**
     * Load stop data from database
     */
    fun load() {
        // TODO: move mock data to `MockDeliveryListService`, load data from db

        val addr = Order.Address(
                classification = Order.Address.Classification.DELIVERY,
                addressLine1 = "Prangenberg",
                addressLine2 = "DEK KURIER",
                addressLine3 = "3. Addresszeile",
                street = "Dörrwiese",
                streetNo = "2",
                zipCode = "36286",
                city = "Neuenstein",
                phone = "+49 6677 9582"
        )
        val addr2 = Order.Address(
                classification = Order.Address.Classification.PICKUP,
                addressLine1 = "Prangenberg",
                addressLine2 = "2. Adresszeile",
                addressLine3 = "3. Addresszeile",
                street = "Burscheidter Weg",
                streetNo = "4",
                zipCode = "40822",
                city = "Mettmann",
                phone = "+49 6677 9582"
        )

        val delAddresses = listOf(
                Order.Address(
                        classification = Order.Address.Classification.DELIVERY,
                        addressLine1 = "Max Mustermann",
                        addressLine2 = "DEK KURIER",
                        addressLine3 = "3. Addresszeile",
                        street = "Dörrwiese",
                        streetNo = "2",
                        zipCode = "36286",
                        city = "Neuenstein",
                        phone = "+49 6677 9582"
                ),
                Order.Address(
                        classification = Order.Address.Classification.DELIVERY,
                        addressLine1 = "Prangenberg",
                        addressLine2 = "2. Adresszeile",
                        addressLine3 = "3. Addresszeile",
                        street = "Burscheidter Weg",
                        streetNo = "4",
                        zipCode = "40822",
                        city = "Mettmann",
                        phone = "+49 6677 9582"
                )
        )

        val dateFrom = Date().toCalendar()
        dateFrom.set(Calendar.HOUR_OF_DAY, 8)
        dateFrom.set(Calendar.MINUTE, 0)

        val dateTo = Date().toCalendar()
        dateTo.set(Calendar.HOUR_OF_DAY, 12)
        dateTo.set(Calendar.MINUTE, 0)

        val appointment = Order.Appointment(
                classification = Order.Appointment.Classification.DELIVERY,
                dateFrom = Date(dateFrom.timeInMillis),
                dateTo = Date(dateTo.timeInMillis)
        )

        for (i in 10..60) {
            stopList.add(Stop(
                    orders = mutableListOf(
                            Order(
                                    id = "1",
                                    state = Order.State.PENDING,
                                    classification = OrderClassification.DELIVERY,
                                    parcel = listOf(
                                            Order.Parcel(
                                                    id = "a",
                                                    labelReference = "100000000$i"
                                            ),
                                            Order.Parcel(
                                                    id = "b",
                                                    labelReference = "100000001$i"
                                            ),
                                            Order.Parcel(
                                                    id = "c",
                                                    labelReference = "100000002$i"
                                            )
                                    ),
                                    addresses = mutableListOf(addr, addr2),
                                    appointment = listOf(appointment),
                                    carrier = Carrier.DER_KURIER,
                                    services = listOf(Order.Service(
                                            classification = Order.Service.Classification.DELIVERY_SERVICE,
                                            service = listOf(ParcelService.NO_ADDITIONAL_SERVICE))
                                    ),
                                    sort = 0
                            )
                    ),
                    address = addr,
                    appointment = appointment,
                    state = Stop.State.PENDING
            ))

            stopList.add(Stop(
                    orders = mutableListOf(
                            Order(
                                    id = "2",
                                    state = Order.State.PENDING,
                                    classification = OrderClassification.DELIVERY,
                                    parcel = listOf(Order.Parcel(
                                            id = "a",
                                            labelReference = "020000000$i"
                                    )),
                                    addresses = mutableListOf(addr, addr2),
                                    appointment = listOf(appointment),
                                    carrier = Carrier.DER_KURIER,
                                    services = listOf(Order.Service(
                                            classification = Order.Service.Classification.DELIVERY_SERVICE,
                                            service = listOf(ParcelService.NO_ADDITIONAL_SERVICE))
                                    ),
                                    sort = 0
                            )
                    ),
                    address = addr,
                    appointment = appointment,
                    state = Stop.State.PENDING
            ))
        }

        stopList.forEach {
            orderList.addAll(it.orders)
        }
    }

    data class ServiceCheck(val type: CheckType, val success: Boolean) {
        enum class CheckType {
            IMEI_CHECKED, CASH_COLLECTED
        }
    }

    data class ServiceInformation(val type: Order.Information.Classification, val value: Order.Information.AdditionalInformation)

    fun addInformation(serviceInformation: ServiceInformation) {
        informationSubject.onNext(serviceInformation)
    }

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
    fun queryDeliveryList(listId: String): List<Order> {
        try{
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
                            appointment = order.appointment.first {
                                (it.classification == Order.Appointment.Classification.DELIVERY && order.classification == OrderClassification.DELIVERY) || (it.classification == Order.Appointment.Classification.PICKUP && order.classification == OrderClassification.PICKUP)
                            }
                    )
            )
            return stopList.last()
        }
    }

    fun findStopByLabelReference(labelReference: String): List<Stop> {
        return stopList.filter {
            it.orders.filter {
                it.parcel.filter {
                    it.labelReference == labelReference
                }.isNotEmpty()
            }.isNotEmpty()
        }
    }

    fun findOrderByLabelReference(labelReference: String): List<Order> {
        return orderList.filter {
            it.parcel.filter {
                it.labelReference == labelReference
            }.isNotEmpty()
        }
    }

    fun countParcelsToBeVehicleLoaded(): Int {
        var count = 0
        count += orderList.filter { it.state == Order.State.PENDING }
                .flatMap { it.parcel }
                .filter { it.state == Order.Parcel.State.PENDING }.size
        return count
    }

    fun countParcelsToBeVehicleUnLoaded(): Int {
        var count = 0
        count += orderList.filter { it.state == Order.State.FAILED || it.state == Order.State.LOADED }
                .flatMap { it.parcel }
                .filter { it.state == Order.Parcel.State.FAILED }.size
        return count
    }
}