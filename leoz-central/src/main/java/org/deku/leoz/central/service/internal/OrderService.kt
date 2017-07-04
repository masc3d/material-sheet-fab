package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.tables.records.TrnVOrderParcelRecord
import org.deku.leoz.central.data.jooq.tables.records.TrnVOrderRecord
import org.deku.leoz.central.data.repository.OrderJooqRepository
import org.deku.leoz.model.AdditionalInformationType
import org.deku.leoz.model.Carrier
import org.deku.leoz.model.OrderClassification
import org.deku.leoz.model.ParcelService
import org.deku.leoz.model.ParcelType
import org.deku.leoz.node.rest.DefaultProblem
import org.deku.leoz.service.internal.OrderService
import org.deku.leoz.service.internal.OrderService.Order
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.auth.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * Order service implementation
 * Created by JT on 30.06.17.
 */
@Named
@ApiKey(false)
@Path("internal/v1/order")
class OrderService : OrderService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext
    @Inject
    private lateinit var orderRepository: OrderJooqRepository

    override fun get(labelRef: String?, custRef: String?, parcelScan: String?): List<OrderService.Order> {
        val orders: List<Order>

        when {
            parcelScan != null -> {
                // Query by parcel scan

                // TODO: if "parcelScan" is a parcel number which has constraints, those should be handled by a ParcelNumber model class which throws on parsing a parcel number
                if (parcelScan.toLong() < 1000000000 && parcelScan.toLong() > 9999999999999)
                    throw DefaultProblem(
                            title = "Wrong Scan id",
                            status = Response.Status.NOT_FOUND)

                val order = this.orderRepository.findByScan(parcelScan)
                        ?.toOrder()
                        ?: throw DefaultProblem(
                        title = "Order not found",
                        status = Response.Status.NOT_FOUND)

                orders = listOf(order)
            }

            else -> TODO("Handle other query types here")
        }

        // Complement order parcels
        orders.forEach {
            it.parcels = this.orderRepository.findParcelsByOrderId(it.id)
                .map { it.toParcel() }
        }

        return orders
    }

    override fun getById(id: Long): OrderService.Order {
        if (id < 10000000000 && id > 9999999999999)
            throw DefaultProblem(
                    title = "Wrong Order id",
                    status = Response.Status.NOT_FOUND)

        val order = this.orderRepository.findByID(id)
                ?.toOrder()
                ?: throw DefaultProblem(
                title = "Order not found",
                status = Response.Status.NOT_FOUND)

        // Complement order parcels
        order.parcels = this.orderRepository.findParcelsByOrderId(order.id)
                .map { it.toParcel() }

        return order
    }

    /**
     * Order record conversion extension
     */
    fun TrnVOrderRecord.toOrder(): Order {
        val o = Order()
        val r = this

        o.id = r.id.toLong()
        o.referenceIDToExchangeOrderID = r.referenceIdToExchangeId.toLong()
        o.carrier = Carrier.DER_KURIER

        if (r.pickupStation == r.deliveryStation && r.customerStation == r.deliveryStation)
            o.orderClassification = OrderClassification.PICKUP_DELIVERY
        else if (r.customerStation == r.deliveryStation)
            o.orderClassification = OrderClassification.DELIVERY
        else if (r.customerStation == r.pickupStation)
            o.orderClassification = OrderClassification.PICKUP


        o.pickupAddress.line1 = r.pickupAddressLine1
        o.pickupAddress.line2 = r.pickupAddressLine2
        o.pickupAddress.line3 = r.pickupAddressLine3
        o.pickupAddress.street = r.pickupAddressStreet
        o.pickupAddress.streetNo = r.pickupAddressStreetNo
        o.pickupAddress.countryCode = r.pickupAddressCountryCode
        o.pickupAddress.zipCode = r.pickupAddressZipCode
        o.pickupAddress.city = r.pickupAddressCity
        o.pickupService = OrderService.Order.Service(listOf(ParcelService.NO_ADDITIONAL_SERVICE))  //todo

        if (r.pickupInformation1 != null) {
            o.pickupInformation = Order.Information(
                    additionalInformation = listOf(
                            Order.AdditionalInformation(
                                    additionalInformationType = AdditionalInformationType.LOADING_LIST_INFO,
                                    information = r.pickupInformation1
                            )
                    )
            )
        }

        o.appointmentPickup.dateStart = r.appointmentPickupStart
        o.appointmentPickup.dateEnd = r.appointmentPickupEnd
        o.appointmentPickup.notBeforeStart = r.appointmentPickupNotBeforeStart == 1
        o.appointmentPickup.dateStart = r.appointmentPickupStart
        o.appointmentPickup.dateEnd = r.appointmentPickupEnd
        o.appointmentPickup.notBeforeStart = r.appointmentPickupNotBeforeStart == 1


        o.deliveryAddress.line1 = r.deliveryAddressLine1
        o.deliveryAddress.line2 = r.deliveryAddressLine2
        o.deliveryAddress.line3 = r.deliveryAddressLine3
        o.deliveryAddress.street = r.deliveryAddressStreet
        o.deliveryAddress.streetNo = r.deliveryAddressStreetNo
        o.deliveryAddress.countryCode = r.deliveryAddressCountryCode
        o.deliveryAddress.zipCode = r.deliveryAddressZipCode
        o.deliveryAddress.city = r.deliveryAddressCity
        o.deliveryService = OrderService.Order.Service(listOf(ParcelService.NO_ADDITIONAL_SERVICE)) //todo

        if (r.deliveryInformation != null) {
            o.deliveryInformation = Order.Information(
                    additionalInformation = listOf(
                            Order.AdditionalInformation(
                                    additionalInformationType = AdditionalInformationType.LOADING_LIST_INFO,
                                    information = r.deliveryInformation
                            )
                    )
            )
        }

        o.appointmentDelivery.dateStart = r.appointmentDeliveryStart
        o.appointmentDelivery.dateEnd = r.appointmentDeliveryEnd
        o.appointmentDelivery.notBeforeStart = r.appointmentDeliveryNotBeforeStart == 1
        o.appointmentDelivery.dateStart = r.appointmentDeliveryStart
        o.appointmentDelivery.dateEnd = r.appointmentDeliveryEnd
        o.appointmentDelivery.notBeforeStart = r.appointmentDeliveryNotBeforeStart == 1

        return o
    }

    /**
     * Order parcel record conversion extension
     */
    fun TrnVOrderParcelRecord.toParcel(): Order.Parcel {
        val r = this
        val p = OrderService.Order.Parcel()

        p.number = r.scanId.toString()
        p.parcelType = ParcelType.valueMap.getValue(r.parcelType)
        p.lastDeliveryListId = r.lastDeliveryListId.toInt()
        //P.information=
        p.dimension!!.weight = r.dimentionWeight

        return p
    }
}