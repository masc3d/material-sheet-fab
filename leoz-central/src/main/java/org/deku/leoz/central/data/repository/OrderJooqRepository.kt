package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.MstUser
import org.deku.leoz.central.data.jooq.tables.TadVOrder
import org.deku.leoz.central.data.jooq.tables.records.TadVOrderParcelRecord
import org.deku.leoz.central.data.jooq.tables.records.TadVOrderRecord
import org.deku.leoz.central.data.prepared
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.jooq.Result
import org.jooq.ResultQuery
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.Stopwatch
import javax.inject.Inject
import javax.inject.Named

/**
 * Order repository
 * Created by JT on 30.06.17.
 */
@Named
open class OrderJooqRepository {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    fun findById(id: Long): TadVOrderRecord? {
        return dslContext.fetchOne(
                Tables.TAD_V_ORDER,
                Tables.TAD_V_ORDER.ID.eq(id.toDouble()))
    }

    fun findByIds(id: List<Long>): List<TadVOrderRecord> {
        return dslContext.fetch(
                Tables.TAD_V_ORDER,
                Tables.TAD_V_ORDER.ID.`in`(id.map { it.toDouble() })
        )
    }

    fun findByIdJoined(id: Long): Map<TadVOrderRecord, List<TadVOrderParcelRecord>> {
        // TODO just an untested example, not working presumably
        return dslContext.select()
                .from(Tables.TAD_V_ORDER)
                .join(Tables.TAD_V_ORDER_PARCEL)
                .on(Tables.TAD_V_ORDER_PARCEL.ORDER_ID.eq(Tables.TAD_V_ORDER.ID))
                .where(Tables.TAD_V_ORDER.ID.eq(id.toDouble()))
                .fetchGroups(Tables.TAD_V_ORDER, TadVOrderParcelRecord::class.java)
    }

    fun findByScan(scanId: String): TadVOrderRecord? {
        val rParcel = dslContext.fetchOne(
                Tables.TAD_V_ORDER_PARCEL,
                Tables.TAD_V_ORDER_PARCEL.SCAN_ID.eq(scanId.toDouble()))
        if (rParcel == null)
            return null
        return findById(rParcel.orderId.toLong()) //null!!!!!!
    }

    fun findParcelsByOrderId(id: Long): List<TadVOrderParcelRecord> {
        return dslContext.fetch(
                Tables.TAD_V_ORDER_PARCEL,
                Tables.TAD_V_ORDER_PARCEL.ORDER_ID.eq(id.toDouble())).sortAsc(Tables.TAD_V_ORDER_PARCEL.ID)
    }

    fun findParcelsByOrderIds(ids: List<Long>): List<TadVOrderParcelRecord> {
        return dslContext.fetch(
                Tables.TAD_V_ORDER_PARCEL,
                Tables.TAD_V_ORDER_PARCEL.ORDER_ID.`in`(ids.map { it.toDouble() })
        )
    }
}