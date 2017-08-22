package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import javax.inject.Inject
import javax.inject.Named
import org.deku.leoz.central.data.jooq.tables.records.TadNodeGeopositionRecord
import sx.time.toTimestamp
import java.util.*

/**
 * Created by helke on 29.05.17.
 */
@Named
class PositionJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    lateinit var dslContext: DSLContext

    fun findByUserId(id: Int, from: Date, to: Date): List<TadNodeGeopositionRecord>? {

        return dslContext
                .select()
                .from(Tables.TAD_NODE_GEOPOSITION)
                .where(Tables.TAD_NODE_GEOPOSITION.USER_ID.eq(id))
                .and(Tables.TAD_NODE_GEOPOSITION.POSITION_DATETIME.between(from.toTimestamp(), to.toTimestamp()))
                .orderBy((Tables.TAD_NODE_GEOPOSITION.POSITION_DATETIME))
                .fetchInto(TadNodeGeopositionRecord::class.java)
    }

    fun findRecentByUserId(id: Int): List<TadNodeGeopositionRecord>? {

        return dslContext
                .select()
                .from(Tables.TAD_NODE_GEOPOSITION)
                .where(Tables.TAD_NODE_GEOPOSITION.USER_ID.eq(id))
                //.and(Tables.TRN_NODE_GEOPOSITION.POSITION_DATETIME.between(from.toTimestamp(), to.toTimestamp()))
                .orderBy((Tables.TAD_NODE_GEOPOSITION.POSITION_DATETIME.desc()))
                .limit(1)
                .fetchInto(TadNodeGeopositionRecord::class.java)
    }

    fun save(geopositionRecord: TadNodeGeopositionRecord): Boolean {
        return (geopositionRecord.store() > 0)
    }

}

