package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.mobile.Tables
import org.deku.leoz.central.data.jooq.mobile.tables.records.TadTourEntryRecord
import org.deku.leoz.central.data.jooq.mobile.tables.records.TadTourRecord
import org.jooq.DSLContext
import org.jooq.SelectWhereStep
import org.springframework.beans.factory.annotation.Qualifier
import javax.inject.Inject
import javax.inject.Named

@Named
class JooqTourRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    fun findById(id: Int): TadTourRecord? =
            dsl.fetchOne(Tables.TAD_TOUR, Tables.TAD_TOUR.ID.eq(id))

    fun findByNodeId(nodeId: Int): TadTourRecord? =
            dsl.fetchOne(Tables.TAD_TOUR, Tables.TAD_TOUR.NODE_ID.eq(nodeId))

    fun findLatestByUserId(userId: Int): TadTourRecord? {
        return dsl.selectFrom(Tables.TAD_TOUR)
                .where(Tables.TAD_TOUR.USER_ID.eq(userId))
                .orderBy(Tables.TAD_TOUR.TIMESTAMP.desc())
                .fetchAny()
    }

    /**
     * Find tour entries
     * @param Tour ids
     * @return Tour entries in positional order
     */
    fun findEntriesByIds(ids: Collection<Int>): List<TadTourEntryRecord> {
        return dsl.selectFrom(Tables.TAD_TOUR_ENTRY)
                .where(Tables.TAD_TOUR_ENTRY.TOUR_ID.`in`(ids))
                .orderBy(Tables.TAD_TOUR_ENTRY.POSITION, Tables.TAD_TOUR_ENTRY.ID)
                .toList()
    }

    /**
     * Find tour entries
     * @param Tour id
     * @return Tour entries in positional order
     */
    fun findEntriesById(id: Int): List<TadTourEntryRecord> {
        return dsl.selectFrom(Tables.TAD_TOUR_ENTRY)
                .where(Tables.TAD_TOUR_ENTRY.TOUR_ID.eq(id))
                .orderBy(Tables.TAD_TOUR_ENTRY.POSITION, Tables.TAD_TOUR_ENTRY.ID)
                .toList()
    }
}
