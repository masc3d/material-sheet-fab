package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TadParcelMessagesRecord
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import javax.inject.Inject

@Component
class JooqMessagesRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)

    lateinit var dsl: DSLContext

    fun findUnprocessedMsg(): List<TadParcelMessagesRecord>{
        return dsl.select()
                .from(Tables.TAD_PARCEL_MESSAGES)
                .where(Tables.TAD_PARCEL_MESSAGES.IS_PROCCESSED.isFalse)
                .fetchInto(TadParcelMessagesRecord::class.java)
    }
}