package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.deku.leoz.central.data.jooq.tables.records.TblstatusRecord
import org.deku.leoz.central.data.jooq.tables.records.TblauftragcolliesRecord
import org.deku.leoz.central.data.jooq.tables.records.TblauftragRecord
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named
import javax.persistence.Persistence
import org.deku.leoz.central.data.jooq.tables.Tblauftragcollies
import org.deku.leoz.central.data.jooq.tables.Tblauftrag
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.Tblstatus
import org.deku.leoz.central.data.jooq.tables.records.TblfeldhistorieRecord
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.model.AdditionalInfo
import org.deku.leoz.model.Event
import org.deku.leoz.model.Reason
import org.deku.leoz.node.rest.DefaultProblem
import org.jooq.Field
import sx.time.toLocalDate
import sx.time.toTimestamp
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by JT on 18.07.17.
 */
@Named
class ParcelJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)

    lateinit var dslContext: DSLContext

    fun saveEvent(eventRecord: TblstatusRecord): Boolean {
        return (eventRecord.store() > 0)
    }

    fun statusExist(unitNo:Double,creator:String,status:Int):Boolean{
        val exist =dslContext.selectCount().from(Tblstatus.TBLSTATUS)
                .where(Tables.TBLSTATUS.PACKSTUECKNUMMER.eq(unitNo))
                        .and(Tables.TBLSTATUS.KZ_STATUSERZEUGER.eq(creator))
                        .and(Tables.TBLSTATUS.KZ_STATUS.eq(status.toUInteger()))
                .fetchOne(0,Int::class.java)
        return exist!=0
    }

    /**
    fun setSignaturePath(parcelNumber: String, path: String): Boolean {
    //update parcel
    //TblauftragcolliesRecord: bmpFileName = path
    return true
    }
     **/

    fun getUnitNo(parcelId: Int): Double? {
        if (parcelId == 0) return null
        return dslContext.select(Tables.TAD_V_ORDER_PARCEL.SCAN_ID)
                .from(Tables.TAD_V_ORDER_PARCEL)
                .where(Tables.TAD_V_ORDER_PARCEL.ID.eq(parcelId.toDouble()))
                .fetchOneInto(Double::class.java)
        /**
        val parcel=parcelId.toString()
        val orderpos=parcel.substring(11,parcel.length-11)
        val orderid=parcel.substring(0,11)
        return dslContext.select(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR)
        .from(Tables.TBLAUFTRAGCOLLIES)
        .where(Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(orderid.toDouble()))
        .and(Tables.TBLAUFTRAGCOLLIES.ORDERPOS.eq(orderpos.toShort()))
        .fetchOneInto(Double::class.java)
         **/
    }

    fun findParcelByUnitNumber(unitNo: Double): TblauftragcolliesRecord? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR.eq(unitNo))
                .and(Tables.TBLAUFTRAGCOLLIES.ORDERPOS.greaterThan(0))
                .and(Tables.TBLAUFTRAGCOLLIES.ORDERID.greaterThan(0.0))
                ?.fetchOneInto(Tblauftragcollies.TBLAUFTRAGCOLLIES)
    }

    fun findOrderByOrderNumber(orderNo: Double): TblauftragRecord? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAG)
                .where(Tables.TBLAUFTRAG.ORDERID.eq(orderNo))
                .and(Tables.TBLAUFTRAG.ORDERID.greaterThan(0.0))
                ?.fetchOneInto(Tblauftrag.TBLAUFTRAG)
    }


    fun findUnitsInBagByBagUnitNumber(bagUnitNo: Double): List<TblauftragcolliesRecord>? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(Tables.TBLAUFTRAGCOLLIES.BAGBELEGNRABC.eq(bagUnitNo))
                .fetchInto(TblauftragcolliesRecord::class.java)
    }
}

fun TblstatusRecord.setDate(date: Date) {
    this.datum = SimpleDateFormat("yyyyMMdd").format(date)
}

fun TblstatusRecord.setTime(date: Date) {
    this.zeit = SimpleDateFormat("HHmm").format(date)
}