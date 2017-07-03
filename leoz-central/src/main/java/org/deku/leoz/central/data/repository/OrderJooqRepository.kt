package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.TrnVOrderParcelRecord
import org.deku.leoz.central.data.jooq.tables.records.TrnVOrderRecord
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import javax.inject.Inject
import javax.inject.Named

/**
 * Order repository
 * Created by JT on 30.06.17.
 */
@Named
open class OrderJooqRepository {

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    fun findByID(id: Long): TrnVOrderRecord? {
        return dslContext.fetchOne(
                Tables.TRN_V_ORDER,
                Tables.TRN_V_ORDER.ID.eq(id.toDouble()))
    }

    fun findByScan(ScanId: String): TrnVOrderRecord? {
        val rParcel = dslContext.fetchOne(
                Tables.TRN_V_ORDER_PARCEL,
                Tables.TRN_V_ORDER_PARCEL.SCAN_ID.eq(ScanId.toDouble()))

        return findByID(rParcel.orderId.toLong())
    }

    fun findParcelsByOrderId(id: Long): List<TrnVOrderParcelRecord> {
        return dslContext.fetch(
                Tables.TRN_V_ORDER_PARCEL,
                Tables.TRN_V_ORDER_PARCEL.ORDER_ID.eq(id.toDouble()))

    }
}