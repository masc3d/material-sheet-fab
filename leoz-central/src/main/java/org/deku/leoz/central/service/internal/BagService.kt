package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.repository.HistoryJooqRepository
import org.deku.leoz.node.rest.ServiceException
import org.deku.leoz.service.internal.BagService.ErrorCode
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.auth.ApiKey
import sx.time.toDate
import sx.time.toSqlDate
import sx.time.toTimestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.BadRequestException
import javax.ws.rs.Path
import org.deku.leoz.central.data.repository.DepotJooqRepository
import org.deku.leoz.central.data.repository.ParcelJooqRepository
import org.deku.leoz.central.data.repository.toBagStatus
import org.deku.leoz.model.UnitNumber
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.BagService
import org.deku.leoz.service.internal.entity.BagDiff
import org.deku.leoz.service.internal.entity.BagInitRequest
import org.deku.leoz.service.internal.entity.BagNumberRange
import org.deku.leoz.service.internal.entity.BagResponse
import org.deku.leoz.service.internal.entity.SectionDepotsLeft
import org.deku.leoz.service.pub.RoutingService
import org.deku.leoz.time.toShortTime
import sx.rs.DefaultProblem
import sx.time.toLocalDate

/**
 * Bundle service (leoz-central)
 * Created by masc on 01/11/2016.
 **/
@Named
@Path("internal/v1/bag")
class BagService : BagService {
    //private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    @Inject
    private lateinit var logHistoryRepository: HistoryJooqRepository

    @Inject
    private lateinit var depotRepository: DepotJooqRepository

    @Inject
    private lateinit var routingService: org.deku.leoz.node.service.pub.RoutingService

    @Inject
    private lateinit var parcelRepository: ParcelJooqRepository

    fun getNextDeliveryDate(sendDate: Date, stationDest: String, countryDest: String, zipDest: String): java.time.LocalDate {
        //return java.time.LocalDate.now().plusDays(1)
        val routingRequest = RoutingService.Request(sendDate = ShortDate(sendDate.toTimestamp()),
                desiredDeliveryDate = null,
                services = null,
                weight = null,
                sender = null,
                consignee = RoutingService.Request.Participant(
                        country = countryDest,
                        zip = zipDest,
                        timeFrom = null,
                        timeTo = null,
                        desiredStation = stationDest
                )
        )
        val routing = routingService.request(routingRequest)
        return routing.deliveryDate!!.date.toLocalDate()
    }

    fun getWorkingDate(): java.time.LocalDate {
        return java.time.LocalDateTime.now().minusHours((6)).toLocalDate()
    }

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

        // TODO: apply UnitNumber class which include checkdigit verification

//        if (!checkCheckDigit(bagId)) {
//            throw ServiceException(ErrorCode.BAG_ID_WRONG_CHECK_DIGIT)
//        }
//
//        if (!checkCheckDigit(whiteSeal)) {
//            throw ServiceException(ErrorCode.WHITE_SEAL_WRONG_CHECK_DIGIT)
//        }
//        if (!checkCheckDigit(yellowSeal)) {
//            throw ServiceException(ErrorCode.YELLOW_SEAL_WRONG_CHECK_DIGIT)
//        }

        // TODO
        // TODO: define constants for repetitive strings (eg. "initBag", "isBagFree")
        val initBag = "initBag"

