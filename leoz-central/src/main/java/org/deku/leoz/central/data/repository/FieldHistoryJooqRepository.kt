package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Routines
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.model.counter
import org.deku.leoz.time.toShortTime
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import javax.inject.Inject
import javax.inject.Named
import sx.time.toTimestamp
import java.util.*


@Named
class FieldHistoryJooqRepository {

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    lateinit var dslContext: DSLContext

    fun addEntry(orderId: Long, unitNo: Long, fieldName: String, oldValue: String, newValue: String, changer: String, point: String) {
        val fieldHistoryRecord = dslContext.newRecord(Tables.TBLFELDHISTORIE)
        fieldHistoryRecord.orderid = orderId.toDouble()
        fieldHistoryRecord.belegnummer = unitNo.toDouble()
        fieldHistoryRecord.feldname = fieldName
        fieldHistoryRecord.oldvalue = oldValue
        fieldHistoryRecord.newvalue = newValue
        fieldHistoryRecord.changer = changer
        fieldHistoryRecord.point = point
        fieldHistoryRecord.id = Routines.fTan(dslContext.configuration(), counter.FIELD_HISTORY.value).toInt().toUInteger()
        fieldHistoryRecord.store()
    }
}