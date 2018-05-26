package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.MstKey
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.MstKeyRecord
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import sx.util.toByteArray
import sx.util.toUUID
import java.util.*
import javax.inject.Inject


/**
 * Created by JT on 17.05.17.
 */
@Component
class JooqKeyRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    lateinit var dsl: DSLContext

    fun findByUid(uid: UUID): MstKeyRecord? {
        return dsl.fetchOne(MstKey.MST_KEY, Tables.MST_KEY.UID.eq(uid.toByteArray()))
    }

    fun findValidByKey(key: String): Boolean {
        return (dsl.fetchOne(
                MstKey.MST_KEY,
                Tables.MST_KEY.KEY.eq(key)
        ) != null)
    }

    fun insertNew(key: String): UUID {
        val r = dsl.newRecord(Tables.MST_KEY)
        r.key = key
        r.store()
        r.refresh()

        return r.uid.toUUID()
    }
}