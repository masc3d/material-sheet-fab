package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables.TAD_V_DELIVERYLIST
import org.deku.leoz.central.data.jooq.dekuclient.Tables.TAD_V_DELIVERYLIST_DETAILS
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TadVDeliverylistDetailsRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TadVDeliverylistRecord
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by JT on 12.07.17.
 */
@Named
open class JooqDeliveryListRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    fun findById(id: Long): TadVDeliverylistRecord? {
        return dsl.fetchOne(
                TAD_V_DELIVERYLIST,
                TAD_V_DELIVERYLIST.ID.eq(id.toDouble()))
    }
    
    fun findByStationId(id: Long): List<TadVDeliverylistRecord> {
        return dsl.fetch(
                TAD_V_DELIVERYLIST,
                TAD_V_DELIVERYLIST.DELIVERY_STATION.eq(id.toDouble()))
    }

    fun findInfoByDate(deliveryDate: Date): List<TadVDeliverylistRecord> {
        return dsl.fetch(
                TAD_V_DELIVERYLIST,
                TAD_V_DELIVERYLIST.DELIVERY_LIST_DATE.equal(deliveryDate.toTimestamp()))
    }

    fun findInfoByDebitor(debitorId: Int): List<TadVDeliverylistRecord> {
        return dsl.fetch(
                TAD_V_DELIVERYLIST,
                TAD_V_DELIVERYLIST.DEBITOR_ID.eq(debitorId.toLong())
        )
    }

    fun findInfoByDateDebitorList(deliveryDate: Date, debitorId: Int): List<TadVDeliverylistRecord> {
        return dsl.fetch(
                TAD_V_DELIVERYLIST,
                TAD_V_DELIVERYLIST.DELIVERY_LIST_DATE.equal(deliveryDate.toTimestamp())
                        .and(TAD_V_DELIVERYLIST.DEBITOR_ID.eq(debitorId.toLong()))
        )
    }

    fun findDetailsById(id: Long): List<TadVDeliverylistDetailsRecord> {
        return dsl.fetch(
                TAD_V_DELIVERYLIST_DETAILS,
                TAD_V_DELIVERYLIST_DETAILS.ID.eq(id.toDouble()))
    }
}