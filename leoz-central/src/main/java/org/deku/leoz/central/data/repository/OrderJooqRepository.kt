package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.service.internal.OrderService
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named
import org.springframework.beans.factory.annotation.Qualifier
import org.deku.leoz.central.data.jooq.Tables
import org.jooq.Record

/**
 * Created by JT on 30.06.17.
 */
@Named
open class OrderJooqRepository {

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    fun findByID(Id: String): OrderService.Order? {

        var O: OrderService.Order? = null
//        if (Id.toLong() > 1000000000 && Id.toLong() < 99999999999)
        val R = dslContext.fetchOne(Tables.TRN_V_ORDER, Tables.TRN_V_ORDER.ID.eq(Id.toDouble()))
        O!!.id = R.id.toLong()
        O!!.referenceIDToExchangeOrderID = R.referenceIdToExchangeId.toLong()
        O!!.pickupAddress.line1 = R.pickupAddressLine1
        O!!.pickupAddress.line2 = R.pickupAddressLine2
        O!!.pickupAddress.line3 = R.pickupAddressLine3
        O!!.pickupAddress.street = R.pickupAddressStreet
        O!!.pickupAddress.streetNo = R.pickupAddressStreetNo
        O!!.pickupAddress.countryCode = R.pickupAddressCountryCode
        O!!.pickupAddress.zipCode = R.pickupAddressZipCode
        O!!.pickupAddress.city = R.pickupAddressCity
        O!!.deliveryAddress.line1 = R.deliveryAddressLine1
        O!!.deliveryAddress.line2 = R.deliveryAddressLine2
        O!!.deliveryAddress.line3 = R.deliveryAddressLine3
        O!!.deliveryAddress.street = R.deliveryAddressStreet
        O!!.deliveryAddress.streetNo = R.deliveryAddressStreetNo
        O!!.deliveryAddress.countryCode = R.deliveryAddressCountryCode
        O!!.deliveryAddress.zipCode = R.deliveryAddressZipCode
        O!!.deliveryAddress.city = R.deliveryAddressCity


//        O!!.carrier=R.carrier find from enum
//        O!!.deliveryService=R.service
        O!!.appointmentPickup.dateStart = R.appointmentPickupStart
        O!!.appointmentPickup.dateEnd = R.appointmentPickupEnd
        O!!.appointmentPickup.notBeforeStart = R.appointmentPickupNotBeforeStart == 1
        O!!.appointmentDelivery.dateStart = R.appointmentDeliveryStart
        O!!.appointmentDelivery.dateEnd = R.appointmentDeliveryEnd
        O!!.appointmentDelivery.notBeforeStart = R.appointmentDeliveryNotBeforeStart == 1

        return O
    }

}