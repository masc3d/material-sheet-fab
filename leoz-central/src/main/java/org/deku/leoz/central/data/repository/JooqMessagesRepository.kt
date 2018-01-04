package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.springframework.beans.factory.annotation.Qualifier
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TadParcelMessagesRecord

@Named
class JooqMessagesRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)

    lateinit var dslContext: DSLContext

    fun findUnprocessedMsg(): List<TadParcelMessagesRecord>{
        return dslContext.select()
                .from(Tables.TAD_PARCEL_MESSAGES)
                .where(Tables.TAD_PARCEL_MESSAGES.IS_PROCCESSED.isFalse)
                .fetchInto(TadParcelMessagesRecord::class.java)
    }
}