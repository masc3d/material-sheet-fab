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
import org.deku.leoz.model.AdditionalInformationType
import org.deku.leoz.model.Carrier
import org.deku.leoz.model.OrderClassification
import org.deku.leoz.model.ParcelService
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

        var O: OrderService.Order? = readOrder(R)
        val P = dslContext.fetchOne(Tables.TRN_V_ORDER_PARCEL, Tables.TRN_V_ORDER_PARCEL.ORDER_ID.eq(R.id))
        if (P !=null)
            O!!.parcels = readParcel(P)

        return O

    }

    fun readParcel(R: TrnVOrderParcelRecord): List<OrderService.Order.Parcel> {
        val Parcels: List<OrderService.Order.Parcel>? = null

        val P : OrderService.Order.Parcel=OrderService.Order.Parcel()
        P.number=R.scanId.toString()
        //P.parcelType=R.parcelType
        P.lastDeliveryListId=R.lastDeliveryListId.toInt()
        //P.information=
        P.dimension!!.weight=R.dimentionWeight

        Parcels!!.plus(P)

        return Parcels!!
    }


    fun readOrder(R: TrnVOrderRecord): OrderService.Order {

        var O: OrderService.Order? = null

        O!!.id = R.id.toLong()
        O.referenceIDToExchangeOrderID = R.referenceIdToExchangeId.toLong()
        O.carrier = Carrier.DER_KURIER

        if (R.pickupStation == R.deliveryStation && R.customerStation == R.deliveryStation)
            O.orderClassification = OrderClassification.PICKUP_DELIVERY
        else if (R.customerStation == R.deliveryStation)
            O.orderClassification = OrderClassification.DELIVERY
        else if (R.customerStation == R.pickupStation)
            O.orderClassification = OrderClassification.PICKUP


        O.pickupAddress.line1 = R.pickupAddressLine1
        O.pickupAddress.line2 = R.pickupAddressLine2
        O.pickupAddress.line3 = R.pickupAddressLine3
        O.pickupAddress.street = R.pickupAddressStreet
        O.pickupAddress.streetNo = R.pickupAddressStreetNo
        O.pickupAddress.countryCode = R.pickupAddressCountryCode
        O.pickupAddress.zipCode = R.pickupAddressZipCode
        O.pickupAddress.city = R.pickupAddressCity
        O.pickupService = OrderService.Order.Service(listOf(ParcelService.NO_ADDITIONAL_SERVICE))  //todo

        var info: OrderService.Order.AdditionalInformation? = null
        var infoList: List<OrderService.Order.AdditionalInformation>? = null
        if (R.pickupInformation1 != null) {
            info!!.additionalInformationType = AdditionalInformationType.LOADING_LIST_INFO
            info!!.information = R.pickupInformation1
            var infoList: List<OrderService.Order.AdditionalInformation>? = null
            infoList!!.plus(info)
            O!!.pickupInformation!!.additionalInformation = infoList
        }
        O.appointmentPickup.dateStart = R.appointmentPickupStart
        O.appointmentPickup.dateEnd = R.appointmentPickupEnd
        O.appointmentPickup.notBeforeStart = R.appointmentPickupNotBeforeStart == 1
        O.appointmentPickup.dateStart = R.appointmentPickupStart
        O.appointmentPickup.dateEnd = R.appointmentPickupEnd
        O.appointmentPickup.notBeforeStart = R.appointmentPickupNotBeforeStart == 1


        O.deliveryAddress.line1 = R.deliveryAddressLine1
        O.deliveryAddress.line2 = R.deliveryAddressLine2
        O.deliveryAddress.line3 = R.deliveryAddressLine3
        O.deliveryAddress.street = R.deliveryAddressStreet
        O.deliveryAddress.streetNo = R.deliveryAddressStreetNo
        O.deliveryAddress.countryCode = R.deliveryAddressCountryCode
        O.deliveryAddress.zipCode = R.deliveryAddressZipCode
        O.deliveryAddress.city = R.deliveryAddressCity
        O.deliveryService = OrderService.Order.Service(listOf(ParcelService.NO_ADDITIONAL_SERVICE)) //todo

        if (R.deliveryInformation != null) {
            info!!.additionalInformationType = AdditionalInformationType.LOADING_LIST_INFO
            info!!.information = R.deliveryInformation
            infoList = null
            infoList!!.plus(info)
            O!!.deliveryInformation!!.additionalInformation = infoList
        }
        O.appointmentDelivery.dateStart = R.appointmentDeliveryStart
        O.appointmentDelivery.dateEnd = R.appointmentDeliveryEnd
        O.appointmentDelivery.notBeforeStart = R.appointmentDeliveryNotBeforeStart == 1
        O.appointmentDelivery.dateStart = R.appointmentDeliveryStart
        O.appointmentDelivery.dateEnd = R.appointmentDeliveryEnd
        O.appointmentDelivery.notBeforeStart = R.appointmentDeliveryNotBeforeStart == 1

        return O
    }

}