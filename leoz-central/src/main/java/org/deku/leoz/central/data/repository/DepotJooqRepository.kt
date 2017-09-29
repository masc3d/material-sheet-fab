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
                // TODO: don't embed SQL. no implicit formatting. repository classes are expected to deliver data as is.
                //.select("lpad(convert(DEPOTNR using utf8),3,'0')").asField<String>("Depot")
                //.from(Tables.TBLDEPOTLISTE)
                .select(Tables.SECTIONDEPOTLIST.DEPOT)
                .from(Tables.SECTIONDEPOTLIST)
                .where(Tables.SECTIONDEPOTLIST.SECTION.eq(iSection.toLong())
                        .and(Tables.SECTIONDEPOTLIST.POSITION.eq(iPosition)))
                .fetchInto(String::class.java)
        /**.select(DEPOT)
        .from(Views.sectiondepotlist)
        .where(section.eq(iSection)
        .and(position.eq(iPosition)
        .fetchInto(String::class.java)
         */
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun findDebitor(StationNr: Long): Int {
        return dslContext
                .select(Tables.TBLDEPOTLISTE.DEBITOR_ID)
                .from(Tables.TBLDEPOTLISTE)
                .where(Tables.TBLDEPOTLISTE.DEPOTNR.eq(StationNr.toInt())
                ).fetchOne(Tables.TBLDEPOTLISTE.DEBITOR_ID)
    }
    fun findStationsByDebitorId(debitorId:Int):List<String>{
        return dslContext
                .select(Tables.TBLDEPOTLISTE.DEPOTNR)
                .from(Tables.TBLDEPOTLISTE)
                .where(Tables.TBLDEPOTLISTE.ID.eq(debitorId))
                .fetchInto(String::class.java)
    }
}
