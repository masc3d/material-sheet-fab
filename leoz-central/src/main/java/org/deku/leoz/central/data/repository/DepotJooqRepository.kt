package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.TbldepotlisteRecord
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject
import javax.inject.Named

/**
 * Created by masc on 07.05.15.
 */
@Named
open class DepotJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun findAll(): List<TbldepotlisteRecord> {
        return dslContext
                .select()
                .from(Tables.TBLDEPOTLISTE)
                .fetchInto(TbldepotlisteRecord::class.java)
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun findSectionDepots(iSection: Int, iPosition: Int): List<String> {
        return dslContext
                .select(Tables.SECTIONDEPOTLIST.DEPOT)
                .from(Tables.SECTIONDEPOTLIST)
                .where(Tables.SECTIONDEPOTLIST.SECTION.eq(iSection.toLong())
                        .and(Tables.SECTIONDEPOTLIST.POSITION.eq(iPosition)))
                .fetchInto(String::class.java)
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun findDebitor(StationNr: Int): Int {
        return dslContext
                .select(Tables.MST_DEBITOR.DEBITOR_ID)
                .from(Tables.MST_DEBITOR).innerJoin(Tables.MST_DEBITOR_STATION)
                .on(Tables.MST_DEBITOR.DEBITOR_ID.eq(Tables.MST_DEBITOR_STATION.DEBITOR_ID))
                .where(Tables.MST_DEBITOR_STATION.STATION_ID.eq(StationNr.toInt())
//todo include send_date  between active_from and active_to
                ).fetchOne(Tables.MST_DEBITOR_STATION.DEBITOR_ID)
    }

    fun findStationsByDebitorId(debitorId: Int): List<String> {
        return dslContext
                .select(Tables.TBLDEPOTLISTE.DEPOTNR)
                .from(Tables.TBLDEPOTLISTE)
                .where(Tables.TBLDEPOTLISTE.ID.eq(debitorId))
//todo include send_date  between active_from and active_to
                .fetchInto(String::class.java)
    }
}
