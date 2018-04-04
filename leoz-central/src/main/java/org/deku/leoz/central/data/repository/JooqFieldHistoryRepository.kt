package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Routines
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TblauftragRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TblauftragcolliesRecord
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.model.counter
import org.deku.leoz.time.toGregorianLongDateString
import org.deku.leoz.time.toGregorianLongDateTimeString
import org.deku.leoz.time.toShortTime
import org.jooq.DSLContext
import org.jooq.UpdatableRecord
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import sx.time.threeten.toLocalDateTimeBp
import java.sql.Timestamp
import java.util.*
import javax.inject.Inject


@Component
class JooqFieldHistoryRepository {

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    lateinit var dsl: DSLContext

    fun addEntry(orderId: Long, unitNo: Long, fieldName: String, oldValue: String, newValue: String, changer: String, point: String) {
        val fieldHistoryRecord = dsl.newRecord(Tables.TBLFELDHISTORIE)
        fieldHistoryRecord.orderid = orderId.toDouble()
        fieldHistoryRecord.belegnummer = unitNo.toDouble()
        fieldHistoryRecord.feldname = fieldName
        fieldHistoryRecord.oldvalue = oldValue
        fieldHistoryRecord.newvalue = newValue
        fieldHistoryRecord.changer = changer
        fieldHistoryRecord.point = point
        fieldHistoryRecord.id = Routines.fTan(dsl.configuration(), counter.FIELD_HISTORY.value).toInt().toUInteger()
        fieldHistoryRecord.store()
    }
}

/**
 * Extension method for storing record with field history
 */
fun <R : UpdatableRecord<R>> UpdatableRecord<R>.storeWithHistory(unitNo: Long, changer: String, point: String) {

    val dsl = this.configuration().dsl()

    this.fields().forEach {
        if (this.changed(it)) {//changed!=modified

            val fieldHistoryRecord = dsl.newRecord(Tables.TBLFELDHISTORIE)
            if (this is TblauftragcolliesRecord)
                fieldHistoryRecord.orderid = this.orderid //this.field("orderid").getValue(this).toString().toLong().toDouble()
            else if (this is TblauftragRecord)
                fieldHistoryRecord.orderid = this.orderid
            else
                fieldHistoryRecord.orderid = 0.0
            fieldHistoryRecord.belegnummer = unitNo.toDouble()
            fieldHistoryRecord.feldname = it.name
            if (it.dataType.typeName.equals("timestamp")) {

                val dtOld = it.original(this) as Date?
                val dtOldTimestamp = (it.original(this) as Timestamp?)?.toLocalDateTime()
                val dtOldValue: String
                if (dtOld == null) {
                    dtOldValue = "null"
                } else if (dtOldTimestamp!!.year <= 1900) {
                    dtOldValue = dtOld.toShortTime().toString()
                } else if (dtOldTimestamp.hour == 0 && dtOldTimestamp.minute == 0 && dtOldTimestamp.second == 0) {
                    dtOldValue = dtOld.toGregorianLongDateString()
                } else {
                    dtOldValue = dtOld.toGregorianLongDateTimeString()
                }
                fieldHistoryRecord.oldvalue = dtOldValue

                val dt = it.getValue(this) as Date?
                val dtTimestamp = (it.getValue(this) as Timestamp?)?.toLocalDateTime()
                val dtNewValue: String
                if (dt == null) {
                    dtNewValue = "null"
                } else if (dtTimestamp!!.year <= 1900) {
                    dtNewValue = dt.toShortTime().toString()
                } else if (dtTimestamp.hour == 0 && dtTimestamp.minute == 0 && dtTimestamp.second == 0) {
                    dtNewValue = dt.toGregorianLongDateString()
                } else {
                    dtNewValue = dt.toGregorianLongDateTimeString()
                }
                fieldHistoryRecord.newvalue = dtNewValue
            } else {
                fieldHistoryRecord.oldvalue = it.original(this)?.toString() ?: ""
                fieldHistoryRecord.newvalue = it.getValue(this)?.toString() ?: "null"
            }

            fieldHistoryRecord.changer = changer
            fieldHistoryRecord.point = point

            if (fieldHistoryRecord.oldvalue != fieldHistoryRecord.newvalue) {
                fieldHistoryRecord.id = Routines.fTan(dsl.configuration(), counter.FIELD_HISTORY.value).toInt().toUInteger()
                fieldHistoryRecord.store()
            }

            this.store(it)
        }
    }
}

fun <R : UpdatableRecord<R>> UpdatableRecord<R>.storeWithHistoryExportservice(unitNo: Long) {
    this.storeWithHistory(unitNo, "WEB", "EX")
}

fun <R : UpdatableRecord<R>> UpdatableRecord<R>.storeWithHistoryParcelprocessing(unitNo: Long) {
    this.storeWithHistory(unitNo, "Z", "PP")
}

fun <R : UpdatableRecord<R>> UpdatableRecord<R>.storeWithHistoryImportservice(unitNo: Long) {
    this.storeWithHistory(unitNo, "WEB", "IM")
}