package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.Tables.*
import org.deku.leoz.central.data.jooq.dekuclient.tables.SsoPMov
import org.deku.leoz.central.data.jooq.dekuclient.tables.SsoSMovepool
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.SsoPMovRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.SsoSMovepoolRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TbldepotlisteRecord
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject

/**
 * Created by masc on 07.05.15.
 */
@Component
class JooqStationRepository {
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

    fun findByStationNo(stationNo: Int): TbldepotlisteRecord? =
            dsl.fetchOne(TBLDEPOTLISTE, TBLDEPOTLISTE.DEPOTNR.eq(stationNo))

    fun findByDebitorId(debitorId: Int): List<TbldepotlisteRecord> =
            dsl.fetch(TBLDEPOTLISTE, TBLDEPOTLISTE.DEBITOR_ID.eq(debitorId).and(TBLDEPOTLISTE.ISTGUELTIG.eq(1))).toList()

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
                .where(TBLDEPOTLISTE.DEBITOR_ID.eq(debitorId)
                        .and(TBLDEPOTLISTE.ISTGUELTIG.eq(1)))
//todo include send_date  between active_from and active_to
                .fetchInto(String::class.java)
    }

    fun findByMatchcode(matchcode: String): TbldepotlisteRecord {
        return dsl
                .select()
                .from(TBLDEPOTLISTE)
                .where(TBLDEPOTLISTE.DEPOTMATCHCODE.eq(matchcode)
                        .and(TBLDEPOTLISTE.ISTGUELTIG.eq(1))
                )
                .fetchOneInto(TbldepotlisteRecord::class.java)
    }

    fun countBagsToSendBagByStation(stationNo: Int): Int {
        val countBagsUsedByStation: Int = dsl.selectCount()
                .from(SSO_S_MOVEPOOL)
                .where(SSO_S_MOVEPOOL.LASTDEPOT.eq(stationNo.toDouble())
                        .and(SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                        .and(SSO_S_MOVEPOOL.STATUS.eq(6.0)))
                .fetchOne(0, Int::class.java)

        val quota: Int = dsl.select(TBLDEPOTLISTE.BAGKONTINGENT)
                .from(TBLDEPOTLISTE)
                .where(TBLDEPOTLISTE.DEPOTNR.eq(stationNo)
                        .and(TBLDEPOTLISTE.AKTIVIERUNGSDATUM.lessOrEqual(Date().toTimestamp()))
                        .and(TBLDEPOTLISTE.DEAKTIVIERUNGSDATUM.greaterOrEqual(Date().toTimestamp()))
                        .and(TBLDEPOTLISTE.ISTGUELTIG.eq(1))
                )
                .fetchOne(0, Int::class.java) ?: 0

        var diff = countBagsUsedByStation - quota
        if (diff < 0)
            diff = 0
        return diff
    }

    fun findBag(bagId: Long): SsoSMovepoolRecord? {
//        val record = dsl.select()
//                .from(SSO_S_MOVEPOOL)
//                .where(SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagId.toDouble()))
//                .fetchOne(record: SsoSMovepoolRecord::class.java)
        return dsl.fetchOne(SsoSMovepool.SSO_S_MOVEPOOL, SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagId.toDouble()))
        //return record
    }

    fun findBagByBagBackOrderId(bagBackOrderId:Long):SsoSMovepoolRecord? {
        return dsl.fetchOne(SsoSMovepool.SSO_S_MOVEPOOL, SSO_S_MOVEPOOL.ORDERDEPOT2HUB.eq(bagBackOrderId.toDouble()))
    }

    fun findUnitNo(orderid: Long): Long? {
        if (orderid == 0.toLong()) return null
        return dsl.select(TBLAUFTRAGCOLLIES.COLLIEBELEGNR).from(TBLAUFTRAGCOLLIES)
                .where(TBLAUFTRAGCOLLIES.ORDERID.eq(orderid.toDouble())
                        .and(Tables.TBLAUFTRAGCOLLIES.IS_CANCELLED.eq(0))
                )
                .fetchOne(0, Long::class.java)
    }

    fun findSeal(sealNo: Long): SsoPMovRecord? {
        return dsl.fetchOne(SsoPMov.SSO_P_MOV, SSO_P_MOV.PLOMBENNUMMER.eq(sealNo.toDouble()))
    }

    fun findStationTour(zip: String, stationNo: Int): Int {
        if (zip.trim() == "") return 99
        val tour = dsl.select(TBLROUTENDEPOT.TOURNR).from(TBLROUTENDEPOT)
                .where(TBLROUTENDEPOT.DEPOTID.eq(stationNo)
                        .and(TBLROUTENDEPOT.VONPLZ.le(zip))
                        .and(TBLROUTENDEPOT.BISPLZ.ge(zip))
                )
                .fetchOne(0, Int::class.java)
        return tour ?: 99
    }

    fun findAllowedStationsByUserId(userId: Int): List<Int> {
        return dsl.select(TBLDEPOTLISTE.DEPOTNR)
                .from(TBLDEPOTLISTE)
                .innerJoin(MST_STATION_USER).on(TBLDEPOTLISTE.ID.eq(MST_STATION_USER.STATION_ID))
                .where(MST_STATION_USER.USER_ID.eq(userId)).and(TBLDEPOTLISTE.ISTGUELTIG.eq(1))
                .fetch(TBLDEPOTLISTE.DEPOTNR)

    }
    fun findStationIdByDepotNr(depotNr:Int):Int?{
        return dsl.selectFrom(TBLDEPOTLISTE)
                .where(TBLDEPOTLISTE.ISTGUELTIG.eq(1))
                .and(TBLDEPOTLISTE.DEPOTNR.eq(depotNr))
                .fetchOne(TBLDEPOTLISTE.ID)
    }
}
