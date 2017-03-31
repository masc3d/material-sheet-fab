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
/**
    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun findSectionDepots(iSection:Int,iPosition:Int): List<String> {
        return dslContext
                .select("lpad(convert(DEPOTNR using utf8),3,'0')").asField<String>("Depot")
                .from(Tables.TBLDEPOTLISTE)
                .fetchInto(String::class.java)
    }
    **
}
