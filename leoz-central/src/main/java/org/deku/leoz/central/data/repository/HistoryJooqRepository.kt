package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TblhistorieRecord
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject
import javax.inject.Named

/**
 * Created by helke on 24.03.17.
 */
@Named
open class HistoryJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    //@Inject
    //private lateinit var historyRepository: HistoryJooqRepository

    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun save(logEntry: TblhistorieRecord) {
        dslContext.insertInto(Tables.TBLHISTORIE).set(logEntry).execute()
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun save(depotId: String, info: String, msgLocation: String, orderId: String) {
        val record = dslContext.newRecord(Tables.TBLHISTORIE)
        record.depotid = if(depotId.length>10)depotId.substring(0,10) else depotId
        record.info = info
        record.msglocation = if(msgLocation.length>45) msgLocation.substring(0,45) else msgLocation
        record.orderid = if(orderId.length>20) orderId.substring(0,20) else orderId
        record.store()
        //historyRepository.save(record)
        //this.save(record)
    }
}