package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.tables.records.MstKeyRecord
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.MstKey
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named
import org.springframework.beans.factory.annotation.Qualifier


/**
 * Created by JT on 17.05.17.
 */


@Named
class KeyJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    lateinit var dslContext: DSLContext

    fun findByID(ID: Int): MstKeyRecord? {
        if (ID == 0)
            return null
        else
            return dslContext.fetchOne(MstKey.MST_KEY, Tables.MST_KEY.KEY_ID.eq(ID))
    }

    fun findValidByKey(Key: String): Boolean {
        return (dslContext.fetchOne(MstKey.MST_KEY, Tables.MST_KEY.KEY.eq(Key)) != null)
    }

    fun insertNew(Key: String): Int {

        val r = dslContext.newRecord(Tables.MST_KEY)
        r.key = Key
        r.store()

        return dslContext.lastID().toInt()
    }
}