package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.MstUser
import org.deku.leoz.central.data.jooq.tables.TrnVOrder
import org.deku.leoz.central.data.jooq.tables.records.TrnVOrderParcelRecord
import org.deku.leoz.central.data.jooq.tables.records.TrnVOrderRecord
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.jooq.Result
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
        // TODO just an untested example, not working presumably
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
        if (rParcel == null)
            return null
        return findById(rParcel.orderId.toLong()) //null!!!!!!
    }

//    fun findParcelsByOrderId(id: Long) : Result<Record> {
//        return dslContext.select().from(Tables.TRN_V_ORDER_PARCEL).where(
//                Tables.TRN_V_ORDER_PARCEL.ORDER_ID.eq(id.toDouble())).fetch()
//    }

    fun findParcelsByOrderId(id: Long): List<TrnVOrderParcelRecord> {

//    Tables.TRN_V_ORDER_PARCEL,
//    Tables.TRN_V_ORDER_PARCEL.ORDER_ID.eq(id.toDouble()))

//        return dslContext.select()
//                .from(Tables.MST_USER.innerJoin(Tables.MST_KEY)
//                        .on(Tables.MST_USER.KEY_ID.eq(Tables.MST_KEY.KEY_ID)))
//                .where(Tables.MST_KEY.KEY.eq(apiKey))?.fetchOneInto(MstUser.MST_USER)
//}
        return dslContext.fetch(
                Tables.TRN_V_ORDER_PARCEL,
                Tables.TRN_V_ORDER_PARCEL.ORDER_ID.eq(id.toDouble()))

}
}