        try {
            var workDate: LocalDate = getWorkingDate()
            val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN)
                    .where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1))
                    .and(Tables.TBLHUBLINIENPLAN.ARBEITSDATUM.equal(workDate.toTimestamp())).fetch()
            //val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN).where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1)).fetch()
            if (result.getValue(0, 0) == 0) {
                //workDate=nextWerktag(workDate.addDays(-1),"100","DE","36285")
                workDate = getNextDeliveryDate(workDate.plusDays(-1).toDate(), "100", "DE", "36285")
            } else {
                //nach Feierabend und Tagesabschluss schon die bags für den nächsten Tag initialisieren oder am Wochenende
                //workDate=nextwerktag(workDate,"100","DE","36285"
                workDate = getNextDeliveryDate(workDate.toDate(), "100", "DE", "36285")
            }
            val status: Double = 5.0
            //val dt:java.util.Date=workDate.toDate()
            val dt = java.sql.Date.valueOf(workDate)


            var resultCount = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,
                    Tables.SSO_S_MOVEPOOL.LASTDEPOT.eq(depotNr.toDouble())
                            .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(status))
                            .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt)))
            if (resultCount > 0) {
                throw ServiceException(ErrorCode.BAG_FOR_DEPOT_ALREADY_EXISTS)
            }


            val now = Date()

            val bagID_double = bagId.substring(0, 11).toDouble()
            val whiteSeal_double = whiteSeal.substring(0, 11).toDouble()
            val yellowSeal_double = yellowSeal.substring(0, 11).toDouble()
            val null_double: Double? = null

            val bagID_string = bagId.substring(0, 11)
            val yellowSeal_string = yellowSeal.substring(0, 11)
            val whiteSeal_string = whiteSeal.substring(0, 11)


            val movePool = dslContext.fetchOne(Tables.SSO_S_MOVEPOOL, Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagID_double))
            if (movePool == null) {
                logHistoryRepository.save(
                        depotId = initBag,
                        info = "BagID: ${bagID_string} not found",
                        msgLocation = initBag,
                        orderId = depotNr.toString())

                throw ServiceException(ErrorCode.UPDATE_MOVEPOOL_FAILED)
            }
            movePool.orderdepot2hub = null_double
            movePool.orderhub2depot = null_double
            movePool.sealNumberGreen = whiteSeal_double
            movePool.sealNumberYellow = yellowSeal_double
            movePool.status = status
            movePool.statusTime = now.toTimestamp()
            movePool.initStatus = 1
            movePool.lastdepot = depotNr.toDouble()
            movePool.workDate = dt
            movePool.movepool = "m"
            resultCount = movePool.update()

            if (resultCount == 0) {

                logHistoryRepository.save(
                        depotId = initBag,
                        info = "BagID: ${bagID_string}; YellowSeal: ${yellowSeal_string}; WhiteSeal: ${whiteSeal_string}",
                        msgLocation = initBag,
                        orderId = depotNr.toString())

                throw ServiceException(ErrorCode.UPDATE_MOVEPOOL_FAILED)
            }

            val depotliste = dslContext.fetchOne(Tables.TBLDEPOTLISTE, Tables.TBLDEPOTLISTE.DEPOTNR.eq(depotNr.toInt()))
            if (depotliste == null) {
                logHistoryRepository.save(
                        depotId = initBag,
                        info = "DepotNr not found",
                        msgLocation = initBag,
                        orderId = depotNr.toString())

                throw ServiceException(ErrorCode.UPDATE_DEPOTLIST_FAILED)
            }
            depotliste.strangdatum = dt.toTimestamp()
            depotliste.update()


            val white = dslContext.newRecord(Tables.SSO_P_MOV)
            white.plombennummer = whiteSeal_double
            white.status = 2.0
            white.statuszeit = now.toTimestamp()
            white.lastdepot = depotNr.toDouble()
            white.farbe = "weiss"
            resultCount = white.store()

            if (resultCount == 0) {


                logHistoryRepository.save(
                        depotId = initBag,
                        info = "WhiteSeal: ${whiteSeal_string}",
                        msgLocation = initBag,
                        orderId = depotNr.toString())

                throw ServiceException(ErrorCode.INSERT_SEAL_MOVE_WHITE_FAILED)
            }


            val yellow = dslContext.newRecord(Tables.SSO_P_MOV)
            yellow.plombennummer = yellowSeal_double
            yellow.status = 2.0
            yellow.statuszeit = now.toTimestamp()
            yellow.lastdepot = depotNr.toDouble()
            yellow.farbe = "gelb"
            resultCount = yellow.store()

            if (resultCount == 0) {

                logHistoryRepository.save(
                        depotId = initBag,
                        info = "YellowSeal: ${yellowSeal_string}",
                        msgLocation = initBag,
                        orderId = depotNr.toString())
                throw ServiceException(ErrorCode.INSERT_SEAL_MOVE_YELLOW_FAILED)
            }
            return true
        } catch (e: ServiceException) {
            throw e
        } catch (e: Exception) {

            logHistoryRepository.save(
                    depotId = initBag,
                    info = e.message ?: e.toString(),
                    msgLocation = initBag,
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

        // TODO: apply UnitNumber class which include checkdigit verification

//        if (!checkCheckDigit(bagId)) {
//            throw ServiceException(ErrorCode.BAG_ID_WRONG_CHECK_DIGIT)
//        }

        //TODO

        val isBagFree = "isBagFree"
        try {
            var workDate: LocalDate = getWorkingDate()
            val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN)
                    .where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1))
                    .and(Tables.TBLHUBLINIENPLAN.ARBEITSDATUM.equal(workDate.toTimestamp()))
                    .fetch()



            if (result.getValue(0, 0) == 0) {
                //workDate=nextWerktag(workDate.addDays(-1),"100","DE","36285")
                workDate = getNextDeliveryDate(workDate.plusDays(-1).toDate(), "100", "DE", "36285")
            } else {
                //nach Feierabend und Tagesabschluss schon die bags für den nächsten Tag initialisieren oder am Wochenende
                //workDate=nextwerktag(workDate,"100","DE","36285"
                workDate = getNextDeliveryDate(workDate.toDate(), "100", "DE", "36285")
            }
            val status: Double = 1.0
            //val dt:java.util.Date=workDate.toDate()
            val dt: Date = workDate.toDate()

            val bagID_double = bagId.substring(0, 11).toDouble()
            //val bagID_string = bagId.substring(0, 11)


            var resultCount = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,
                    Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagID_double)
                            .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(status))
                            .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("p"))
                            .and(Tables.SSO_S_MOVEPOOL.MULTIBAG.equal(0)))
            if (resultCount > 0) {
                return true
            }

            resultCount = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,
                    Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagID_double)
                            .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate())))
            if (resultCount > 0) {
                //dieser Bag wurde bereits initialisiert
                throw ServiceException(ErrorCode.BAG_ALREADY_INITIALZED)
            }
            //:org.jooq.Result<SsoSMovepoolRecord>
