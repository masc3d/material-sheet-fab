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

        val O: OrderService.Order? = null
//        if (Id.toLong() > 1000000000 && Id.toLong() < 99999999999)
        val R = dslContext.fetchOne(Tables.TRN_V_ORDER, Tables.TRN_V_ORDER.ID.eq(Id.toDouble()))
        O!!.id = R.id.toLong()

        //O.carrier=R.carrier

        return O
    }

}