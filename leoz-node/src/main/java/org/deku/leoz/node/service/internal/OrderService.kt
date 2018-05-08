package org.deku.leoz.node.service.internal

import org.deku.leoz.model.*
import org.deku.leoz.node.Application
import org.deku.leoz.node.data.repository.StationContractRepository
import org.deku.leoz.node.data.repository.OrderParcelRepository
import org.deku.leoz.node.data.repository.OrderRepository
import org.deku.leoz.node.data.repository.StationRepository
import org.deku.leoz.node.rest.restrictByDebitor
import org.deku.leoz.service.internal.OrderService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import sx.rs.RestProblem
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import org.deku.leoz.service.internal.OrderService.Type
import org.deku.leoz.service.internal.OrderService.Order
import org.deku.leoz.node.data.jpa.TadOrderParcel
import org.deku.leoz.node.data.jpa.TadOrder

@Component
@Path("internal/v1/order")
@Profile(Application.PROFILE_CLIENT_NODE)
class OrderService : org.deku.leoz.service.internal.OrderService {

    @Context
    private lateinit var httpRequest: HttpServletRequest

    @Inject
    private lateinit var orderRepository: OrderRepository

    @Inject
    private lateinit var orderParcelRepository: OrderParcelRepository

    @Inject
    private lateinit var stationContractRepo: StationContractRepository

    @Inject
    private lateinit var stationRepository: StationRepository

    override fun get(ids: List<Long>?, labelRef: String?, custRef: String?, parcelScan: String?): List<OrderService.Order> {

        val orders: List<OrderService.Order>

        when {
            parcelScan != null -> {
                // Query by parcel scan
                val parcelScanId = parcelScan.toLongOrNull()
                parcelScanId ?: throw RestProblem(
                        title = "ScanId not numeric - not implemented",
                        status = Response.Status.BAD_REQUEST
                )
                val orderRecord: TadOrder? = this.orderParcelRepository.findByScanId(parcelScanId)?.let { this.orderRepository.findByOrderId(it.orderid) }
                orderRecord ?: throw RestProblem(
                        title = "Order not found",
                        status = Response.Status.NOT_FOUND)


                val order = orderRecord.toOrder()

                httpRequest.restrictByDebitor { order.findOrderDebitorId(Type.DELIVERY) }

                orders = listOf(order)
            }
            labelRef != null -> {
                TODO("Lookup by label ref not implemented")
            }
            custRef != null -> {
                TODO("Lookup by cust ref not implemented")
            }
            ids != null -> {
                val rOrders = this.orderRepository.findByOrderIds(ids)

                if(rOrders.count()==0)
                    throw RestProblem(
                            title = "Order not found",
                            status = Response.Status.NOT_FOUND)

                val rParcelsByOrderId = this.orderParcelRepository
                        .findByOrderIds(rOrders.map { it.orderid })
                        .groupBy { it.orderid }

                // TODO: debitor restricition validation, but only if it was http request. check must be skipped for internal consumers

                orders = rOrders.map {
                    it.toOrder().also { order ->
                        order.parcels = rParcelsByOrderId
                                .getOrDefault(order.id, listOf())
                                .map { it.toParcel() }
                    }
                }
            }
            else -> TODO("Not supported")
        }

        orders.forEach { x ->
            x.parcels = this.orderParcelRepository
                    .findByOrderId(x.id)
                    .map { it.toParcel() }
        }

        return orders
    }

    override fun getById(id: Long): OrderService.Order {
        val order = this.orderRepository.findByOrderId(id)
                ?.toOrder()
                ?: throw RestProblem(
                        title = "Order not found",
                        status = Response.Status.NOT_FOUND)

        httpRequest.restrictByDebitor { order.findOrderDebitorId(Type.DELIVERY) }

        order.parcels = this.orderParcelRepository
                .findByOrderId(order.id)
                .map { it.toParcel() }

        return order
    }

    //todo include send_date
    fun OrderService.Order.findOrderDebitorId(pickupDeliver: Type): Int? {
        val stationNo: Int

        when (pickupDeliver) {
            Type.PICKUP -> stationNo = this.pickupStation
            Type.DELIVERY -> stationNo = this.deliveryStation
        }
        return stationRepository.findByStation(stationNo)?.let { stationContractRepo.findByStationId(it.stationId)?.debitorId }
    }

    fun TadOrder.toOrder(): OrderService.Order {
        val o = Order()
        val r = this
        o.id = this.orderid
        o.referenceIDToExchangeOrderID = r.referenceIdToExchangeId.toLong()
        o.carrier = Carrier.DER_KURIER
        if (r.pickupStation == r.deliveryStation && r.customerStation == r.deliveryStation)
            o.orderClassification = OrderClassification.PICKUP_DELIVERY
        else
            o.orderClassification = OrderClassification.DELIVERY
        o.pickupStation = r.pickupStation.toInt()
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

        o.pickupServices = ps

        o.pickupAppointment.dateStart = r.appointmentPickupStart
        o.pickupAppointment.dateEnd = r.appointmentPickupEnd
        o.pickupAppointment.notBeforeStart = r.appointmentPickupNotBeforeStart
        o.pickupAppointment.dateStart = r.appointmentPickupStart
        o.pickupAppointment.dateEnd = r.appointmentPickupEnd
        o.pickupAppointment.notBeforeStart = r.appointmentPickupNotBeforeStart

        o.deliveryStation = r.deliveryStation.toInt()
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
        o.deliveryAppointment.notBeforeStart = r.appointmentDeliveryNotBeforeStart
        o.deliveryAppointment.dateStart = r.appointmentDeliveryStart
        o.deliveryAppointment.dateEnd = r.appointmentDeliveryEnd
        o.deliveryAppointment.notBeforeStart = r.appointmentDeliveryNotBeforeStart

        return o

    }
    fun TadOrderParcel.toParcel():OrderService.Order.Parcel{
        val r = this
        val p = Order.Parcel()
        p.id = r.parcelId
        p.number = toUnitNo(r.scanId)
        p.parcelType = ParcelType.valueMap.getValue(r.parcelType.toInt())
        p.lastDeliveryListId = r.lastDeliveryListId?.toInt()
        if (DekuUnitNumber.parse(
                        toUnitNo(r.scanId)).value.type == UnitNumber.Type.Bag
                //todo take list of alowed deliveryStations from sys_prperties
                && r.deliveryStation == 956) {
            p.isDelivered = false
        } else {
            p.isDelivered = r.deliveredStatus.toInt() == 4
        }
        p.isMissing = r.deliveredStatus.toInt() == 8 && r.lastDeliveredEventReason.toInt() == 30
        p.isDamaged = r.isDamaged // is set form leo statusExport 1
        p.dimension.weight = r.dimensionWeightReal
        p.dimension.height = r.dimensionHeight
        p.dimension.length = r.dimensionLength
        p.dimension.width = r.dimensionWidth

        return p
    }
    fun toUnitNo(id: Long): String {
        return id.toString().padStart(11, padChar = '0')
    }

}