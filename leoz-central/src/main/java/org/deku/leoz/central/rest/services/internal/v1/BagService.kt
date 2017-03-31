package org.deku.leoz.central.rest.services.internal.v1

import sx.packager.BundleRepository
import org.deku.leoz.central.Application
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.SsoSMovepoolRecord
import org.deku.leoz.service.update.UpdateInfo
import org.deku.leoz.service.update.UpdateInfoRequest
import org.deku.leoz.central.data.repository.NodeJooqRepository
import org.deku.leoz.node.data.jpa.QMstBundleVersion
import org.deku.leoz.node.data.repository.master.BundleVersionRepository
import org.deku.leoz.node.rest.ServiceException
import org.deku.leoz.node.rest.service.internal.v1.BundleService
import org.deku.leoz.rest.entity.internal.v1.BagInitRequest
import org.deku.leoz.rest.service.internal.v1.BagService
import org.joda.time.Hours
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import sx.rs.ApiKey
import sx.time.toDate
import sx.time.toTimestamp
import java.util.*
//import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.BadRequestException
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import org.deku.leoz.util.*
import sx.time.toLocalDate
import java.sql.Date
import org.deku.leoz.central.data.repository.HistoryJooqRepository
import org.deku.leoz.central.data.jooq.tables.records.TblhistorieRecord

import org.deku.leoz.rest.entity.internal.v1.BagFreeRequest
import org.jooq.Result

/**
 * Bundle service (leoz-central)
 * Created by masc on 01/11/2016.
 **/
@Named
@ApiKey(false)
@Path("internal/v1/bag")
class BagService : BagService {

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    @Inject
    private lateinit var logHistoryRepository: HistoryJooqRepository

    override fun get(id: String): String {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return "String Bag:" + id
    }

    override fun initialize(bagInitRequest: BagInitRequest): Boolean {
        if (bagInitRequest.bagId.isNullOrEmpty()) {
            throw ServiceException(BagService.ErrorCode.BAG_ID_MISSING)
        }

        if (bagInitRequest.whiteSeal.isNullOrEmpty()) {
            throw ServiceException(BagService.ErrorCode.WHITE_SEAL_MISSING)
        }
        if (bagInitRequest.yellowSeal.isNullOrEmpty()) {
            throw ServiceException(BagService.ErrorCode.YELLOW_SEAL_MISSING)
        }
        if (bagInitRequest.depotNr == null) {
            throw ServiceException(BagService.ErrorCode.DEPOT_NR_MISSING)
        }

        if (bagInitRequest.bagId!!.length < 12) {
            throw ServiceException(BagService.ErrorCode.BAG_ID_MISSING_CHECK_DIGIT)
        }

        if (bagInitRequest.whiteSeal!!.length < 12) {
            throw ServiceException(BagService.ErrorCode.WHITE_SEAL_MISSING_CHECK_DIGIT)
        }
        if (bagInitRequest.yellowSeal!!.length < 12) {
            throw ServiceException(BagService.ErrorCode.YELLOW_SEAL_MISSING_CHECK_DIGIT)
        }
        if (bagInitRequest.depotNr!! <= 0 || bagInitRequest.depotNr!! > 999) {
            throw ServiceException(BagService.ErrorCode.DEPOT_NR_NOT_VALID)
        }

        if (!checkCheckDigit(bagInitRequest.bagId!!)) {
            throw ServiceException(BagService.ErrorCode.BAG_ID_WRONG_CHECK_DIGIT)
        }

        if (!checkCheckDigit(bagInitRequest.whiteSeal!!)) {
            throw ServiceException(BagService.ErrorCode.WHITE_SEAL_WRONG_CHECK_DIGIT)
        }
        if (!checkCheckDigit(bagInitRequest.yellowSeal!!)) {
            throw ServiceException(BagService.ErrorCode.YELLOW_SEAL_WRONG_CHECK_DIGIT)
        }
        //TODO

        //var recHistory = TblhistorieRecord()
        try {
            var dtWork: LocalDate = getWorkingDate()
            var result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN)
                    .where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1))
                    .and(Tables.TBLHUBLINIENPLAN.ARBEITSDATUM.equal(dtWork.toTimestamp())).fetch()
            //val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN).where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1)).fetch()
            if (result.getValue(0, 0) == 0) {
                //dtWork=nextWerktag(dtWork.addDays(-1),"100","DE","36285")
            } else {
                //nach Feierabend und Tagesabschluss schon die bags für den nächsten Tag initialisieren oder am Wochenende
                //dtWork=nextwerktag(dtWork,"100","DE","36285"
                dtWork = getNextDeliveryDate()
            }
            val dblStatus: Double = 5.0
            //val dt:java.util.Date=dtWork.toDate()
            val dt: java.sql.Date = java.sql.Date.valueOf(dtWork);

            /**
            result=dslContext.selectCount().from(Tables.SSO_S_MOVEPOOL).where(Tables.SSO_S_MOVEPOOL.LASTDEPOT.eq(bagInitRequest.depotNr!!.toDouble())).and(Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus)).and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m")).and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt)).fetch()
            if(result.getValue(0,0)!=0){
            throw ServiceException(BagService.ErrorCode.BAG_FOR_DEPOT_ALREADY_EXISTS)
            }
             **/

            var iResultCount: Int = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,
                    Tables.SSO_S_MOVEPOOL.LASTDEPOT.eq(bagInitRequest.depotNr!!.toDouble())
                            .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus))
                            .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt)))
            if (iResultCount > 0) {
                throw ServiceException(BagService.ErrorCode.BAG_FOR_DEPOT_ALREADY_EXISTS)
            }
