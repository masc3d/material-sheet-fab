package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.TblhistorieRecord
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

    @Inject
    private lateinit var historyRepository: HistoryJooqRepository

    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun add(logEntry:TblhistorieRecord):Int{
        return dslContext.insertInto(Tables.TBLHISTORIE).set(logEntry).execute()
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun add(depotId:String,info:String,msgLocation:String,orderId:String):Int{
        val record = dslContext.newRecord(Tables.TBLHISTORIE)
        record.depotid=depotId
        record.info=info
        record.msglocation=msgLocation
        record.orderid=orderId
        historyRepository.add(record)
    }
}