package org.deku.leoz.central.rest.services.internal.v1

//import java.util.*

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.repository.HistoryJooqRepository
import org.deku.leoz.node.rest.ServiceException
import org.deku.leoz.rest.entity.internal.v1.BagFreeRequest
import org.deku.leoz.rest.entity.internal.v1.BagInitRequest
import org.deku.leoz.rest.service.internal.v1.BagService.ErrorCode
import org.deku.leoz.util.checkCheckDigit
import org.deku.leoz.util.getNextDeliveryDate
import org.deku.leoz.util.getWorkingDate
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.ApiKey
import sx.time.toDate
import sx.time.toSqlDate
import sx.time.toTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.BadRequestException
import javax.ws.rs.Path

/**
 * Bundle service (leoz-central)
 * Created by masc on 01/11/2016.
 **/
@Named
@ApiKey(false)
@Path("internal/v1/bag")
class BagService : org.deku.leoz.rest.service.internal.v1.BagService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    @Inject
    private lateinit var logHistoryRepository: HistoryJooqRepository

    override fun get(id: String): String {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return "String Bag:" + id
    }

    /**
     * ${link
     */
    override fun initialize(bagInitRequest: BagInitRequest): Boolean {
        val bagId = bagInitRequest.bagId
        val depotNr = bagInitRequest.depotNr
        val yellowSeal = bagInitRequest.yellowSeal
        val whiteSeal = bagInitRequest.whiteSeal

        if (bagId == null || bagId.isEmpty()) {
            throw ServiceException(ErrorCode.BAG_ID_MISSING)
        }

        if (whiteSeal == null || whiteSeal.isEmpty()) {
            throw ServiceException(ErrorCode.WHITE_SEAL_MISSING)
        }

        if (yellowSeal == null || yellowSeal.isEmpty()) {
            throw ServiceException(ErrorCode.YELLOW_SEAL_MISSING)
        }

        if (depotNr == null) {
            throw ServiceException(ErrorCode.DEPOT_NR_MISSING)
        }

        if (bagId.length < 12) {
            throw ServiceException(ErrorCode.BAG_ID_MISSING_CHECK_DIGIT)
        }

        if (whiteSeal.length < 12) {
            throw ServiceException(ErrorCode.WHITE_SEAL_MISSING_CHECK_DIGIT)
        }
        if (yellowSeal.length < 12) {
            throw ServiceException(ErrorCode.YELLOW_SEAL_MISSING_CHECK_DIGIT)
        }
        if (depotNr <= 0 || depotNr > 999) {
            throw ServiceException(ErrorCode.DEPOT_NR_NOT_VALID)
        }

        if (!checkCheckDigit(bagId)) {
            throw ServiceException(ErrorCode.BAG_ID_WRONG_CHECK_DIGIT)
        }

        if (!checkCheckDigit(whiteSeal)) {
            throw ServiceException(ErrorCode.WHITE_SEAL_WRONG_CHECK_DIGIT)
        }
        if (!checkCheckDigit(yellowSeal)) {
            throw ServiceException(ErrorCode.YELLOW_SEAL_WRONG_CHECK_DIGIT)
        }

        // TODO
        // TODO: define constants for repetitive strings (eg. "initBag", "isBagFree")

        //var recHistory = TblhistorieRecord()
        try {
            var dtWork: LocalDate = getWorkingDate()
            val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN)
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
            val dt = java.sql.Date.valueOf(dtWork);

            /**
            result=dslContext.selectCount().from(Tables.SSO_S_MOVEPOOL).where(Tables.SSO_S_MOVEPOOL.LASTDEPOT.eq(bagInitRequest.depotNr!!.toDouble())).and(Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus)).and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m")).and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt)).fetch()
            if(result.getValue(0,0)!=0){
            throw ServiceException(BagService.ErrorCode.BAG_FOR_DEPOT_ALREADY_EXISTS)
            }
             **/

            var iResultCount = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,
                    Tables.SSO_S_MOVEPOOL.LASTDEPOT.eq(depotNr.toDouble())
                            .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus))
                            .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt)))
            if (iResultCount > 0) {
                throw ServiceException(ErrorCode.BAG_FOR_DEPOT_ALREADY_EXISTS)
            }


            val dtNow = Date()

            val dblBagID = bagId.substring(0, 11).toDouble()
            val dblWhiteSeal = whiteSeal.substring(0, 11).toDouble()
            val dblYellowSeal = yellowSeal.substring(0, 11).toDouble()
            val dblNull: Double? = null

            val sBagID = bagId.substring(0, 11)
            val sYellowSeal = yellowSeal.substring(0, 11)
            val sWhiteSeal = whiteSeal.substring(0, 11)

            // TODO: use `.newRecord`, then `.store` or `.update` respectively
            /**
            iResultCount = dslContext.update(Tables.SSO_S_MOVEPOOL)
            .set(Tables.SSO_S_MOVEPOOL.ORDERDEPOT2HUB, dblNull)
            .set(Tables.SSO_S_MOVEPOOL.ORDERHUB2DEPOT, dblNull)
            .set(Tables.SSO_S_MOVEPOOL.SEAL_NUMBER_GREEN, dblWhiteSeal)
            .set(Tables.SSO_S_MOVEPOOL.SEAL_NUMBER_YELLOW, dblYellowSeal)
            .set(Tables.SSO_S_MOVEPOOL.STATUS, dblStatus)
            .set(Tables.SSO_S_MOVEPOOL.STATUS_TIME, dtNow.toTimestamp())
            .set(Tables.SSO_S_MOVEPOOL.INIT_STATUS, 1)
            .set(Tables.SSO_S_MOVEPOOL.LASTDEPOT, depotNr.toDouble())
            .set(Tables.SSO_S_MOVEPOOL.WORK_DATE, dt)
            .set(Tables.SSO_S_MOVEPOOL.MOVEPOOL, "m")
            .where(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID)).execute()
             **/
            val recMovePool = dslContext.fetchOne(Tables.SSO_S_MOVEPOOL, Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID))
            if (recMovePool == null) {
                logHistoryRepository.save(
                        depotId = "initBag",
                        info = "BagID: ${sBagID} not found",
                        msgLocation = "initBag",
                        orderId = depotNr.toString())

                throw ServiceException(ErrorCode.UPDATE_MOVEPOOL_FAILED)
            }
            recMovePool.orderdepot2hub = dblNull
            recMovePool.orderhub2depot = dblNull
            recMovePool.sealNumberGreen = dblWhiteSeal
            recMovePool.sealNumberYellow = dblYellowSeal
            recMovePool.status = dblStatus
            recMovePool.statusTime = dtNow.toTimestamp()
            recMovePool.initStatus = 1
            recMovePool.lastdepot = depotNr.toDouble()
            recMovePool.workDate = dt
            recMovePool.movepool = "m"
            iResultCount = recMovePool.update()

            if (iResultCount == 0) {
                /**
                recHistory.depotid = "initBag"//bagInitRequest.depotNr!!.toString()
                recHistory.info = "BagID: " + sBagID + "; YellowSeal: " + sYellowSeal + "; WhiteSeal: " + sWhiteSeal
                recHistory.msglocation = "initBag"
                recHistory.orderid = bagInitRequest.depotNr!!.toString()
                logHistoryRepository.add(recHistory)
                 **/
                logHistoryRepository.save(
                        depotId = "initBag",
                        info = "BagID: ${sBagID}; YellowSeal: ${sYellowSeal}; WhiteSeal: ${sWhiteSeal}",
                        msgLocation = "initBag",
                        orderId = depotNr.toString())

                throw ServiceException(ErrorCode.UPDATE_MOVEPOOL_FAILED)
            }


            /**
            iResultCount = dslContext.update(Tables.TBLDEPOTLISTE)
            .set(Tables.TBLDEPOTLISTE.STRANGDATUM, dt.toTimestamp())
            .where(Tables.TBLDEPOTLISTE.DEPOTNR.eq(depotNr.toInt()))
            .execute()
             **/
            val recDepotliste = dslContext.fetchOne(Tables.TBLDEPOTLISTE, Tables.TBLDEPOTLISTE.DEPOTNR.eq(depotNr.toInt()))
            if (recDepotliste == null) {
                logHistoryRepository.save(
                        depotId = "initBag",
                        info = "DepotNr not found",
                        msgLocation = "initBag",
                        orderId = depotNr.toString())

                throw ServiceException(ErrorCode.UPDATE_DEPOTLIST_FAILED)
            }
            recDepotliste.strangdatum = dt.toTimestamp()
            iResultCount = recDepotliste.update()

            // TODO: use `.newRecord`, then `.store` or `.insert` respectively
            /**
            iResultCount = dslContext.insertInto(Tables.SSO_P_MOV,
            Tables.SSO_P_MOV.PLOMBENNUMMER,
            Tables.SSO_P_MOV.STATUS,
            Tables.SSO_P_MOV.STATUSZEIT,
            Tables.SSO_P_MOV.LASTDEPOT,
            Tables.SSO_P_MOV.FARBE)
            .values(
            dblWhiteSeal,
            2.0,
            dtNow.toTimestamp(),
            depotNr.toDouble(),
            "weiss").execute()
             **/
            val recWhite = dslContext.newRecord(Tables.SSO_P_MOV)
            recWhite.plombennummer = dblWhiteSeal
            recWhite.status = 2.0
            recWhite.statuszeit = dtNow.toTimestamp()
            recWhite.lastdepot = depotNr.toDouble()
            recWhite.farbe = "weiss"
            iResultCount = recWhite.store()

            if (iResultCount == 0) {
                /**
                recHistory.depotid = "initBag"//bagInitRequest.depotNr!!.toString()
                recHistory.info = "WhiteSeal: " + sWhiteSeal
                recHistory.msglocation = "initBag"
                recHistory.orderid = bagInitRequest.depotNr!!.toString()
                logHistoryRepository.add(recHistory)
                 **/

                logHistoryRepository.save(
                        depotId = "initBag",
                        info = "WhiteSeal: ${sWhiteSeal}",
                        msgLocation = "initBag",
                        orderId = depotNr.toString())

                throw ServiceException(ErrorCode.INSERT_SEAL_MOVE_WHITE_FAILED)
            }

            // TODO: use `.newRecord`, then `.store` or `.insert` respectively
            /**
            iResultCount = dslContext.insertInto(
            Tables.SSO_P_MOV,
            Tables.SSO_P_MOV.PLOMBENNUMMER,
            Tables.SSO_P_MOV.STATUS,
            Tables.SSO_P_MOV.STATUSZEIT,
            Tables.SSO_P_MOV.LASTDEPOT,
            Tables.SSO_P_MOV.FARBE)
            .values(
            dblYellowSeal,
            2.0,
            dtNow.toTimestamp(),
            depotNr.toDouble(),
            "gelb")
            .execute()
             **/
            val recYellow = dslContext.newRecord(Tables.SSO_P_MOV)
            recYellow.plombennummer = dblYellowSeal
            recYellow.status = 2.0
            recYellow.statuszeit = dtNow.toTimestamp()
            recYellow.lastdepot = depotNr.toDouble()
            recYellow.farbe = "gelb"
            iResultCount = recYellow.store()

            if (iResultCount == 0) {
                /**
                recHistory.depotid = "initBag"//bagInitRequest.depotNr!!.toString()
                recHistory.info = "YellowSeal: " + sYellowSeal
                recHistory.msglocation = "initBag"
                recHistory.orderid = bagInitRequest.depotNr!!.toString()
                logHistoryRepository.add(recHistory)
                 **/
                logHistoryRepository.save(
                        depotId = "initBag",
                        info = "YellowSeal: ${sYellowSeal}",
                        msgLocation = "initBag",
                        orderId = depotNr.toString())
                throw ServiceException(ErrorCode.INSERT_SEAL_MOVE_YELLOW_FAILED)
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
            logHistoryRepository.save(
                    depotId = "initBag",
                    info = e.message ?: e.toString(),
                    msgLocation = "initBag",
                    orderId = depotNr.toString())
            throw BadRequestException(e.message)
        }


        //throw NotImplementedError()
    }

    /**
     * D
     */
    override fun isFree(bagFreeRequest: BagFreeRequest): Boolean {
        val bagId = bagFreeRequest.bagId
        val depotNr = bagFreeRequest.depotNr

        if (bagId == null || bagId.isEmpty()) {
            throw ServiceException(ErrorCode.BAG_ID_MISSING)
        }

        if (depotNr == null) {
            throw ServiceException(ErrorCode.DEPOT_NR_MISSING)
        }

        if (bagId.length < 12) {
            throw ServiceException(ErrorCode.BAG_ID_MISSING_CHECK_DIGIT)
        }

        if (depotNr <= 0 || depotNr > 999) {
            throw ServiceException(ErrorCode.DEPOT_NR_NOT_VALID)
        }

        if (!checkCheckDigit(bagId)) {
            throw ServiceException(ErrorCode.BAG_ID_WRONG_CHECK_DIGIT)
        }

        //TODO

        //var recHistory = TblhistorieRecord()
        try {
            var dtWork: LocalDate = getWorkingDate()
            val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN)
                    .where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1))
                    .and(Tables.TBLHUBLINIENPLAN.ARBEITSDATUM.equal(dtWork.toTimestamp()))
                    .fetch()
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
            val dt: Date = dtWork.toDate()

            val dblBagID = bagId.substring(0, 11).toDouble()
            val dblNull: Double? = null

            val sBagID = bagId.substring(0, 11)

            var iResultCount = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,
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
                            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate())))
            if (iResultCount > 0) {
                //dieser Bag wurde bereits initialisiert
                throw ServiceException(ErrorCode.BAG_ALREADY_INITIALZED)
            }
            //:org.jooq.Result<SsoSMovepoolRecord>
