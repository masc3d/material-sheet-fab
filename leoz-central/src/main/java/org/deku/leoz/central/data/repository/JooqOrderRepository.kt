package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.Tblauftrag
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TadVOrderParcelRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TadVOrderRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TblauftragRecord
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import javax.inject.Inject

/**
 * Order repository
 * Created by JT on 30.06.17.
 */
@Component
class JooqOrderRepository {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    fun findById(id: Long): TadVOrderRecord? {
        return dsl.fetchOne(
                Tables.TAD_V_ORDER,
                Tables.TAD_V_ORDER.ID.eq(id.toDouble()))
    }

    /**
     * Find orders by ids.
     * The orders are guaranteed to be returned in the same order has the list of ids
     * @param ids List of order ids
     */
    fun findByIds(ids: List<Long>): List<TadVOrderRecord> {
        val set = LinkedHashSet(ids.map { it.toDouble() })

        return dsl.fetch(
                Tables.TAD_V_ORDER,
                Tables.TAD_V_ORDER.ID.`in`(set)
        ).sortedWith(compareBy { set.indexOf(it.id) })
    }

    fun findByScan(scanId: String): TadVOrderRecord? {
        val rParcel = dsl.fetchOne(
                Tables.TAD_V_ORDER_PARCEL,
                Tables.TAD_V_ORDER_PARCEL.SCAN_ID.eq(scanId.toDouble()))
        if (rParcel == null)
            return null
        return findById(rParcel.orderId.toLong()) //null!!!!!!
    }

    /**
     * Find parcels by order
     * @param id Order id
     */
    fun findParcelsByOrderId(id: Long): List<TadVOrderParcelRecord> {
        return dsl.fetch(
                Tables.TAD_V_ORDER_PARCEL,
                Tables.TAD_V_ORDER_PARCEL.ORDER_ID.eq(id.toDouble())
        )
    }

    /**
     * Find parcels by order ids. The parcels returned are in no particular order
     * @param ids Order ids
     */
    fun findParcelsByOrderIds(ids: List<Long>): List<TadVOrderParcelRecord> {
        return dsl.fetch(
                Tables.TAD_V_ORDER_PARCEL,
                Tables.TAD_V_ORDER_PARCEL.ORDER_ID.`in`(ids.map { it.toDouble() })
        )
    }

    fun findOrderByOrderNumber(orderNo: Long): TblauftragRecord? {
        if(orderNo==0.toLong()) return null
        return dsl.select()
                .from(Tables.TBLAUFTRAG)
                .where(Tables.TBLAUFTRAG.ORDERID.eq(orderNo.toDouble()))
                //.and(Tables.TBLAUFTRAG.ORDERID.greaterThan(0.0))
                ?.fetchOneInto(Tblauftrag.TBLAUFTRAG)
    }
}