//status_time wieder raus, timestamp on update
            val dblBagID: Double = bagInitRequest.bagId!!.substring(0, 11).toDouble()
            val dblWhiteSeal: Double = bagInitRequest.whiteSeal!!.substring(0, 11).toDouble()
            val dblYellowSeal: Double = bagInitRequest.yellowSeal!!.substring(0, 11).toDouble()
            val dblNull: Double? = null

            val sBagID: String = bagInitRequest.bagId!!.substring(0, 11)
            val sYellowSeal: String = bagInitRequest.yellowSeal!!.substring(0, 11)
            val sWhiteSeal: String = bagInitRequest.whiteSeal!!.substring(0, 11)



            iResultCount = dslContext.update(Tables.SSO_S_MOVEPOOL)
                    .set(Tables.SSO_S_MOVEPOOL.ORDERDEPOT2HUB, dblNull)
                    .set(Tables.SSO_S_MOVEPOOL.ORDERHUB2DEPOT, dblNull)
                    .set(Tables.SSO_S_MOVEPOOL.SEAL_NUMBER_GREEN, dblWhiteSeal)
                    .set(Tables.SSO_S_MOVEPOOL.SEAL_NUMBER_YELLOW, dblYellowSeal)
                    .set(Tables.SSO_S_MOVEPOOL.STATUS, dblStatus)
                    .set(Tables.SSO_S_MOVEPOOL.INIT_STATUS, 1)
                    .set(Tables.SSO_S_MOVEPOOL.LASTDEPOT, bagInitRequest.depotNr!!.toDouble())
                    .set(Tables.SSO_S_MOVEPOOL.WORK_DATE, dt)
                    .set(Tables.SSO_S_MOVEPOOL.MOVEPOOL, "m")
                    .where(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID)).execute()
            if (iResultCount == 0) {
                /**
                recHistory.depotid = "initBag"//bagInitRequest.depotNr!!.toString()
                recHistory.info = "BagID: " + sBagID + "; YellowSeal: " + sYellowSeal + "; WhiteSeal: " + sWhiteSeal
                recHistory.msglocation = "initBag"
                recHistory.orderid = bagInitRequest.depotNr!!.toString()
                logHistoryRepository.add(recHistory)
                 **/
                logHistoryRepository.add("initBag"
                        , "BagID: " + sBagID + "; YellowSeal: " + sYellowSeal + "; WhiteSeal: " + sWhiteSeal
                        , "initBag"
                        , bagInitRequest.depotNr!!.toString())

                throw ServiceException(BagService.ErrorCode.UPDATE_MOVEPOOL_FAILED)
            }

            val dtNow: java.sql.Timestamp = java.sql.Timestamp.valueOf(LocalDateTime.now())

            iResultCount = dslContext.update(Tables.TBLDEPOTLISTE)
                    .set(Tables.TBLDEPOTLISTE.STRANGDATUM, dt.toTimestamp())
                    .where(Tables.TBLDEPOTLISTE.DEPOTNR.eq(bagInitRequest.depotNr!!.toInt())).execute()

            iResultCount = dslContext.insertInto(Tables.SSO_P_MOV,
                    Tables.SSO_P_MOV.PLOMBENNUMMER,
                    Tables.SSO_P_MOV.STATUS,
                    Tables.SSO_P_MOV.STATUSZEIT,
                    Tables.SSO_P_MOV.LASTDEPOT,
                    Tables.SSO_P_MOV.FARBE)
                    .values(dblWhiteSeal, 2.0, dtNow, bagInitRequest.depotNr!!.toDouble(), "weiss").execute()
            if (iResultCount == 0) {
                /**
                recHistory.depotid = "initBag"//bagInitRequest.depotNr!!.toString()
                recHistory.info = "WhiteSeal: " + sWhiteSeal
                recHistory.msglocation = "initBag"
                recHistory.orderid = bagInitRequest.depotNr!!.toString()
                logHistoryRepository.add(recHistory)
                **/

                logHistoryRepository.add("initBag"
                        , "WhiteSeal: " + sWhiteSeal
                        , "initBag"
                        , bagInitRequest.depotNr!!.toString())

                throw ServiceException(BagService.ErrorCode.INSERT_SEAL_MOVE_WHITE_FAILED)
            }
            iResultCount = dslContext.insertInto(Tables.SSO_P_MOV, Tables.SSO_P_MOV.PLOMBENNUMMER, Tables.SSO_P_MOV.STATUS, Tables.SSO_P_MOV.STATUSZEIT, Tables.SSO_P_MOV.LASTDEPOT, Tables.SSO_P_MOV.FARBE).values(dblYellowSeal, 2.0, dtNow, bagInitRequest.depotNr!!.toDouble(), "gelb").execute()
            if (iResultCount == 0) {
                /**
                recHistory.depotid = "initBag"//bagInitRequest.depotNr!!.toString()
                recHistory.info = "YellowSeal: " + sYellowSeal
                recHistory.msglocation = "initBag"
                recHistory.orderid = bagInitRequest.depotNr!!.toString()
                logHistoryRepository.add(recHistory)
                **/
                logHistoryRepository.add("initBag"
                        , "YellowSeal: " + sYellowSeal
                        , "initBag"
                        , bagInitRequest.depotNr!!.toString())
                throw ServiceException(BagService.ErrorCode.INSERT_SEAL_MOVE_YELLOW_FAILED)
            }
            return true
        } catch(e: ServiceException) {
            throw e
        } catch(e: Exception) {
            /**
            recHistory.depotid = "initBag"//bagInitRequest.depotNr!!.toString()
            recHistory.info = e.message ?: e.toString()
            recHistory.msglocation = "initBag"
            recHistory.orderid = bagInitRequest.depotNr!!.toString()
            logHistoryRepository.add(recHistory)
            **/
            logHistoryRepository.add("initBag"
                    , e.message ?: e.toString()
                    , "initBag"
                    , bagInitRequest.depotNr!!.toString())
            throw BadRequestException(e.message)
        }


        //throw NotImplementedError()
    }


    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun isFree(bagFreeRequest: BagFreeRequest): Boolean {
        if (bagFreeRequest.bagId.isNullOrEmpty()) {
            throw ServiceException(BagService.ErrorCode.BAG_ID_MISSING)
        }


        if (bagFreeRequest.depotNr == null) {
            throw ServiceException(BagService.ErrorCode.DEPOT_NR_MISSING)
        }

        if (bagFreeRequest.bagId!!.length < 12) {
            throw ServiceException(BagService.ErrorCode.BAG_ID_MISSING_CHECK_DIGIT)
        }


        if (bagFreeRequest.depotNr!! <= 0 || bagFreeRequest.depotNr!! > 999) {
            throw ServiceException(BagService.ErrorCode.DEPOT_NR_NOT_VALID)
        }

        if (!checkCheckDigit(bagFreeRequest.bagId!!)) {
            throw ServiceException(BagService.ErrorCode.BAG_ID_WRONG_CHECK_DIGIT)
        }


        //TODO

        //var recHistory = TblhistorieRecord()
        try {
            var dtWork: LocalDate = getWorkingDate()
            var result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN)
                    .where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1))
                    .and(Tables.TBLHUBLINIENPLAN.ARBEITSDATUM.equal(dtWork.toTimestamp())).fetch()
            //val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN).where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1)).fetch()
            if (result.getValue(0, 0) == 0) {
                //dtWork=nextWerktag(dtWork.addDays(-1),"100","DE","36285")
            } else {
                //nach Feierabend und Tagesabschluss schon die bags für den nächsten Tag initialisieren oder am Wochenende
                //dtWork=nextwerktag(dtWork,"100","DE","36285"
                dtWork = getNextDeliveryDate()
            }
            val dblStatus: Double = 1.0
            //val dt:java.util.Date=dtWork.toDate()
            val dt: java.sql.Date = java.sql.Date.valueOf(dtWork);

            val dblBagID: Double = bagFreeRequest.bagId!!.substring(0, 11).toDouble()
            val dblNull: Double? = null

            val sBagID: String = bagFreeRequest.bagId!!.substring(0, 11)

            var iResultCount: Int = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,
                    Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID)
                            .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus))
                            .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("p"))
                            .and(Tables.SSO_S_MOVEPOOL.MULTIBAG.equal(0)))
            if (iResultCount > 0) {
                return true
            }

            iResultCount = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,
                    Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID)
                            .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt)))
            if (iResultCount > 0) {
                //dieser Bag wurde bereits initialisiert
                throw ServiceException(BagService.ErrorCode.BAG_ALREADY_INITIALZED)
            }
            //:org.jooq.Result<SsoSMovepoolRecord>
