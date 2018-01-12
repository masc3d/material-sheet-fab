package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.Tables.*
import org.deku.leoz.central.data.jooq.dekuclient.tables.SsoPMov
import org.deku.leoz.central.data.jooq.dekuclient.tables.SsoSMov
import org.deku.leoz.central.data.jooq.dekuclient.tables.SsoSMovepool
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.SsoPMovRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.SsoSMovepoolRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TbldepotlisteRecord
import org.deku.leoz.model.BagStatus
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional
import sx.time.toTimestamp
import java.util.*

import javax.inject.Inject
import javax.inject.Named

import org.deku.leoz.service.internal.BagService
import org.deku.leoz.service.internal.ExportService

/**
 * Created by masc on 07.05.15.
 */
@Named
open class JooqStationRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    fun findAll(): List<TbldepotlisteRecord> {
        return dsl
                .select()
                .from(TBLDEPOTLISTE)
                .fetchInto(TbldepotlisteRecord::class.java)
    }

    fun findById(id: Int): TbldepotlisteRecord? =
            dsl.fetchOne(TBLDEPOTLISTE, TBLDEPOTLISTE.ID.eq(id))

    fun findByDebitorId(debitorId: Int): List<TbldepotlisteRecord> =
            dsl.fetch(TBLDEPOTLISTE, TBLDEPOTLISTE.DEBITOR_ID.eq(debitorId)).toList()

    fun findSectionDepots(iSection: Int, iPosition: Int): List<String> {
        return dsl
                .select(SECTIONDEPOTLIST.DEPOT)
                .from(SECTIONDEPOTLIST)
                .where(SECTIONDEPOTLIST.SECTION.eq(iSection.toLong())
                        .and(SECTIONDEPOTLIST.POSITION.eq(iPosition)))
                .fetchInto(String::class.java)
    }

    fun findDebitor(StationNr: Int): Int {
        return dsl
                .select(MST_DEBITOR.DEBITOR_ID)
                .from(MST_DEBITOR).innerJoin(MST_DEBITOR_STATION)
                .on(MST_DEBITOR.DEBITOR_ID.eq(MST_DEBITOR_STATION.DEBITOR_ID))
                .where(MST_DEBITOR_STATION.STATION_ID.eq(StationNr)
//todo include send_date  between active_from and active_to
                ).fetchOne(MST_DEBITOR_STATION.DEBITOR_ID)
    }

    fun findStationsByDebitorId(debitorId: Int): List<String> {
        return dsl
                .select(TBLDEPOTLISTE.DEPOTNR)
                .from(TBLDEPOTLISTE)
                .where(TBLDEPOTLISTE.ID.eq(debitorId))
//todo include send_date  between active_from and active_to
                .fetchInto(String::class.java)
    }

    fun findByMatchcode(matchcode: String): TbldepotlisteRecord {
        return dsl
                .select()
                .from(TBLDEPOTLISTE)
                .where(TBLDEPOTLISTE.DEPOTMATCHCODE.eq(matchcode))
                .fetchOneInto(TbldepotlisteRecord::class.java)
    }

    fun getCountBagsToSendBagByStation(stationNo: Int): Int {
        val countBagsUsedByStation: Int = dsl.selectCount()
                .from(SSO_S_MOVEPOOL)
                .where(SSO_S_MOVEPOOL.LASTDEPOT.eq(stationNo.toDouble())
                        .and(SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                        .and(SSO_S_MOVEPOOL.STATUS.eq(6.0)))
                .fetchOne(0, Int::class.java)

        val quota: Int = dsl.select(TBLDEPOTLISTE.BAGKONTINGENT)
                .from(TBLDEPOTLISTE)
                .where(TBLDEPOTLISTE.DEPOTNR.eq(stationNo)
                        .and(TBLDEPOTLISTE.AKTIVIERUNGSDATUM.greaterOrEqual(Date().toTimestamp()))
                        .and(TBLDEPOTLISTE.DEAKTIVIERUNGSDATUM.lessOrEqual(Date().toTimestamp()))
                )
                .fetchOne(0, Int::class.java) ?: 0

        var diff = countBagsUsedByStation - quota
        if (diff < 0)
            diff = 0
        return diff
    }

    fun getBag(bagId: Long): SsoSMovepoolRecord? {
//        val record = dsl.select()
//                .from(SSO_S_MOVEPOOL)
//                .where(SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagId.toDouble()))
//                .fetchOne(record: SsoSMovepoolRecord::class.java)
        return dsl.fetchOne(SsoSMovepool.SSO_S_MOVEPOOL, SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagId.toDouble()))
        //return record
    }

    fun getUnitNo(orderid: Long): Long? {
        if (orderid == 0.toLong()) return null
        return dsl.select(TBLAUFTRAGCOLLIES.COLLIEBELEGNR).from(TBLAUFTRAGCOLLIES)
                .where(TBLAUFTRAGCOLLIES.ORDERID.eq(orderid.toDouble()))
                .fetchOne(0, Long::class.java)
    }

    fun getSeal(sealNo: Long): SsoPMovRecord? {
        return dsl.fetchOne(SsoPMov.SSO_P_MOV, SSO_P_MOV.PLOMBENNUMMER.eq(sealNo.toDouble()))
    }
}
