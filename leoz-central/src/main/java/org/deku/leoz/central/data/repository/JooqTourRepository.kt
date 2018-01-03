package org.deku.leoz.central.data.repository

import org.deku.leoz.central.data.jooq.mobile.Tables
import org.deku.leoz.central.data.jooq.mobile.tables.records.TadTourEntryRecord
import org.jooq.SelectWhereStep

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