//Tables.SSO_S_MOVEPOOL.ORDERDEPOT2HUB
            val dResult = dslContext.select()
                    .from(Tables.SSO_S_MOVEPOOL)
                    .where(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagID_double))
                    .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                    .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.ne(dt.toSqlDate()))
                    .fetch()

            if (dResult.size > 0) {
                val orderIdDepot2Hub: Double = dResult.getValue(0, Tables.SSO_S_MOVEPOOL.ORDERDEPOT2HUB) ?: 0.0
                if (orderIdDepot2Hub > 0) {

                    val order = dslContext.fetchOne(Tables.TBLAUFTRAG, Tables.TBLAUFTRAG.ORDERID.eq(orderIdDepot2Hub))
                    if (order != null) {

                        order.lockflag = 4
                        order.sdgstatus = "L"
                        order.update()
                    }
                }
            }


            val free = dslContext.fetchOne(Tables.SSO_S_MOVEPOOL, Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagID_double))
            if (free != null) {
                free.movepool = "p"
                free.status = status
                free.printed = -1.0
                free.multibag = 0
                resultCount = free.update()
            } else {
                resultCount = 0
            }

            if (resultCount < 1) {

                logHistoryRepository.save(
                        depotId = isBagFree,
                        info = "Problem beim update sso_s_movepool",
                        msgLocation = isBagFree,
                        orderId = depotNr.toString())
                return false
            }


            return true
        } catch (e: ServiceException) {
            throw e
        } catch (e: Exception) {

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

            var tmp: String
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
            //l = listOf("Hallo", "Test")
            //findSectionDepots
            l = depotRepository.findSectionDepots(section, position)
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
        section ?:
                throw ServiceException(ErrorCode.SECTION_MISSING)
        position ?:
                throw ServiceException(ErrorCode.POSITION_MISSING)

        val sGetSectionDepotsLeft = "getSectionDepotsLeft"
        try {


            var workDate: LocalDate = getWorkingDate()
            val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN)
                    .where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1))
                    .and(Tables.TBLHUBLINIENPLAN.ARBEITSDATUM.equal(workDate.toTimestamp()))
                    .fetch()
            if (result.getValue(0, 0) == 0) {
                //workDate=nextWerktag(workDate.addDays(-1),"100","DE","36285")
                workDate = getNextDeliveryDate(workDate.plusDays(-1).toDate(), "100", "DE", "36285")
            } else {
                //nach Feierabend und Tagesabschluss schon die bags für den nächsten Tag initialisieren oder am Wochenende
                //workDate=nextwerktag(workDate,"100","DE","36285"
                workDate = getNextDeliveryDate(workDate.toDate(), "100", "DE", "36285")
            }
            //val dt:java.util.Date=workDate.toDate()
            val dt: Date = workDate.toDate()
            val movepoolStatus: Double = 5.0

            val resultCount = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL.innerJoin(Tables.SECTIONDEPOTLIST)
                    .on(Tables.SSO_S_MOVEPOOL.LASTDEPOT.coerce(Int::class.java).eq(Tables.SECTIONDEPOTLIST.DEPOTNR),
                            Tables.SSO_S_MOVEPOOL.STATUS.eq(movepoolStatus)
                                    .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                                    .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate()))
                                    .and(Tables.SECTIONDEPOTLIST.SECTION.eq(section.toLong()))
                                    .and(Tables.SECTIONDEPOTLIST.POSITION.eq(position))
                    )
            )
            /**.and(Tables.SSO_S_MOVEPOOL.LASTDEPOT.in(select(Depotnr).from(Views.sectiondepotlist)
            .where(section.eq(iSection)
            .and(position.eq(iPosition))))
             **/
            val l: List<String>
            //l = listOf("Hallo", "Test")


            /**
            l=dslConext.select(DEPOT)
            .from(Views.sectiondepotlist)
            .where(section.eq(iSection)
            .and(position.eq(iPosition)
            .and(depotnr.ni(select lastdepot from sso_s_movepool where movepool='m' and status=5 and work_date=))
            .fetchInto(String::class.java)
             **/
            l = dslContext.select(Tables.SECTIONDEPOTLIST.DEPOT)
                    .from(Tables.SECTIONDEPOTLIST)
                    .where(Tables.SECTIONDEPOTLIST.SECTION.eq(section.toLong()))
                    .and(Tables.SECTIONDEPOTLIST.POSITION.eq(position))
                    .and(Tables.SECTIONDEPOTLIST.DEPOTNR.notIn(dslContext.select(Tables.SSO_S_MOVEPOOL.LASTDEPOT.coerce(Int::class.java)).from(Tables.SSO_S_MOVEPOOL)
                            .where(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m")
                                    .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(movepoolStatus))
                                    .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.eq(dt.toSqlDate())))))
                    .fetchInto(String::class.java)

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

        // TODO: apply UnitNumber class which include checkdigit verification

