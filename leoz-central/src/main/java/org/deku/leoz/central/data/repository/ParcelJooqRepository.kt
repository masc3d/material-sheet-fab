package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.deku.leoz.central.data.jooq.tables.records.TblstatusRecord
import org.deku.leoz.central.data.jooq.tables.records.TblauftragcolliesRecord
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named
import javax.persistence.Persistence
import org.deku.leoz.central.data.jooq.tables.Tblauftragcollies
import org.deku.leoz.central.data.jooq.tables.Tblauftrag
import org.deku.leoz.central.data.jooq.Tables

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

    fun setSignaturePath(parcelNumber: String, path: String): Boolean {
        //update parcel
        //TblauftragcolliesRecord: bmpFileName = path
        return true
    }
    fun getUnitNo(parcelId: Int): Double? {
        if (parcelId==0) return null
        val parcel=parcelId.toString()
        val orderpos=parcel.substring(11,parcel.length-11)
        val orderid=parcel.substring(0,11)
        return dslContext.select(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR)
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(orderid.toDouble()))
                .and(Tables.TBLAUFTRAGCOLLIES.ORDERPOS.eq(orderpos.toShort()))
                .fetchOneInto(Double::class.java)
    }
}