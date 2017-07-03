package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.service.internal.OrderService
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named
import org.springframework.beans.factory.annotation.Qualifier
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.TrnVOrderParcelRecord
import org.deku.leoz.central.data.jooq.tables.records.TrnVOrderRecord
import org.deku.leoz.model.*
import org.deku.leoz.node.rest.DefaultProblem
import javax.ws.rs.core.Response

/**
 * Created by JT on 30.06.17.
 */
@Named
open class OrderJooqRepository {

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    fun findByScan(ScanId: String?): OrderService.Order? {
        if (ScanId!!.toLong() < 1000000000 && ScanId.toLong() > 9999999999999)
            throw DefaultProblem(
                    title = "wrong Scan id",
                    status = Response.Status.NOT_FOUND)

        val P = dslContext.fetchOne(Tables.TRN_V_ORDER_PARCEL, Tables.TRN_V_ORDER_PARCEL.SCAN_ID.eq(ScanId.toDouble()))
        if (P == null)
            throw DefaultProblem(
                    title = "Parcel not found",
                    status = Response.Status.NOT_FOUND)

        return findByID(P.orderId.toLong())
    }


    fun findByID(id: Long): OrderService.Order? {
        if (id < 10000000000 && id > 9999999999999)
            throw DefaultProblem(
                    title = "wrong Order id",
                    status = Response.Status.NOT_FOUND)

        val R = dslContext.fetchOne(Tables.TRN_V_ORDER, Tables.TRN_V_ORDER.ID.eq(id.toDouble()))
        if (R == null)
            throw DefaultProblem(
                    title = "Order not found",
                    status = Response.Status.NOT_FOUND)

        val O: OrderService.Order? = readOrder(R)
        val P = dslContext.fetchOne(Tables.TRN_V_ORDER_PARCEL, Tables.TRN_V_ORDER_PARCEL.ORDER_ID.eq(R.id))
        if (P != null)
            O!!.parcels = readParcel(P)

        return O
    }

    fun readParcel(r: TrnVOrderParcelRecord): List<OrderService.Order.Parcel> {
        val parcels: List<OrderService.Order.Parcel>? = null

        val p: OrderService.Order.Parcel = OrderService.Order.Parcel()
        p.number = r.scanId.toString()
        p.parcelType =  ParcelType.valueMap.getValue(r.parcelType)
        p.lastDeliveryListId = r.lastDeliveryListId.toInt()
        //P.information=
        p.dimension!!.weight = r.dimentionWeight

        parcels!!.plus(p)

        return parcels
    }


    fun readOrder(record: TrnVOrderRecord): OrderService.Order {
        val r = record
        val O: OrderService.Order? = null

        O!!.id = r.id.toLong()
        O.referenceIDToExchangeOrderID = r.referenceIdToExchangeId.toLong()
        O.carrier = Carrier.DER_KURIER

        if (r.pickupStation == r.deliveryStation && r.customerStation == r.deliveryStation)
            O.orderClassification = OrderClassification.PICKUP_DELIVERY
        else if (r.customerStation == r.deliveryStation)
            O.orderClassification = OrderClassification.DELIVERY
        else if (r.customerStation == r.pickupStation)
            O.orderClassification = OrderClassification.PICKUP


        O.pickupAddress.line1 = r.pickupAddressLine1
        O.pickupAddress.line2 = r.pickupAddressLine2
        O.pickupAddress.line3 = r.pickupAddressLine3
        O.pickupAddress.street = r.pickupAddressStreet
        O.pickupAddress.streetNo = r.pickupAddressStreetNo
        O.pickupAddress.countryCode = r.pickupAddressCountryCode
        O.pickupAddress.zipCode = r.pickupAddressZipCode
        O.pickupAddress.city = r.pickupAddressCity
        O.pickupService = OrderService.Order.Service(listOf(ParcelService.NO_ADDITIONAL_SERVICE))  //todo

        var info: OrderService.Order.AdditionalInformation? = null
        var infoList: List<OrderService.Order.AdditionalInformation>? = null
        if (r.pickupInformation1 != null) {
            info!!.additionalInformationType = AdditionalInformationType.LOADING_LIST_INFO
            info!!.information = r.pickupInformation1
            var infoList: List<OrderService.Order.AdditionalInformation>? = null
            infoList!!.plus(info)
            O!!.pickupInformation!!.additionalInformation = infoList
        }
        O.appointmentPickup.dateStart = r.appointmentPickupStart
        O.appointmentPickup.dateEnd = r.appointmentPickupEnd
        O.appointmentPickup.notBeforeStart = r.appointmentPickupNotBeforeStart == 1
        O.appointmentPickup.dateStart = r.appointmentPickupStart
        O.appointmentPickup.dateEnd = r.appointmentPickupEnd
        O.appointmentPickup.notBeforeStart = r.appointmentPickupNotBeforeStart == 1


        O.deliveryAddress.line1 = r.deliveryAddressLine1
        O.deliveryAddress.line2 = r.deliveryAddressLine2
        O.deliveryAddress.line3 = r.deliveryAddressLine3
        O.deliveryAddress.street = r.deliveryAddressStreet
        O.deliveryAddress.streetNo = r.deliveryAddressStreetNo
        O.deliveryAddress.countryCode = r.deliveryAddressCountryCode
        O.deliveryAddress.zipCode = r.deliveryAddressZipCode
        O.deliveryAddress.city = r.deliveryAddressCity
        O.deliveryService = OrderService.Order.Service(listOf(ParcelService.NO_ADDITIONAL_SERVICE)) //todo

        if (r.deliveryInformation != null) {
            info!!.additionalInformationType = AdditionalInformationType.LOADING_LIST_INFO
            info!!.information = r.deliveryInformation
            infoList = null
            infoList!!.plus(info)
            O!!.deliveryInformation!!.additionalInformation = infoList
        }
        O.appointmentDelivery.dateStart = r.appointmentDeliveryStart
        O.appointmentDelivery.dateEnd = r.appointmentDeliveryEnd
        O.appointmentDelivery.notBeforeStart = r.appointmentDeliveryNotBeforeStart == 1
        O.appointmentDelivery.dateStart = r.appointmentDeliveryStart
        O.appointmentDelivery.dateEnd = r.appointmentDeliveryEnd
        O.appointmentDelivery.notBeforeStart = r.appointmentDeliveryNotBeforeStart == 1

        return O
    }

}