package org.deku.leoz.central.rest.services.internal.v1

//import java.util.*

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.repository.HistoryJooqRepository
import org.deku.leoz.node.rest.ServiceException
import org.deku.leoz.rest.entity.internal.v1.*
import org.deku.leoz.rest.service.internal.v1.BagService.ErrorCode
import org.deku.leoz.util.checkCheckDigit
import org.deku.leoz.util.getNextDeliveryDate
import org.deku.leoz.util.getWorkingDate
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.auth.ApiKey
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
    override fun initialize(bagId: String?, bagInitRequest: BagInitRequest): Boolean {
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
        val sInitBag = "initBag"

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
            val dt = java.sql.Date.valueOf(dtWork)


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


            val recMovePool = dslContext.fetchOne(Tables.SSO_S_MOVEPOOL, Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID))
            if (recMovePool == null) {
                logHistoryRepository.save(
                        depotId = sInitBag,
                        info = "BagID: ${sBagID} not found",
                        msgLocation = sInitBag,
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

                logHistoryRepository.save(
                        depotId = sInitBag,
                        info = "BagID: ${sBagID}; YellowSeal: ${sYellowSeal}; WhiteSeal: ${sWhiteSeal}",
                        msgLocation = sInitBag,
                        orderId = depotNr.toString())

                throw ServiceException(ErrorCode.UPDATE_MOVEPOOL_FAILED)
            }

            val recDepotliste = dslContext.fetchOne(Tables.TBLDEPOTLISTE, Tables.TBLDEPOTLISTE.DEPOTNR.eq(depotNr.toInt()))
            if (recDepotliste == null) {
                logHistoryRepository.save(
                        depotId = sInitBag,
                        info = "DepotNr not found",
                        msgLocation = sInitBag,
                        orderId = depotNr.toString())

                throw ServiceException(ErrorCode.UPDATE_DEPOTLIST_FAILED)
            }
            recDepotliste.strangdatum = dt.toTimestamp()
            iResultCount = recDepotliste.update()


            val recWhite = dslContext.newRecord(Tables.SSO_P_MOV)
            recWhite.plombennummer = dblWhiteSeal
            recWhite.status = 2.0
            recWhite.statuszeit = dtNow.toTimestamp()
            recWhite.lastdepot = depotNr.toDouble()
            recWhite.farbe = "weiss"
            iResultCount = recWhite.store()

            if (iResultCount == 0) {


                logHistoryRepository.save(
                        depotId = sInitBag,
                        info = "WhiteSeal: ${sWhiteSeal}",
                        msgLocation = sInitBag,
                        orderId = depotNr.toString())

                throw ServiceException(ErrorCode.INSERT_SEAL_MOVE_WHITE_FAILED)
            }


            val recYellow = dslContext.newRecord(Tables.SSO_P_MOV)
            recYellow.plombennummer = dblYellowSeal
            recYellow.status = 2.0
            recYellow.statuszeit = dtNow.toTimestamp()
            recYellow.lastdepot = depotNr.toDouble()
            recYellow.farbe = "gelb"
            iResultCount = recYellow.store()

            if (iResultCount == 0) {

                logHistoryRepository.save(
                        depotId = sInitBag,
                        info = "YellowSeal: ${sYellowSeal}",
                        msgLocation = sInitBag,
                        orderId = depotNr.toString())
                throw ServiceException(ErrorCode.INSERT_SEAL_MOVE_YELLOW_FAILED)
            }
            return true
        } catch(e: ServiceException) {
            throw e
        } catch(e: Exception) {

            logHistoryRepository.save(
                    depotId = sInitBag,
                    info = e.message ?: e.toString(),
                    msgLocation = sInitBag,
                    orderId = depotNr.toString())
            throw BadRequestException(e.message)
        }


        //throw NotImplementedError()
    }

    /**
     * D
     */
    override fun isFree(bagId: String?, depotNr: Int?): Boolean {
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

        val isBagFree = "isBagFree"
        try {
            var dtWork: LocalDate = getWorkingDate()
            val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN)
                    .where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1))
                    .and(Tables.TBLHUBLINIENPLAN.ARBEITSDATUM.equal(dtWork.toTimestamp()))
                    .fetch()
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

                    val recOrder = dslContext.fetchOne(Tables.TBLAUFTRAG, Tables.TBLAUFTRAG.ORDERID.eq(dblOrderIdDepot2Hub))
                    if (recOrder != null) {

                        recOrder.lockflag = 4
                        recOrder.sdgstatus = "L"
                        iResultCount = recOrder.update()
                    }
                }
            }


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

                logHistoryRepository.save(
                        depotId = isBagFree,
                        info = "Problem beim update sso_s_movepool",
                        msgLocation = isBagFree,
                        orderId = depotNr.toString())
                return false
            }


            return true
        } catch(e: ServiceException) {
            throw e
        } catch(e: Exception) {

            logHistoryRepository.save(
                    depotId = isBagFree,
                    info = e.message ?: e.toString(),
                    msgLocation = isBagFree,
                    orderId = depotNr.toString())
            throw BadRequestException(e.message)
        }


        //throw NotImplementedError()
    }

    override fun getNumberRange(): BagNumberRange {
        var minBagId: Double? = null
        var maxBagId: Double? = null
        var minWhiteSeal: Double? = null
        var maxWhiteSeal: Double? = null
        var minYellowSeal: Double? = null
        var maxYellowSeal: Double? = null
        var minUnitNo: Double? = null
        var maxUnitNo: Double? = null
        var minUnitNoBack: Double? = null
        var maxUnitNoBack: Double? = null

        val getNumberRange = "getNumberRange"

        try {

            var left3WhiteSeal = 0
            var digit4WhiteSeal = 0
            var left3YellowSeal = 0
            var digit4YellowSeal = 0
            var left4BagUnitNo = 0
            var offsetBagUnitNo = 0
            var left4BagUnitNoBack = 0
            var offsetUnitNoBack = 0
            var left3BagId = 0
            var digit4BagId = 0

            // TODO: this is a perfect candidate for a repository method, eliminates 90% code per fetch. eg. `val left3WhiteSeal = sysCollectionsRepo.find(typ = 85, sort = 10)`
            var result = dslContext.select()
                    .from(Tables.TBLSYSCOLLECTIONS)
                    .where(Tables.TBLSYSCOLLECTIONS.TYP.eq(85))
                    .and(Tables.TBLSYSCOLLECTIONS.SORT.eq(10))
                    .fetch()
            if (result.size > 0) {
                left3WhiteSeal = result.getValue(0, Tables.TBLSYSCOLLECTIONS.IDVALUE) ?: 0
            }
            result = dslContext.select()
                    .from(Tables.TBLSYSCOLLECTIONS)
                    .where(Tables.TBLSYSCOLLECTIONS.TYP.eq(85))
                    .and(Tables.TBLSYSCOLLECTIONS.SORT.eq(11))
                    .fetch()
            if (result.size > 0) {
                digit4WhiteSeal = result.getValue(0, Tables.TBLSYSCOLLECTIONS.IDVALUE) ?: 0
            }
            result = dslContext.select()
                    .from(Tables.TBLSYSCOLLECTIONS)
                    .where(Tables.TBLSYSCOLLECTIONS.TYP.eq(85))
                    .and(Tables.TBLSYSCOLLECTIONS.SORT.eq(20))
                    .fetch()
            if (result.size > 0) {
                left3YellowSeal = result.getValue(0, Tables.TBLSYSCOLLECTIONS.IDVALUE) ?: 0
            }
            result = dslContext.select()
                    .from(Tables.TBLSYSCOLLECTIONS)
                    .where(Tables.TBLSYSCOLLECTIONS.TYP.eq(85))
                    .and(Tables.TBLSYSCOLLECTIONS.SORT.eq(21))
                    .fetch()
            if (result.size > 0) {
                digit4YellowSeal = result.getValue(0, Tables.TBLSYSCOLLECTIONS.IDVALUE) ?: 0
            }
            result = dslContext.select()
                    .from(Tables.TBLSYSCOLLECTIONS)
                    .where(Tables.TBLSYSCOLLECTIONS.TYP.eq(85))
                    .and(Tables.TBLSYSCOLLECTIONS.SORT.eq(30))
                    .fetch()
            if (result.size > 0) {
                left4BagUnitNo = result.getValue(0, Tables.TBLSYSCOLLECTIONS.IDVALUE) ?: 0
            }
            result = dslContext.select()
                    .from(Tables.TBLSYSCOLLECTIONS)
                    .where(Tables.TBLSYSCOLLECTIONS.TYP.eq(85))
                    .and(Tables.TBLSYSCOLLECTIONS.SORT.eq(31))
                    .fetch()
            if (result.size > 0) {
                offsetBagUnitNo = result.getValue(0, Tables.TBLSYSCOLLECTIONS.IDVALUE) ?: 0
            }
            result = dslContext.select()
                    .from(Tables.TBLSYSCOLLECTIONS)
                    .where(Tables.TBLSYSCOLLECTIONS.TYP.eq(85))
                    .and(Tables.TBLSYSCOLLECTIONS.SORT.eq(40))
                    .fetch()
            if (result.size > 0) {
                left4BagUnitNoBack = result.getValue(0, Tables.TBLSYSCOLLECTIONS.IDVALUE) ?: 0
            }
            result = dslContext.select()
                    .from(Tables.TBLSYSCOLLECTIONS)
                    .where(Tables.TBLSYSCOLLECTIONS.TYP.eq(85))
                    .and(Tables.TBLSYSCOLLECTIONS.SORT.eq(41))
                    .fetch()
            if (result.size > 0) {
                offsetUnitNoBack = result.getValue(0, Tables.TBLSYSCOLLECTIONS.IDVALUE) ?: 0
            }
            result = dslContext.select()
                    .from(Tables.TBLSYSCOLLECTIONS)
                    .where(Tables.TBLSYSCOLLECTIONS.TYP.eq(85))
                    .and(Tables.TBLSYSCOLLECTIONS.SORT.eq(60))
                    .fetch()
            if (result.size > 0) {
                left3BagId = result.getValue(0, Tables.TBLSYSCOLLECTIONS.IDVALUE) ?: 0
            }
            result = dslContext.select()
                    .from(Tables.TBLSYSCOLLECTIONS)
                    .where(Tables.TBLSYSCOLLECTIONS.TYP.eq(85))
                    .and(Tables.TBLSYSCOLLECTIONS.SORT.eq(61))
                    .fetch()
            if (result.size > 0) {
                digit4BagId = result.getValue(0, Tables.TBLSYSCOLLECTIONS.IDVALUE) ?: 0
            }

            var tmp = ""
            if (digit4BagId > 0 && left3BagId > 0) {
                tmp = left3BagId.toString() + digit4BagId.toString() + "0000000"
                minBagId = tmp.toDouble()
                maxBagId = minBagId + 9999999
            }
            if (digit4WhiteSeal > 0 && left3WhiteSeal > 0) {
                tmp = left3WhiteSeal.toString() + digit4WhiteSeal.toString() + "0000000"
                minWhiteSeal = tmp.toDouble()
                maxWhiteSeal = minWhiteSeal + 9999999
            }
            if (digit4YellowSeal > 0 && left3YellowSeal > 0) {
                tmp = left3YellowSeal.toString() + digit4YellowSeal.toString() + "0000000"
                minYellowSeal = tmp.toDouble()
                maxYellowSeal = minYellowSeal + 9999999
            }
            if (left4BagUnitNo > 0 && offsetBagUnitNo > 0) {
                tmp = left4BagUnitNo.toString() + offsetBagUnitNo.toString()//10071000000
                minUnitNo = tmp.toDouble()
                maxUnitNo = minUnitNo + 999999
            }
            if (left4BagUnitNoBack > 0 && offsetUnitNoBack > 0) {
                tmp = left4BagUnitNoBack.toString() + offsetUnitNoBack.toString()//10072000000
                minUnitNoBack = tmp.toDouble()
                maxUnitNoBack = minUnitNoBack + 999999
            }

            val bagserviceNumberRange = BagNumberRange(
                    minBagId = minBagId,
                    maxBagId = maxBagId,
                    minWhiteSeal = minWhiteSeal,
                    maxWhiteSeal = maxWhiteSeal,
                    minYellowSeal = minYellowSeal,
                    maxYellowSeal = maxYellowSeal,
                    minUnitNo = minUnitNo,
                    maxUnitNo = maxUnitNo,
                    minUnitNoBack = minUnitNoBack,
                    maxUnitNoBack = maxUnitNoBack)


            return bagserviceNumberRange
        } catch (e: Exception) {
            logHistoryRepository.save(
                    depotId = getNumberRange,
                    info = e.message ?: e.toString(),
                    msgLocation = getNumberRange,
                    orderId = "")
            throw BadRequestException(e.message)
        }
    }

    override fun getSectionDepots(section: Int?, position: Int?): List<String> {
        if (section == null) {
            throw ServiceException(ErrorCode.SECTION_MISSING)
        }
        if (position == null) {
            throw ServiceException(ErrorCode.POSITION_MISSING)
        }
        val getSectionDepots = "getSectionDepots"
        try {
            val l: List<String>
            l = listOf("Hallo", "Test")
            //findSectionDepots
            return l
        } catch (e: Exception) {
            logHistoryRepository.save(
                    depotId = getSectionDepots,
                    info = e.message ?: e.toString(),
                    msgLocation = getSectionDepots,
                    orderId = "")
            throw BadRequestException(e.message)
        }
    }

    override fun getSectionDepotsLeft(section: Int?, position: Int?): SectionDepotsLeft {
        if (section == null) {
            throw ServiceException(ErrorCode.SECTION_MISSING)
        }
        if (position == null) {
            throw ServiceException(ErrorCode.POSITION_MISSING)
        }
        val sGetSectionDepotsLeft = "getSectionDepotsLeft"
        try {


            var dtWork: LocalDate = getWorkingDate()
            val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN)
                    .where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1))
                    .and(Tables.TBLHUBLINIENPLAN.ARBEITSDATUM.equal(dtWork.toTimestamp()))
                    .fetch()
            if (result.getValue(0, 0) == 0) {
                //dtWork=nextWerktag(dtWork.addDays(-1),"100","DE","36285")
            } else {
                //nach Feierabend und Tagesabschluss schon die bags für den nächsten Tag initialisieren oder am Wochenende
                //dtWork=nextwerktag(dtWork,"100","DE","36285"
                dtWork = getNextDeliveryDate()
            }
            //val dt:java.util.Date=dtWork.toDate()
            val dt: Date = dtWork.toDate()

            val resultCount = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,
                    Tables.SSO_S_MOVEPOOL.STATUS.eq(5.0)
                            .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate()))
            )
            /**.and(Tables.SSO_S_MOVEPOOL.LASTDEPOT.in(select(Depotnr).from(Views.sectiondepotlist)
            .where(section.eq(iSection)
            .and(position.eq(iPosition))))
             **/
            val l: List<String>
            l = listOf("Hallo", "Test")


            /**
            l=dslConext.select(DEPOT)
            .from(Views.sectiondepotlist)
            .where(section.eq(iSection)
            .and(position.eq(iPosition)
            .and(depotnr.ni(select lastdepot from sso_s_movepool where movepool='m' and status=5 and work_date=))
            .fetchInto(String::class.java)
             **/

            val sectionDepotsLeft = SectionDepotsLeft(l, resultCount)
            return sectionDepotsLeft
        } catch (e: Exception) {
            logHistoryRepository.save(
                    depotId = sGetSectionDepotsLeft,
                    info = e.message ?: e.toString(),
                    msgLocation = sGetSectionDepotsLeft,
                    orderId = "")
            throw BadRequestException(e.message)
        }
    }

    override fun isOk(bagId: String?, unitNo: String?): BagResponse {
        if (bagId == null || bagId.isEmpty()) {
            throw ServiceException(ErrorCode.BAG_ID_MISSING)
        }

        if (unitNo == null || unitNo.isEmpty()) {
            throw ServiceException(ErrorCode.BAG_UNITNO_MISSING)
        }

        if (bagId.length < 12) {
            throw ServiceException(ErrorCode.BAG_ID_MISSING_CHECK_DIGIT)
        }

        if (unitNo.length < 12) {
            throw ServiceException(ErrorCode.BAG_UNITNO_MISSING_CHECK_DIGIT)
        }

        if (!checkCheckDigit(bagId)) {
            throw ServiceException(ErrorCode.BAG_ID_WRONG_CHECK_DIGIT)
        }
        if (!checkCheckDigit(unitNo)) {
            throw ServiceException(ErrorCode.BAG_UNITNO_WRONG_CHECK_DIGIT)
        }
        var ok = false
        var info: String? = null
        val isBagOk = "isBagOk"
        try {
            var dtWork: LocalDate = getWorkingDate()
            val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN)
                    .where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1))
                    .and(Tables.TBLHUBLINIENPLAN.ARBEITSDATUM.equal(dtWork.toTimestamp()))
                    .fetch()
            if (result.getValue(0, 0) == 0) {
                //dtWork=nextWerktag(dtWork.addDays(-1),"100","DE","36285")
            } else {
                //nach Feierabend und Tagesabschluss schon die bags für den nächsten Tag initialisieren oder am Wochenende
                //dtWork=nextwerktag(dtWork,"100","DE","36285"
                dtWork = getNextDeliveryDate()
            }
            //val dt:java.util.Date=dtWork.toDate()
            val dt: Date = dtWork.toDate()

            val dblBagID = bagId.substring(0, 11).toDouble()
            val dblNull: Double? = null

            val sBagID = bagId.substring(0, 11)
            val dblUnitNo = unitNo.substring(0, 11).toDouble()
            val sUnitNo = unitNo.substring(0, 11)

            val dblStatus = 5.0

            var resultCount = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,
                    Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus)
                            .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate()))
                            .and(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID))
                            .and(Tables.SSO_S_MOVEPOOL.ORDERHUB2DEPOT.eq(
                                    dslContext.select(Tables.TBLAUFTRAGCOLLIES.ORDERID)
                                            .from(Tables.TBLAUFTRAGCOLLIES)
                                            .where(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR.eq(dblUnitNo)))))
            if (resultCount <= 0) {
                throw ServiceException(ErrorCode.NO_DATA)
            }
            val recOk = dslContext.fetchOne(Tables.SSO_S_MOVEPOOL, Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID)
                    .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                    .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate()))
                    .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus))

                    .and(Tables.SSO_S_MOVEPOOL.ORDERHUB2DEPOT.eq(
                            dslContext.select(Tables.TBLAUFTRAGCOLLIES.ORDERID)
                                    .from(Tables.TBLAUFTRAGCOLLIES)
                                    .where(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR.eq(dblUnitNo)))))
            if (recOk != null) {
                recOk.initStatus = 4
                resultCount = recOk.update()
            } else {
                resultCount = 0
            }


            resultCount = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,
                    Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus)
                            .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate()))
                            .and(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID))
                            .and(Tables.SSO_S_MOVEPOOL.INIT_STATUS.eq(4))
                            .and(Tables.SSO_S_MOVEPOOL.ORDERHUB2DEPOT.eq(
                                    dslContext.select(Tables.TBLAUFTRAGCOLLIES.ORDERID)
                                            .from(Tables.TBLAUFTRAGCOLLIES)
                                            .where(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR.eq(dblUnitNo)))))
            if (resultCount <= 0) {
                logHistoryRepository.save(
                        depotId = isBagOk,
                        info = "set Init_status=4 BagID: ${sBagID}",
                        msgLocation = isBagOk,
                        orderId = sUnitNo)
                return BagResponse(ok, "kein init_status=4")
            }
            ok = true

            //test ob alle bags für diesen Strang gecheckt->freigeben für Beladung
            val lastdepot: Double?
            val mresult = dslContext.select()
                    .from(Tables.SSO_S_MOVEPOOL)
                    .where(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                    .and(Tables.SSO_S_MOVEPOOL.INIT_STATUS.eq(4))
                    .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate()))
                    .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus))
                    .and(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(dblBagID))
                    .fetch()
            if (mresult.size > 0) {
                lastdepot = mresult.getValue(0, Tables.SSO_S_MOVEPOOL.LASTDEPOT) ?: 0.0
            } else {
                lastdepot = 0.0
            }

            val iDepot: Int = lastdepot.toInt()
            val sDepot = iDepot.toString()
            info = "Depot " + sDepot
            /**
            val presult = dslContext.select()
            .from(Views.sectiondepotlist)
            .where(sectiondepotlist.depotnr.eq(iDepot))
            .fetch()
            val iSection:Int?
            val iPosition:Int?
            if(presult.size>0){
            iSection=presult.getValue(0,section) ?:0
            iPosition=presult.getValue(0,position) ?: 0
            }else{
            iSection=0
            iPosition=0
            }
            val iCountDepots=dslContext.fetchCount(Views.sectiondepotlist,
            section.eq(iSection)
            .and(position.eq(iPosition)))
            val iCountDepotsChecked=dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m")
            .and(Tables.SSO_S_MOVEPOOL.INIT_STATUS.eq(4))
            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate()))
            .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus))
            .and(Tables.SSO_S_MOVEPOOL.LASTDEPOT.in(dslContext.select(DepotNr)
            .from(Views.sectiondepotlist)
            .where(position.eq(iPosition).and(section.eq(iSection))))))

            val sSection=iSection.toString()
            val sPosition=iPosition==1?"a":iPosition==2?"b":""
            if (iCountDepots==iCountDepotsChecked){
            val recMP = dslContext.fetch(Tables.SSO_S_MOVEPOOL, Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m")
            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate()))
            .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus))
            .and(Tables.SSO_S_MOVEPOOL.LASTDEPOT.in(dslContext.select(DepotNr)
            .from(Views.sectiondepotlist)
            .where(position.eq(iPosition).and(section.eq(iSection))))))
            if (recMP != null) {
            for(i in 0..(recMP.size-1)){
            recMP(i).initStatus = 5
            resultCount = recMP(i).update()
            }
            }
            val iCountWork=dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m")
            .and(Tables.SSO_S_MOVEPOOL.INIT_STATUS.eq(5))
            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate()))
            .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(dblStatus))
            .and(Tables.SSO_S_MOVEPOOL.LASTDEPOT.in(dslContext.select(DepotNr)
            .from(Views.sectiondepotlist)
            .where(position.eq(iPosition).and(section.eq(iSection))))))
            if(iCountWork==iCountDepots){
            info+="/r/nStrang ${sSection} ${sPosition} zur Beladung frei"
            }
            else{
            info="Problem bei Freigabe von Strang ${sStrang} ${sPosition}"
            }
            }
            else{
            info+="/r/n/Strang ${sSection} ${sPosition}:${iCountDepotsChecked.toString()}/${iCountDepots.toString()}"
            }
             **/
            return BagResponse(ok, info)
        } catch(e: ServiceException) {
            throw e
        } catch(e: Exception) {

            logHistoryRepository.save(
                    depotId = isBagOk,
                    info = e.message ?: e.toString(),
                    msgLocation = isBagOk,
                    orderId = "")
            throw BadRequestException(e.message)
        }
    }

}