//        if (!checkCheckDigit(bagId)) {
//            throw ServiceException(ErrorCode.BAG_ID_WRONG_CHECK_DIGIT)
//        }
//        if (!checkCheckDigit(unitNo)) {
//            throw ServiceException(ErrorCode.BAG_UNITNO_WRONG_CHECK_DIGIT)
//        }
        var ok = false
        val info: String?
        val isBagOk = "isBagOk"
        try {
            var workDate: LocalDate = getWorkingDate()
            val result = dslContext.selectCount().from(Tables.TBLHUBLINIENPLAN)
                    .where(Tables.TBLHUBLINIENPLAN.ISTLIFE.equal(-1))
                    .and(Tables.TBLHUBLINIENPLAN.ARBEITSDATUM.equal(workDate.toTimestamp()))
                    .fetch()
            if (result.getValue(0, 0) == 0) {
                //workDate=nextWerktag(workDate.addDays(-1),"100","DE","36285")
                workDate = getNextDeliveryDate(workDate.plusDays(-1).toDate(), "100", "DE", "36285")
            } else {
                //nach Feierabend und Tagesabschluss schon die bags für den nächsten Tag initialisieren oder am Wochenende
                //workDate=nextwerktag(workDate,"100","DE","36285"
                workDate = getNextDeliveryDate(workDate.toDate(), "100", "DE", "36285")
            }
            //val dt:java.util.Date=workDate.toDate()
            val dt: Date = workDate.toDate()

            val bagID_double = bagId.substring(0, 11).toDouble()

            val bagID_string = bagId.substring(0, 11)
            val dblUnitNo = unitNo.substring(0, 11).toDouble()
            val sUnitNo = unitNo.substring(0, 11)

            val status = 5.0

            var resultCount = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,
                    Tables.SSO_S_MOVEPOOL.STATUS.eq(status)
                            .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate()))
                            .and(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagID_double))
                            .and(Tables.SSO_S_MOVEPOOL.ORDERHUB2DEPOT.eq(
                                    dslContext.select(Tables.TBLAUFTRAGCOLLIES.ORDERID)
                                            .from(Tables.TBLAUFTRAGCOLLIES)
                                            .where(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR.eq(dblUnitNo)))))
            if (resultCount <= 0) {
                throw ServiceException(ErrorCode.NO_DATA)
            }
            val recOk = dslContext.fetchOne(Tables.SSO_S_MOVEPOOL, Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagID_double)
                    .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                    .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate()))
                    .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(status))

                    .and(Tables.SSO_S_MOVEPOOL.ORDERHUB2DEPOT.eq(
                            dslContext.select(Tables.TBLAUFTRAGCOLLIES.ORDERID)
                                    .from(Tables.TBLAUFTRAGCOLLIES)
                                    .where(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR.eq(dblUnitNo)))))
            if (recOk != null) {
                recOk.initStatus = 4
                recOk.update()
            }


            resultCount = dslContext.fetchCount(Tables.SSO_S_MOVEPOOL,
                    Tables.SSO_S_MOVEPOOL.STATUS.eq(status)
                            .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate()))
                            .and(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagID_double))
                            .and(Tables.SSO_S_MOVEPOOL.INIT_STATUS.eq(4))
                            .and(Tables.SSO_S_MOVEPOOL.ORDERHUB2DEPOT.eq(
                                    dslContext.select(Tables.TBLAUFTRAGCOLLIES.ORDERID)
                                            .from(Tables.TBLAUFTRAGCOLLIES)
                                            .where(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR.eq(dblUnitNo)))))
            if (resultCount <= 0) {
                logHistoryRepository.save(
                        depotId = isBagOk,
                        info = "set Init_status=4 BagID: ${bagID_string}",
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
                    .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(status))
                    .and(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bagID_double))
                    .fetch()
            if (mresult.size > 0) {
                lastdepot = mresult.getValue(0, Tables.SSO_S_MOVEPOOL.LASTDEPOT) ?: 0.0
            } else {
                lastdepot = 0.0
            }

            val depot: Int = lastdepot.toInt()
            //val depot_string = depot.toString()
            //info = "Depot " + depot_string
            info = "Depot " + depot.toString()
            /**
            val presult = dslContext.select()
            .from(Views.sectiondepotlist)
            .where(sectiondepotlist.depotnr.eq(depot))
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
            .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(status))
            .and(Tables.SSO_S_MOVEPOOL.LASTDEPOT.in(dslContext.select(DepotNr)
            .from(Views.sectiondepotlist)
            .where(position.eq(iPosition).and(section.eq(iSection))))))

            val sSection=iSection.toString()
            val sPosition=iPosition==1?"a":iPosition==2?"b":""
            if (iCountDepots==iCountDepotsChecked){
            val recMP = dslContext.fetch(Tables.SSO_S_MOVEPOOL, Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m")
            .and(Tables.SSO_S_MOVEPOOL.WORK_DATE.equal(dt.toSqlDate()))
            .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(status))
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
            .and(Tables.SSO_S_MOVEPOOL.STATUS.eq(status))
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
        } catch (e: ServiceException) {
            throw e
        } catch (e: Exception) {

            logHistoryRepository.save(
                    depotId = isBagOk,
                    info = e.message ?: e.toString(),
                    msgLocation = isBagOk,
                    orderId = "")
            throw BadRequestException(e.message)
        }
    }

    override fun getDiff(): List<BagDiff> {
        val gDiff = "getDiff"
        var diff: List<BagDiff>
        try {
            diff = mutableListOf()
            //diff=listOf()

            val runresult = dslContext.select()
                    .from(Tables.USYSTBLZAEHLER)
                    .where(Tables.USYSTBLZAEHLER.COUNTERTYP.eq(43))
                    .fetch()
            if (runresult.size < 1) {
                throw ServiceException(ErrorCode.NO_RUN_ID)
            }
            var runid = runresult.getValue(0, Tables.USYSTBLZAEHLER.TAGESZAEHLER) ?: 0
            if (runid == 0) {
                throw ServiceException(ErrorCode.NO_RUN_ID)
            }
            runid -= 1

            //diff
            val diffresult = dslContext.select(Tables.SSO_CHECK.COLLIEBELEGNR, //.as("unitno"),
                    Tables.SSO_CHECK.STRANG, //.as("section"),
                    Tables.SSO_CHECK.LD, //.as("deliverydate"),
                    Tables.SSO_CHECK.BEMERKUNG, //.as("notice"),
                    Tables.SSO_CHECK.ENTLADEDELTAMIN)//.as("delta")
                    .from(Tables.SSO_CHECK)
                    .where(Tables.SSO_CHECK.RUN.eq((runid)))
                    .orderBy(Tables.SSO_CHECK.ENTLADEDELTAMIN.desc())
                    .fetch()
            //  .fetchInto(BagDiff)
            if (diffresult.size < 1) {
                throw ServiceException(ErrorCode.NO_DATA_TO_RUN_ID)
            }

            for (i in 0..diffresult.size - 1) {
                val bd = BagDiff(diffresult.getValue(i, Tables.SSO_CHECK.COLLIEBELEGNR),
                        diffresult.getValue(i, Tables.SSO_CHECK.STRANG),
                        diffresult.getValue(i, Tables.SSO_CHECK.LD),
                        diffresult.getValue(i, Tables.SSO_CHECK.BEMERKUNG),
                        diffresult.getValue(i, Tables.SSO_CHECK.ENTLADEDELTAMIN))
                diff.add(bd)
            }


            return diff
        } catch (e: ServiceException) {
            throw e
        } catch (e: Exception) {
            logHistoryRepository.save(
                    depotId = gDiff,
                    info = e.message ?: e.toString(),
                    msgLocation = gDiff,
                    orderId = "")
            throw BadRequestException(e.message)
        }
    }

    override fun lineArrival(scanId: String?): BagResponse {
        if (scanId == null || scanId.isEmpty()) {
            throw ServiceException(ErrorCode.SCAN_ID_MISSING)
        }
        var lineScanId = scanId
        if (lineScanId.length > 12) {
            lineScanId = lineScanId.substring(0, 12)
        }
        if (lineScanId.length < 12) {
            lineScanId = lineScanId.padStart(12, '0')
        }

        // TODO: remove support for check digit place holder, apply UnitNumber class which include checkdigit verification
//        if (!lineScanId.endsWith(',')) {
//            if (!checkCheckDigit(lineScanId)) {
//                throw ServiceException(ErrorCode.SCAN_ID_WRONG_CHECK_DIGIT)
//            }
//        }
        lineScanId = lineScanId.substring(0, 11)
        var ok = false
        var info: String?
        val logLineArrival = "LineArriva"//"LineArrival" schneidet nicht ab!! 10 zeichen
        val lineVersion = -1
        val logLineArrivalLocation = "KfzMng"
        var color: String
        try {
            val line: Int
            val lineR = dslContext.select(Tables.TBLHUBLINIEN.LINIENNR)
                    .from(Tables.TBLHUBLINIEN)
                    .where(Tables.TBLHUBLINIEN.VERSION.eq(lineVersion))
                    .and(Tables.TBLHUBLINIEN.SCANID.eq(lineScanId.toDouble()))
                    .fetch()//?.getValue(0, Tables.TBLHUBLINIEN.LINIENNR)
            if (lineR == null) {
                logHistoryRepository.save(
                        depotId = logLineArrival,
                        info = "Problem: Linie=${lineScanId}",
                        msgLocation = logLineArrival,
                        orderId = "")
                throw ServiceException(ErrorCode.SCAN_ID_NOT_VALID)
            } else {
                if (lineR.size > 0) {
                    line = lineR.getValue(0, Tables.TBLHUBLINIEN.LINIENNR)
                } else {
                    logHistoryRepository.save(
                            depotId = logLineArrival,
                            info = "Problem: Linie=${lineScanId}",
                            msgLocation = logLineArrival,
                            orderId = "")
                    throw ServiceException(ErrorCode.SCAN_ID_NOT_VALID)
                }
            }
            if (line <= 0) {
                logHistoryRepository.save(
                        depotId = logLineArrival,
                        info = "Problem: Linie=${lineScanId}",
                        msgLocation = logLineArrivalLocation,
                        orderId = "")
                info = "ungültige Liniennr"
                return BagResponse(ok, info)
            }
            /*
            val lineCount = dslContext.fetchCount(Tables.TBLHUBLINIEN,
                    Tables.TBLHUBLINIEN.VERSION.eq(lineVersion)
                            .and(Tables.TBLHUBLINIEN.LINIENNR.eq(line)))
            if (lineCount == 0) {
                logHistoryRepository.save(
                        depotId = logLineArrival,
                        info = "Problem: Linie=${lineScanId}",
                        msgLocation = logLineArrivalLocation,
                        orderId = "")
                info = "ungültige Liniennr"
                return BagResponse(ok, info)
            }
            */
            ok = true
            val recLine = dslContext.fetchOne(Tables.TBLHUBLINIEN, Tables.TBLHUBLINIEN.KFZHUBEINGANG.isNull
                    .and(Tables.TBLHUBLINIEN.VERSION.eq(lineVersion))
                    .and(Tables.TBLHUBLINIEN.LINIENNR.eq(line)))
            if (recLine != null) {
                val dt = Date()
                recLine.kfzhubeingang = dt.toTimestamp()
                recLine.update()
                val sdf = SimpleDateFormat("yyyMMddHHmmss")
                val f = sdf.format(dt)
                //grün
                color = "green"
                logHistoryRepository.save(
                        depotId = logLineArrival,
                        info = "Linie=${lineScanId} KfzHubEingang=${f}",
                        msgLocation = logLineArrivalLocation,
                        orderId = line.toString())
            } else {
                //schon gescannt -->gelb
                color = "yellow"
            }
            info = "Ankunft Linie ${line} ok"

            //#240 feld packgew Pkst / Gew in hubkfzankunft: "283/1210" + Entladung=0
            val s = UInteger.valueOf(134217728)


            val unitCount = dslContext.fetchCount(Tables.TBLAUFTRAGCOLLIES.innerJoin(Tables.TBLAUFTRAG)
                    .on(Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(Tables.TBLAUFTRAG.ORDERID)),
                    Tables.TBLAUFTRAGCOLLIES.BELADELINIE.eq(line.toDouble())
                            .and(Tables.TBLAUFTRAG.LOCKFLAG.eq(0))
                            .and(Tables.TBLAUFTRAG.SERVICE.bitAnd(s).eq(UInteger.valueOf(0)))
                            //.andNot(Tables.TBLAUFTRAG.SERVICE.bitAnd(s))
                            //.and(Tables.TBLAUFTRAG.SERVICE.bitAnd(s).eq(0)))
                            .and(Tables.TBLAUFTRAGCOLLIES.DTEINGANGHUP3.isNull))


            val unitWeight = dslContext.select(Tables.TBLAUFTRAGCOLLIES.GEWICHTREAL?.sum()?.round())
                    .from(Tables.TBLAUFTRAGCOLLIES.innerJoin(Tables.TBLAUFTRAG).on(Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(Tables.TBLAUFTRAG.ORDERID)))
                    .where(Tables.TBLAUFTRAGCOLLIES.BELADELINIE.eq(line.toDouble()))
                    .and(Tables.TBLAUFTRAG.LOCKFLAG.eq(0))
                    .and(Tables.TBLAUFTRAG.SERVICE.bitAnd(s).eq(UInteger.valueOf(0)))
                    .and(Tables.TBLAUFTRAGCOLLIES.DTEINGANGHUP3.isNull).fetch()?.getValue(0, Tables.TBLAUFTRAGCOLLIES.GEWICHTREAL?.sum()?.round(0)) ?: 0


            var weight = "${unitCount.toString()}/${unitWeight.toString()}"
            val ui = UInteger.valueOf(line)

            val rec = dslContext.fetchOne(Tables.HUBFAHRZEUGBELADUNG, Tables.HUBFAHRZEUGBELADUNG.LINIE.eq(ui))
            if (rec != null) {
                rec.angekommen = -1
                rec.entladung = 0.0
                rec.packgew = weight
                val i = rec.update()
                if (i > 0) {
                    logHistoryRepository.save(
                            depotId = logLineArrival,
                            info = "Linie=${lineScanId} angekommen=-1",
                            msgLocation = logLineArrivalLocation,
                            orderId = line.toString())
                }
            }



            return BagResponse(ok, info, color)
        } catch (e: ServiceException) {
            throw e
        } catch (e: Exception) {

            logHistoryRepository.save(
                    depotId = logLineArrival,
                    info = e.message ?: e.toString(),
                    msgLocation = logLineArrivalLocation,
                    orderId = "")
            throw BadRequestException(e.message)
        }
    }

    override fun bagIn(unitNo: String?, sealNo: String?): BagResponse {
        if (unitNo == null || unitNo.isEmpty()) {
            throw ServiceException(ErrorCode.BAG_UNITNO_MISSING)
        }

        if (sealNo == null || sealNo.isEmpty()) {
            throw ServiceException(ErrorCode.YELLOW_SEAL_MISSING)
        }
        var bagUnitNo = unitNo
        if (bagUnitNo.length > 12) {
            bagUnitNo = bagUnitNo.substring(0, 12)
        }
        if (bagUnitNo.length < 12) {
            bagUnitNo = bagUnitNo.padStart(12, '0')
        }
//        if (!bagUnitNo.endsWith(',')) {
//            if (!checkCheckDigit(bagUnitNo)) {
//                throw ServiceException(ErrorCode.BAG_UNITNO_WRONG_CHECK_DIGIT)
//            }
//        }
        bagUnitNo = bagUnitNo.substring(0, 11)
        var bagSealNo = sealNo
        if (bagSealNo.length > 12) {
            bagSealNo = bagSealNo.substring(0, 12)
        }
        if (bagSealNo.length < 12) {
            bagSealNo = bagSealNo.padStart(12, '0')
        }

        // TODO: remove support for check digit place holder, apply UnitNumber class which include checkdigit verification
//        if (!bagSealNo.endsWith(',')) {
//            if (!checkCheckDigit(bagSealNo)) {
//                throw ServiceException(ErrorCode.YELLOW_SEAL_WRONG_CHECK_DIGIT)
//            }
//        }

        //bagSealNo = bagSealNo.substring(0, 11)
        var ok = false
        var info: String? = null
        val logIn = "BagIn"// schneidet nicht ab!! 10 zeichen
        val logInLocation = "BagIn"
        var color = "red"
        try {
            if (bagUnitNo.toLong() >= 10071000000 && bagUnitNo.toLong() < 10072000000) {
                info = "Rück-Label scannen"
                return BagResponse(ok, info, color)
            }
            //ToDO unload...
            return BagResponse(ok, info, color)
        } catch (e: ServiceException) {
            throw e
        } catch (e: Exception) {

            logHistoryRepository.save(
                    depotId = logIn,
                    info = e.message ?: e.toString(),
                    msgLocation = logInLocation,
                    orderId = "")
            throw BadRequestException(e.message)
        }
    }

    override fun getCount2SendBackByStation(stationNo: Int): Int {
        return depotRepository.getCountBags2SendBagByStation(stationNo)
    }

    override fun getStatus(bagId: Long): BagService.BagStatus {
        val un = UnitNumber.parseLabel(bagId.toString())
        if (un.hasError)
            throw ServiceException(ErrorCode.BAG_ID_NOT_VALID)
        val bagNo = un.value.value.toLong()
        if(un.value.type!=UnitNumber.Type.BagId)
            throw ServiceException(ErrorCode.BAG_ID_NOT_VALID)
        val statusrecord = depotRepository.getBagStatus(bagNo)
        statusrecord?:throw ServiceException(ErrorCode.BAG_ID_NOT_VALID)
        return statusrecord.toBagStatus()
    }

    override fun isPickupStation(stationNo: Int, orderID: Long): Boolean {
        var ret=false
        val order=parcelRepository.getOrderById(orderID)
        order?:throw DefaultProblem(title="Order not found")
        if(order.depotnrabd==stationNo)
            ret=true
        return ret
    }


}
