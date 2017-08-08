package org.deku.leoz.central.data.repository

import com.thoughtworks.xstream.converters.extended.SqlDateConverter
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistDetailsRecord
import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistRecord
import org.deku.leoz.central.data.jooq.tables.records.TadVDeliverylistinfoRecord
import org.deku.leoz.service.entity.ShortDate
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import sx.time.toTimestamp
import java.sql.Timestamp
import java.util.*
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by JT on 12.07.17.
 */
@Named
open class DeliveryListJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    fun findById(id: Long): TadVDeliverylistRecord? {
        return dslContext.fetchOne(
                Tables.TAD_V_DELIVERYLIST,
                Tables.TAD_V_DELIVERYLIST.ID.eq(id.toDouble()))
    }

    fun findInfoByDate(deliveryDate: Date): List<TadVDeliverylistinfoRecord> {
        return dslContext.fetch(
                Tables.TAD_V_DELIVERYLISTINFO,
                Tables.TAD_V_DELIVERYLISTINFO.DELIVERY_LIST_DATE.equal(deliveryDate.toTimestamp()))
    }

    fun findDetailsById(id: Long): List<TadVDeliverylistDetailsRecord> {
        return dslContext.fetch(
                Tables.TAD_V_DELIVERYLIST_DETAILS,
                Tables.TAD_V_DELIVERYLIST_DETAILS.ID.eq(id.toDouble())).sortAsc(Tables.TAD_V_DELIVERYLIST_DETAILS.ORDER_POSITION)
    }
}