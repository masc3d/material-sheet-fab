package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.MstKeyRecord
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.MstKey
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named
import org.springframework.beans.factory.annotation.Qualifier


/**
 * Created by JT on 17.05.17.
 */
@Named
class JooqKeyRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    lateinit var dslContext: DSLContext

    fun findByID(id: Int): MstKeyRecord? {
        if (id == 0)
            return null
        else
            return dslContext.fetchOne(MstKey.MST_KEY, Tables.MST_KEY.KEY_ID.eq(id))
    }

    fun findValidByKey(key: String): Boolean {
        return (dslContext.fetchOne(
                MstKey.MST_KEY,
                Tables.MST_KEY.KEY.eq(key)
        ) != null)
    }

    fun insertNew(key: String): Int {
        val r = dslContext.newRecord(Tables.MST_KEY)
        r.key = key
        r.store()

        return dslContext.lastID().toInt()
    }
}