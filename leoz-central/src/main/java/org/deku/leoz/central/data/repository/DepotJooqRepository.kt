package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.SsoPMov
import org.deku.leoz.central.data.jooq.tables.SsoSMov
import org.deku.leoz.central.data.jooq.tables.SsoSMovepool
import org.deku.leoz.central.data.jooq.tables.records.SsoPMovRecord
import org.deku.leoz.central.data.jooq.tables.records.SsoSMovepoolRecord
import org.deku.leoz.central.data.jooq.tables.records.TbldepotlisteRecord
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
                .where(Tables.MST_DEBITOR_STATION.STATION_ID.eq(StationNr)
//todo include send_date  between active_from and active_to
                ).fetchOne(Tables.MST_DEBITOR_STATION.DEBITOR_ID)
    }

    open fun findStationsByDebitorId(debitorId: Int): List<String> {
        return dslContext
                .select(Tables.TBLDEPOTLISTE.DEPOTNR)
                .from(Tables.TBLDEPOTLISTE)
                .where(Tables.TBLDEPOTLISTE.ID.eq(debitorId))
//todo include send_date  between active_from and active_to
                .fetchInto(String::class.java)
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun findByMatchcode(matchcode: String): TbldepotlisteRecord {
        return dslContext
                .select()
                .from(Tables.TBLDEPOTLISTE)
                .where(Tables.TBLDEPOTLISTE.DEPOTMATCHCODE.eq(matchcode))
                .fetchOneInto(TbldepotlisteRecord::class.java)
    }

    open fun getCountBags2SendBagByStation(stationNo: Int): Int {
        val countBagsUsedByStation: Int = dslContext.selectCount()
                .from(Tables.SSO_S_MOVEPOOL)
                .where(Tables.SSO_S_MOVEPOOL.LASTDEPOT.eq(stationNo.toDouble())
                        .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                        .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(6.0)))
                .fetchOne(0, Int::class.java)

        val quota: Int = dslContext.select(Tables.TBLDEPOTLISTE.BAGKONTINGENT)
                .from(Tables.TBLDEPOTLISTE)
                .where(Tables.TBLDEPOTLISTE.DEPOTNR.eq(stationNo)
                        .and(Tables.TBLDEPOTLISTE.AKTIVIERUNGSDATUM.greaterOrEqual(Date().toTimestamp()))
                        .and(Tables.TBLDEPOTLISTE.DEAKTIVIERUNGSDATUM.lessOrEqual(Date().toTimestamp()))
                )
                .fetchOne(0, Int::class.java) ?: 0

        var diff = countBagsUsedByStation - quota
        if (diff < 0)
            diff = 0
        return diff
    }

    open fun getBag(bagId: Long): SsoSMovepoolRecord? {
//        val record = dslContext.select()
//                .from(Tables.SSO_S_MOVEPOOL)
//                .where(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagId.toDouble()))
//                .fetchOne(record: SsoSMovepoolRecord::class.java)
        return dslContext.fetchOne(SsoSMovepool.SSO_S_MOVEPOOL, Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagId.toDouble()))
        //return record
    }

    open fun getUnitNo(orderid: Long): Long? {
        if (orderid == 0.toLong()) return null
        return dslContext.select(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR).from(Tables.TBLAUFTRAGCOLLIES)
                .where(Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(orderid.toDouble()))
                .fetchOne(0, Long::class.java)
    }

    open fun getSeal(sealNo:Long):SsoPMovRecord?{
        return dslContext.fetchOne(SsoPMov.SSO_P_MOV,Tables.SSO_P_MOV.PLOMBENNUMMER.eq(sealNo.toDouble()))
    }
}

fun SsoSMovepoolRecord.toBag(): ExportService.Bag {
    val bag = ExportService.Bag(
            this.bagNumber.toLong(),
            this.sealNumberGreen?.toLong(),
            //this.status?.toInt(),
            BagStatus.values().find { it.value == this.status?.toInt() },
            this.statusTime,
            this.lastdepot?.toInt(),
            this.sealNumberYellow?.toLong(),
            this.sealNumberRed?.toLong(),
            this.orderhub2depot?.toLong(),
            this.orderdepot2hub?.toLong(),
            this.initStatus,
            this.workDate,
            this.printed?.toInt(),
            this.multibag.toInt(),
            this.movepool
    )
    return bag
}

fun SsoSMovepoolRecord.toGeneralBag(): BagService.Bag {
    val bag = BagService.Bag(
            this.bagNumber.toLong(),
            this.sealNumberGreen?.toLong(),
            //this.status?.toInt(),
            BagStatus.values().find { it.value == this.status?.toInt() },
            this.statusTime,
            this.lastdepot?.toLong(),
            this.sealNumberYellow?.toLong(),
            this.sealNumberRed?.toLong(),
            this.orderhub2depot?.toLong(),
            this.orderdepot2hub?.toLong(),
            this.initStatus,
            this.workDate,
            this.printed?.toInt(),
            this.multibag.toInt(),
            this.movepool
    )
    return bag
}
//fun SsoSMovepoolRecord.toBag(): BagService.Bag{
//    val bag=BagService.Bag(
//            this.bagNumber.toLong(),
//            this.sealNumberGreen?.toLong(),
//            BagStatus.values().find{it.value==this.status?.toInt()},
//            this.statusTime,
//            this.lastdepot?.toLong(),
//            this.sealNumberYellow?.toLong(),
//            this.sealNumberRed?.toLong(),
//            depotRepository.getUnitNo(this.orderhub2depot?.toLong()),
//            //this.orderhub2depot?.toLong(),
//            depotRepository.getUnitNo(this.orderdepot2hub?.toLong()),
//            //this.orderdepot2hub?.toLong(),
//            this.workDate
//    )
//    return bag
//}
