package org.deku.leoz.central.service.internal

import org.deku.leoz.central.data.jooq.tables.records.TadVOrderParcelRecord
import org.deku.leoz.central.data.jooq.tables.records.TadVOrderRecord
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.model.*
import sx.rs.DefaultProblem
import org.deku.leoz.service.internal.OrderService
import org.deku.leoz.service.internal.OrderService.Order
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * Order service implementation
 * Created by JT on 30.06.17.
 */
@Named
@Path("internal/v1/order")
class OrderService : OrderService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var orderRepository: OrderJooqRepository

    @Inject
    private lateinit var parcelRepository: ParcelJooqRepository

    @Inject
    private lateinit var statusRepository: StatusJooqRepository

    @Inject
    private lateinit var userRepository: UserJooqRepository

    @Inject
    private lateinit var depotRepository: DepotJooqRepository

    override fun get(labelRef: String?, custRef: String?, parcelScan: String?): List<OrderService.Order> {
        return get(labelRef, custRef, parcelScan, null)
    }

    override fun get(labelRef: String?, custRef: String?, parcelScan: String?, apiKey: String?): List<OrderService.Order> {
        val orders: List<Order>

        when {
            parcelScan != null -> {
                // Query by parcel scan
                val order = this.orderRepository.findByScan(parcelScan)
                        ?.toOrder()
                        ?: throw DefaultProblem(
                        title = "Order not found",
                        status = Response.Status.NOT_FOUND)
//to be removed if mobile supports apikeys
                if (apiKey != null) {
//--<
                    apiKey ?:
                            throw DefaultProblem(status = Response.Status.UNAUTHORIZED)

                    val authorizedUserRecord = userRepository.findByKey(apiKey)
                    authorizedUserRecord ?:
                            throw DefaultProblem(status = Response.Status.UNAUTHORIZED)
                    when {
                        order.findOrderDebitorId(Type.DELIVERY) != authorizedUserRecord.debitorId
                        ->
                            throw DefaultProblem(status = Response.Status.FORBIDDEN)
                    }
                }
                orders = listOf(order)
            }
            else -> TODO("Handle other query types here")
        }

        orders.forEach { order ->
            order.parcels = this.orderRepository
                    .findParcelsByOrderId(order.id)
                    .map { it.toParcel() }
        }

        return orders
    }

    override fun getById(id: Long): OrderService.Order {
        return getById(id, null)
    }

    override fun getById(id: Long, apiKey: String?): OrderService.Order {

        val order = this.orderRepository.findById(id)
                ?.toOrder()
                ?: throw DefaultProblem(
                title = "Order not found",
                status = Response.Status.NOT_FOUND)
//to be removed if mobile supports apikeys
        if (apiKey != null) {
//--<
            apiKey ?:
                    throw DefaultProblem(status = Response.Status.UNAUTHORIZED)

            val authorizedUserRecord = userRepository.findByKey(apiKey)
            authorizedUserRecord ?:
                    throw DefaultProblem(status = Response.Status.UNAUTHORIZED)
            when {
                order.findOrderDebitorId(Type.DELIVERY) != authorizedUserRecord.debitorId
                ->
                    throw DefaultProblem(status = Response.Status.FORBIDDEN)
            }
        }

        order.parcels = this.orderRepository
                .findParcelsByOrderId(order.id)
                .map { it.toParcel() }

        return order
    }

    fun getByIds(ids: List<Long>): List<OrderService.Order> {
        val rOrders = this.orderRepository.findByIds(ids)

        val rParcelsByOrderId = this.orderRepository
                .findParcelsByOrderIds(rOrders.map { it.id.toLong() })
                .groupBy { it.orderId.toLong() }

        return rOrders.map {
            it.toOrder().also { order ->
                order.parcels = rParcelsByOrderId
                        .getOrDefault(order.id, listOf())
                        .map { it.toParcel() }
            }
        }
    }

    fun Order.findOrderDebitorId(pickupDeliver: Type): Int {
        var station: Long

        when (pickupDeliver) {
            Type.PICKUP -> station = this.pickupStation
            Type.DELIVERY -> station = this.deliveryStation
        }
//                depotRepository.findDebitorDepots(authorizedUserRecord.debitorId).map { (deliveryListRecord.deliveryStation.toInt()) }.isEmpty()

        return depotRepository.findDebitor(station)
    }

    enum class Type {
        PICKUP,
        DELIVERY
    }

    /**
     * Order record conversion extension
     */
    fun TadVOrderRecord.toOrder(): Order {
        val o = Order()
        val r = this

        o.id = r.id.toLong()
        o.referenceIDToExchangeOrderID = r.referenceIdToExchangeId.toLong()
        o.carrier = Carrier.DER_KURIER
        if (r.pickupStation == r.deliveryStation && r.customerStation == r.deliveryStation)
            o.orderClassification = OrderClassification.PICKUP_DELIVERY
        else
            o.orderClassification = OrderClassification.DELIVERY
        o.pickupStation = r.pickupStation.toLong()
        o.pickupAddress.line1 = r.pickupAddressLine1
        o.pickupAddress.line2 = r.pickupAddressLine2
        o.pickupAddress.line3 = r.pickupAddressLine3
        o.pickupAddress.phoneNumber = r.pickupAddressPhonenumber
        o.pickupAddress.street = r.pickupAddressStreet
        o.pickupAddress.streetNo = r.pickupAddressStreetNo
        o.pickupAddress.countryCode = r.pickupAddressCountryCode
        o.pickupAddress.zipCode = r.pickupAddressZipCode
        o.pickupAddress.city = r.pickupAddressCity
        if (r.pickupInformation1 != null) {
            o.pickupNotice = r.pickupInformation1
        }
        val pickupServiceValue = 0   //todo take from Mysql
        val s: Array<ParcelService> = ParcelService.values()
        val ps: MutableList<ParcelService> = arrayListOf()
        if (pickupServiceValue.toLong() > 0) {
            s.forEach {
                if (((it.serviceId and pickupServiceValue.toLong()) == it.serviceId) && it.serviceId > 0)
                    ps.add(ParcelService.valueOf(it.name))
            }
        }
/*
        else {
            ps.add(ParcelService.valueOf(ParcelService.NO_ADDITIONAL_SERVICE.name))
        }
*/
        o.pickupServices = ps

        o.pickupAppointment.dateStart = r.appointmentPickupStart
        o.pickupAppointment.dateEnd = r.appointmentPickupEnd
        o.pickupAppointment.notBeforeStart = r.appointmentPickupNotBeforeStart == 1
        o.pickupAppointment.dateStart = r.appointmentPickupStart
        o.pickupAppointment.dateEnd = r.appointmentPickupEnd
        o.pickupAppointment.notBeforeStart = r.appointmentPickupNotBeforeStart == 1

        o.deliveryStation = r.deliveryStation.toLong()
        o.deliveryAddress.line1 = r.deliveryAddressLine1
        o.deliveryAddress.line2 = r.deliveryAddressLine2
        o.deliveryAddress.line3 = r.deliveryAddressLine3
        o.deliveryAddress.phoneNumber = r.deliveryAddressPhonenumber
        o.deliveryAddress.street = r.deliveryAddressStreet
        o.deliveryAddress.streetNo = r.deliveryAddressStreetNo
        o.deliveryAddress.countryCode = r.deliveryAddressCountryCode
        o.deliveryAddress.zipCode = r.deliveryAddressZipCode
        o.deliveryAddress.city = r.deliveryAddressCity

        val ds: MutableList<ParcelService> = arrayListOf()
        ds.clear()
        if (r.service.toLong() > 0) {
            s.forEach {
                if (((it.serviceId and r.service.toLong()) == it.serviceId) && it.serviceId > 0)
                    ds.add(ParcelService.valueOf(it.name))
            }
        }
/*
        else {
            ds.add(ParcelService.valueOf(ParcelService.NO_ADDITIONAL_SERVICE.name))
        }
*/
        o.deliveryServices = ds

        if (r.cashAmount > 0 && (r.service.toLong() and ParcelService.CASH_ON_DELIVERY.serviceId) == ParcelService.CASH_ON_DELIVERY.serviceId) {
            val cs = Order.CashService()
            cs.cashAmount = r.cashAmount
            o.deliveryCashService = cs
        }
        if (r.deliveryInformation != null) {
            o.deliveryNotice = r.deliveryInformation
        }

        o.deliveryAppointment.dateStart = r.appointmentDeliveryStart
        o.deliveryAppointment.dateEnd = r.appointmentDeliveryEnd
        o.deliveryAppointment.notBeforeStart = r.appointmentDeliveryNotBeforeStart == 1
        o.deliveryAppointment.dateStart = r.appointmentDeliveryStart
        o.deliveryAppointment.dateEnd = r.appointmentDeliveryEnd
        o.deliveryAppointment.notBeforeStart = r.appointmentDeliveryNotBeforeStart == 1

        return o
    }

    /**
     * Order parcel record conversion extension
     */
    fun TadVOrderParcelRecord.toParcel(): Order.Parcel {
        val r = this
        val p = Order.Parcel()
        p.id = r.id.toLong()
        p.number = toUnitNo(r.scanId)
        p.parcelType = ParcelType.valueMap.getValue(r.parcelType)
        p.lastDeliveryListId = r.lastDeliveryListId?.toInt()
        p.isDelivered = r.deliveredStatus.toInt() == 4
        p.isMissing = r.deliveredStatus.toInt() == 8 && r.lastDeliveredEventReason.toInt() == 30
        p.isDamaged = statusRepository.statusExist(r.scanId.toLong(), "E", 8, 31)
        p.dimension.weight = r.dimentionWeight
        p.dimension.height = r.dimensionHeight.toInt()
        p.dimension.length = r.dimensionLength.toInt()
        p.dimension.width = r.dimensionWidth.toInt()

        return p
    }

    fun toUnitNo(id: Double): String {
        return id.toLong().toString().padStart(11, padChar = '0')
    }
}

