package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.mobile.Tables.TAD_TOUR
import org.deku.leoz.central.data.jooq.mobile.Tables.TAD_TOUR_ENTRY
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
            dsl.fetchOne(TAD_TOUR, TAD_TOUR.ID.eq(id))

    fun findByNodeId(nodeId: Int): TadTourRecord? =
            dsl.fetchOne(TAD_TOUR, TAD_TOUR.NODE_ID.eq(nodeId))

    fun findLatestByUserId(userId: Int): TadTourRecord? {
        return dsl.selectFrom(TAD_TOUR)
                .where(TAD_TOUR.USER_ID.eq(userId))
                .orderBy(TAD_TOUR.TIMESTAMP.desc())
                .fetchAny()
    }

    /**
     * Find tour entries
     * @param Tour ids
     * @return Tour entries in positional order
     */
    fun findEntriesByIds(ids: Collection<Int>): List<TadTourEntryRecord> {
        return dsl.selectFrom(TAD_TOUR_ENTRY)
                .where(TAD_TOUR_ENTRY.TOUR_ID.`in`(ids))
                .orderBy(TAD_TOUR_ENTRY.POSITION, TAD_TOUR_ENTRY.ID)
                .toList()
    }

    /**
     * Find tour entries
     * @param Tour id
     * @return Tour entries in positional order
     */
    fun findEntriesById(id: Int): List<TadTourEntryRecord> {
        return dsl.selectFrom(TAD_TOUR_ENTRY)
                .where(TAD_TOUR_ENTRY.TOUR_ID.eq(id))
                .orderBy(TAD_TOUR_ENTRY.POSITION, TAD_TOUR_ENTRY.ID)
                .toList()
    }

    /**
     * Delete tour(s)
     * @param ids Tour ids
     */
    fun delete(ids: List<Int>) {
        if (ids.count() > 0) {
            dsl.transaction { _ ->
                dsl.deleteFrom(TAD_TOUR_ENTRY)
                        .where(TAD_TOUR_ENTRY.TOUR_ID.`in`(ids))
                        .execute()

                dsl.deleteFrom(TAD_TOUR)
                        .where(TAD_TOUR.ID.`in`(ids))
                        .execute()
            }
        }
    }

    /**
     * Delete tour(s) by user
     * @param userId User id
     */
    fun deleteByUser(userId: Int) {
        this.delete(dsl.selectFrom(TAD_TOUR)
                .where(TAD_TOUR.USER_ID.eq(userId))
                .fetch(TAD_TOUR.ID))
    }

    /**
     * Delete tour(s) by station
     * @param stationNo Station no
     */
    fun deleteByStation(stationNo: Int) {
        this.delete(dsl.selectFrom(TAD_TOUR)
                .where(TAD_TOUR.STATION_NO.eq(stationNo))
                .fetch(TAD_TOUR.ID))
    }
}
