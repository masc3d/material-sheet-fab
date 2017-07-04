package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.TrnVOrder
import org.deku.leoz.central.data.jooq.tables.records.TrnVOrderParcelRecord
import org.deku.leoz.central.data.jooq.tables.records.TrnVOrderRecord
import org.jooq.DSLContext
import org.jooq.RecordMapper
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

    fun findById(id: Long): TrnVOrderRecord? {
        return dslContext.fetchOne(
                Tables.TRN_V_ORDER,
                Tables.TRN_V_ORDER.ID.eq(id.toDouble()))
    }

    fun findByIdJoined(id: Long): Map<TrnVOrderRecord, List<TrnVOrderParcelRecord>> {
        return dslContext.select()
                .from(Tables.TRN_V_ORDER)
                .join(Tables.TRN_V_ORDER_PARCEL)
                .on(Tables.TRN_V_ORDER_PARCEL.ORDER_ID.eq(Tables.TRN_V_ORDER.ID))
                .where(Tables.TRN_V_ORDER.ID.eq(id.toDouble()))
                .fetchGroups(Tables.TRN_V_ORDER, TrnVOrderParcelRecord::class.java)
    }

    fun findByScan(scanId: String): TrnVOrderRecord? {
        val rParcel = dslContext.fetchOne(
                Tables.TRN_V_ORDER_PARCEL,
                Tables.TRN_V_ORDER_PARCEL.SCAN_ID.eq(scanId.toDouble()))

        return findById(rParcel.orderId.toLong())
    }

    fun findParcelsByOrderId(id: Long): List<TrnVOrderParcelRecord> {
        return dslContext.fetch(
                Tables.TRN_V_ORDER_PARCEL,
                Tables.TRN_V_ORDER_PARCEL.ORDER_ID.eq(id.toDouble()))

    }
}