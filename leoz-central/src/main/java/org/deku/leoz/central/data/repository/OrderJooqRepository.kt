package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.TadVOrderParcelRecord
import org.deku.leoz.central.data.jooq.tables.records.TadVOrderRecord
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
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

    /**
     * Find orders by ids.
     * The orders are guaranteed to be returned in the same order has the list of ids
     * @param ids List of order ids
     */
    fun findByIds(ids: List<Long>): List<TadVOrderRecord> {
        val set = LinkedHashSet(ids.map { it.toDouble() })

        return dslContext.fetch(
                Tables.TAD_V_ORDER,
                Tables.TAD_V_ORDER.ID.`in`(set)
        ).sortedWith(compareBy { set.indexOf(it.id) })
    }

    fun findByScan(scanId: String): TadVOrderRecord? {
        val rParcel = dslContext.fetchOne(
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
        return dslContext.fetch(
                Tables.TAD_V_ORDER_PARCEL,
                Tables.TAD_V_ORDER_PARCEL.ORDER_ID.eq(id.toDouble())
        )
    }

    /**
     * Find parcels by order ids. The parcels returned are in no particular order
     * @param ids Order ids
     */
    fun findParcelsByOrderIds(ids: List<Long>): List<TadVOrderParcelRecord> {
        return dslContext.fetch(
                Tables.TAD_V_ORDER_PARCEL,
                Tables.TAD_V_ORDER_PARCEL.ORDER_ID.`in`(ids.map { it.toDouble() })
        )
    }
}