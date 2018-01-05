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

    fun findEntriesByIds(ids: Collection<Int>): List<TadTourEntryRecord> {
        return dsl.selectFrom(Tables.TAD_TOUR_ENTRY)
                .where(Tables.TAD_TOUR_ENTRY.TOUR_ID.`in`(ids))
                .toList()
    }

    fun findEntriesById(id: Int): List<TadTourEntryRecord> {
        return dsl.selectFrom(Tables.TAD_TOUR_ENTRY)
                .where(Tables.TAD_TOUR_ENTRY.TOUR_ID.eq(id))
                .toList()
    }
}

/**
 * Fetch tour entries, correctly sorted by position/id
 * @param tourId Tour id
 */
fun SelectWhereStep<TadTourEntryRecord>.fetchByTourId(tourId: Int): List<TadTourEntryRecord> {
    // Fetch tour entries. Equal positions represent stop tasks in order of PK
    return this
            .where(Tables.TAD_TOUR_ENTRY.TOUR_ID.eq(tourId))
            .orderBy(Tables.TAD_TOUR_ENTRY.POSITION, Tables.TAD_TOUR_ENTRY.ID)
            .fetch()
}
