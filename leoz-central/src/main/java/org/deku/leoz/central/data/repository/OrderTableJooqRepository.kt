package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.Tblauftrag
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TblauftragRecord


@Named
class OrderTableJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)

    lateinit var dslContext: DSLContext

    fun findOrderByOrderNumber(orderNo: Long): TblauftragRecord? {
        if(orderNo==0.toLong()) return null
        return dslContext.select()
                .from(Tables.TBLAUFTRAG)
                .where(Tables.TBLAUFTRAG.ORDERID.eq(orderNo.toDouble()))
                //.and(Tables.TBLAUFTRAG.ORDERID.greaterThan(0.0))
                ?.fetchOneInto(Tblauftrag.TBLAUFTRAG)
    }
}