//Tables.SSO_S_MOVEPOOL.ORDERDEPOT2HUB
            val dResult = dslContext.select()
                    .from(Tables.SSO_S_MOVEPOOL)
                    .where(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID))
                    .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                    .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.ne(dt.toSqlDate()))
                    .fetch()

            if (dResult.size > 0) {
                val dblOrderIdDepot2Hub: Double = dResult.getValue(0, Tables.SSO_S_MOVEPOOL.ORDERDEPOT2HUB) ?: 0.0
                if (dblOrderIdDepot2Hub > 0) {
                    /**
                    iResultCount = dslContext.update(Tables.TBLAUFTRAG)
                    .set(Tables.TBLAUFTRAG.LOCKFLAG, 4)
                    .set(Tables.TBLAUFTRAG.SDGSTATUS, "L")
                    .where(Tables.TBLAUFTRAG.ORDERID.eq(dblOrderIdDepot2Hub))
                    .execute()
                     **/
                    val recOrder = dslContext.fetchOne(Tables.TBLAUFTRAG, Tables.TBLAUFTRAG.ORDERID.eq(dblOrderIdDepot2Hub))
                    if (recOrder != null) {

                        recOrder.lockflag = 4
                        recOrder.sdgstatus = "L"
                        iResultCount = recOrder.update()
                    }
                }
            }

            // TODO: use `.newRecord`, then `.store` or `.update` respectively
            /**
            iResultCount = dslContext.update(Tables.SSO_S_MOVEPOOL)
            .set(Tables.SSO_S_MOVEPOOL.MOVEPOOL, "p")
            .set(Tables.SSO_S_MOVEPOOL.STATUS, dblStatus)
            .set(Tables.SSO_S_MOVEPOOL.PRINTED, -1.0)
            .set(Tables.SSO_S_MOVEPOOL.MULTIBAG, 0)
            .where(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID))
            .execute()
             **/
            val recFree = dslContext.fetchOne(Tables.SSO_S_MOVEPOOL, Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID))
            if (recFree != null) {
                recFree.movepool = "p"
                recFree.status = dblStatus
                recFree.printed = -1.0
                recFree.multibag = 0
                iResultCount = recFree.update()
            } else {
                iResultCount = 0
            }

            if (iResultCount < 1) {
                /**
                recHistory.depotid = "isBagFree"
                recHistory.info = "Problem beim update sso_s_movepool"
                recHistory.msglocation = "isBagFree"
                recHistory.orderid = bagFreeRequest.depotNr!!.toString()
                logHistoryRepository.add(recHistory)
                 **/
                logHistoryRepository.save(
                        depotId = "isBagFree",
                        info = "Problem beim update sso_s_movepool",
                        msgLocation = "isBagFree",
                        orderId = depotNr.toString())
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
            logHistoryRepository.save(
                    depotId = "isBagFree",
                    info = e.message ?: e.toString(),
                    msgLocation = "isBagFree",
                    orderId = depotNr.toString())
            throw BadRequestException(e.message)
        }


        //throw NotImplementedError()
    }


}