//Tables.SSO_S_MOVEPOOL.ORDERDEPOT2HUB
            val dResult = dslContext.select().from(Tables.SSO_S_MOVEPOOL)
                    .where(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID))
                    .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                    .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.ne(dt)).fetch()
            if (dResult.size > 0) {
                val dblOrderIdDepot2Hub: Double = dResult.getValue(0, Tables.SSO_S_MOVEPOOL.ORDERDEPOT2HUB) ?: 0.0
                if (dblOrderIdDepot2Hub > 0) {
                    iResultCount = dslContext.update(Tables.TBLAUFTRAG)
                            .set(Tables.TBLAUFTRAG.LOCKFLAG, 4)
                            .set(Tables.TBLAUFTRAG.SDGSTATUS, "L")
                            .where(Tables.TBLAUFTRAG.ORDERID.eq(dblOrderIdDepot2Hub))
                            .execute()
                }
            }
            iResultCount = dslContext.update(Tables.SSO_S_MOVEPOOL)
                    .set(Tables.SSO_S_MOVEPOOL.MOVEPOOL, "p")
                    .set(Tables.SSO_S_MOVEPOOL.STATUS, dblStatus)
                    .set(Tables.SSO_S_MOVEPOOL.PRINTED, -1.0)
                    .set(Tables.SSO_S_MOVEPOOL.MULTIBAG, 0)
                    .where(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID))
                    .execute()
            if (iResultCount < 1) {
                /**
                recHistory.depotid = "isBagFree"
                recHistory.info = "Problem beim update sso_s_movepool"
                recHistory.msglocation = "isBagFree"
                recHistory.orderid = bagFreeRequest.depotNr!!.toString()
                logHistoryRepository.add(recHistory)
                **/
                logHistoryRepository.add("isBagFree"
                        , "Problem beim update sso_s_movepool"
                        , "isBagFree"
                        , bagFreeRequest.depotNr!!.toString())
                return false
            }


            return true
        } catch(e: ServiceException) {
            throw e
        } catch(e: Exception) {
            /**
            recHistory.depotid = "isBagFree"//bagInitRequest.depotNr!!.toString()
            recHistory.info = e.message ?: e.toString()
            recHistory.msglocation = "isBagFree"
            recHistory.orderid = bagFreeRequest.depotNr!!.toString()
            logHistoryRepository.add(recHistory)
            **/
            logHistoryRepository.add("isBagFree"
                    , e.message ?: e.toString()
                    , "isBagFree"
                    , bagFreeRequest.depotNr!!.toString())
            throw BadRequestException(e.message)
        }


        //throw NotImplementedError()
    }


}
