package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TadNodeGeopositionRecord
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject

/**
 * Created by helke on 29.05.17.
 */
@Component
class JooqGeopositionRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    lateinit var dsl: DSLContext

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun findByUserId(id: Int, from: Date, to: Date): List<TadNodeGeopositionRecord>? {

        return dsl
                .select()
                .from(Tables.TAD_NODE_GEOPOSITION)
                .where(Tables.TAD_NODE_GEOPOSITION.USER_ID.eq(id))
                .and(Tables.TAD_NODE_GEOPOSITION.POSITION_DATETIME.between(from.toTimestamp(), to.toTimestamp()))
                .orderBy((Tables.TAD_NODE_GEOPOSITION.POSITION_DATETIME))
                .fetchInto(TadNodeGeopositionRecord::class.java)
    }

    fun findRecentByUserId(id: Int): List<TadNodeGeopositionRecord>? {

        return dsl
                .select()
                .from(Tables.TAD_NODE_GEOPOSITION)
                .where(Tables.TAD_NODE_GEOPOSITION.USER_ID.eq(id))
                //.and(Tables.TRN_NODE_GEOPOSITION.POSITION_DATETIME.between(from.toTimestamp(), to.toTimestamp()))
                .orderBy((Tables.TAD_NODE_GEOPOSITION.POSITION_DATETIME.desc()))
                .limit(1)
                .fetchInto(TadNodeGeopositionRecord::class.java)
    }

    fun save(geopositionRecord: TadNodeGeopositionRecord): Boolean {
        try {
            return (geopositionRecord.store() > 0)
        } catch (e: Exception) {
            log.error(e.toString())
            return false
        }
    }

}

