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

    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun add(logEntry:TblhistorieRecord):Int{
        return dslContext.insertInto(Tables.TBLHISTORIE).set(logEntry).execute()
    }
}