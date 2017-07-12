package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistDetailsRecord
import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistRecord
import org.deku.leoz.central.data.jooq.tables.records.TadVOrderRecord
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import javax.inject.Inject

/**
 * Created by JT on 12.07.17.
 */
open class DeliveryListJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    fun findById(id: Long): TadVDeliverylistRecord? {
        return dslContext.fetchOne(
                Tables.TAD_V_DELIVERYLIST,
                Tables.TAD_V_DELIVERYLIST.ID.eq(id.toDouble()))
    }

    fun findDetailsById(id: Long): List<TadVDeliverylistDetailsRecord> {
        return dslContext.fetch(
                Tables.TAD_V_DELIVERYLIST_DETAILS,
                Tables.TAD_V_DELIVERYLIST_DETAILS.ID.eq(id.toDouble()))
    }
}