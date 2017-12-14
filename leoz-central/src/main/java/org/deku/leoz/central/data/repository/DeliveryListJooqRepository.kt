package org.deku.leoz.central.data.repository

import com.thoughtworks.xstream.converters.extended.SqlDateConverter
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TadVDeliverylistDetailsRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TadVDeliverylistRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TadVDeliverylistinfoRecord
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

    //falsch debitor fehlt
    fun findById(id: Long): TadVDeliverylistRecord? {
        return dslContext.fetchOne(
                Tables.TAD_V_DELIVERYLIST,
                Tables.TAD_V_DELIVERYLIST.ID.eq(id.toDouble()))
    }

    fun findInfoByDate(deliveryDate: Date): List<TadVDeliverylistRecord> {
        return dslContext.fetch(
                Tables.TAD_V_DELIVERYLIST,
                Tables.TAD_V_DELIVERYLIST.DELIVERY_LIST_DATE.equal(deliveryDate.toTimestamp()))
    }

    fun findInfoByDebitor(debitorId: Int): List<TadVDeliverylistRecord> {
        return dslContext.fetch(
                Tables.TAD_V_DELIVERYLIST,
                Tables.TAD_V_DELIVERYLIST.DEBITOR_ID.eq(debitorId.toLong())
        )
    }

    fun findInfoByDateDebitorList(deliveryDate: Date, debitorId: Int): List<TadVDeliverylistRecord> {
        return dslContext.fetch(
                Tables.TAD_V_DELIVERYLIST,
                Tables.TAD_V_DELIVERYLIST.DELIVERY_LIST_DATE.equal(deliveryDate.toTimestamp())
                        .and(Tables.TAD_V_DELIVERYLIST.DEBITOR_ID.eq(debitorId.toLong()))
        )
    }

    fun findDetailsById(id: Long): List<TadVDeliverylistDetailsRecord> {
        return dslContext.fetch(
                Tables.TAD_V_DELIVERYLIST_DETAILS,
                Tables.TAD_V_DELIVERYLIST_DETAILS.ID.eq(id.toDouble()))
    }
}