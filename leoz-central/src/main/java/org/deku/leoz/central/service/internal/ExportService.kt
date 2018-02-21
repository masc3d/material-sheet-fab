package org.deku.leoz.central.service.internal

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Routines
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.SsoSMovepoolRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TblauftragRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TblauftragcolliesRecord
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.model.*
import org.deku.leoz.service.entity.DayTypeKey
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.*
import org.deku.leoz.service.internal.BagService
import org.deku.leoz.service.internal.ExportService
import org.deku.leoz.service.internal.UserService
import org.deku.leoz.service.pub.RoutingService
import org.deku.leoz.time.toShortTime
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional
import sx.rs.RestProblem
import sx.time.toLocalDate
import sx.time.toTimestamp
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import org.deku.leoz.central.rest.authorizedUser

@Named
@Path("internal/v1/export")
open class ExportService : org.deku.leoz.service.internal.ExportService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var userService: UserService

    @Inject
    private lateinit var bagService: BagService

    @Inject
    private lateinit var stationRepository: JooqStationRepository

    @Inject
    private lateinit var parcelRepository: JooqParcelRepository

    @Inject
    private lateinit var routingService: org.deku.leoz.node.service.pub.RoutingService

    @Inject
    private lateinit var stationService: StationService

    @Inject
    private lateinit var bagServiceCentral: org.deku.leoz.central.service.internal.BagService

    @Inject
    private lateinit var statusRepository: JooqStatusRepository

    @Context
    private lateinit var httpRequest: HttpServletRequest

    data class ExportUnitOrder(val unit: TblauftragcolliesRecord, val order: TblauftragRecord)

    @Transactional(PersistenceConfiguration.QUALIFIER)
    override fun export(scanCode: String, loadingListNo: String, stationNo: Int): String {

        //check stationNo in user-stationsAllowed

        val un = DekuUnitNumber.parseLabel(loadingListNo)
        when {
            un.hasError -> {
                throw RestProblem(
                        status = Response.Status.NOT_FOUND,
                        title = ExportService.ResponseMsg.LL_WRONG_CHECKDIGIT.value //"Loadinglist - wrong check digit"
                )
            }
        }
        if (un.value.type != UnitNumber.Type.Parcel)

            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = ExportService.ResponseMsg.LL_NOT_VALID.value//"Loadinglist not valid"
            )


        val exportUnitOrder = getAndCheckUnit(scanCode, stationNo)

        val unitRecord = exportUnitOrder.unit
        val orderRecord = exportUnitOrder.order

        if (unitRecord.verpackungsart == 91) {//verpackungsart=Valore
            val station = stationService.getByStationNo(stationNo)
            if (!station.exportValuablesAllowed) {
                throw RestProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = ExportService.ResponseMsg.VAL_NOT_ALLOWED.value//"Valuables not allowed"
                )
            }
            if (!station.exportValuablesWithoutBagAllowed) {
                throw RestProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = ExportService.ResponseMsg.VAL_NOT_ALLOWED_WITHOUT_BAG.value //"Valuables not allowed without bag"
                )
            }
        }
        var title = ""//""Ok"
        if (unitRecord.ladelistennummerd == null) {
        } else if (unitRecord.ladelistennummerd.toLong() == un.value.value.toLong()) {
            //doppelt gescannt
            throw RestProblem(
                    status = Response.Status.OK,
                    title = ExportService.ResponseMsg.PARCEL_ALREADY_SCANNED.value//"Parcel already scanned"
            )
        } else {
            //umbuchen auf andere ladeliste
            title = ExportService.ResponseMsg.LL_CHANGED.value//"Loadinglist changed"

        }

        exportUnit(ExportUnitOrder(unitRecord, orderRecord), un.value.value.toLong(), stationNo)

        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return mapper.writeValueAsString(title)
    }

    override fun getLoadedParcelsToExportByStationNo(stationNo: Int, sendDate: Date?): List<ExportService.Order> {

        val orders = if (sendDate != null) parcelRepository.getOrdersToExportByStation(stationNo, sendDate.toLocalDate()) else parcelRepository.getOrdersToExportByStation(station = stationNo)
        if (orders.count() == 0)
            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = ExportService.ResponseMsg.NO_ORDERS_FOUND.value//"No orders found"
            )
        var allParcels = parcelRepository.getLoadedParcelsToExportByOrderids(orders.map { it.orderid.toLong() }.toList()).groupBy { it.orderid }
        if (allParcels.count() == 0)
            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = ExportService.ResponseMsg.NO_PARCELS_FOUND.value//"No parcels found"
            )


        return orders.map {
            it.toOrderToExport().also { order ->
                order.parcels = allParcels
                        .getOrDefault(order.orderId.toDouble(), listOf())
                        .map { it.toParcelToExport() }
            }
        }.filter { it.parcels.count() > 0 }

    }

    override fun getNewLoadinglistNo(): ExportService.Loadinglist {
        var loadlistNo: Long = 0
        do {
            loadlistNo = Routines.fTan(dsl.configuration(), counter.LOADING_LIST.value) + 300000
            var checklist = parcelRepository.getParcelsByLoadingList(loadlistNo)
        } while (checklist.count() > 0)

        return ExportService.Loadinglist(loadinglistNo = loadlistNo)
    }

    override fun getParcelsToExportByStationNo(stationNo: Int, sendDate: Date?): List<ExportService.Order> {

        val orders = if (sendDate != null) parcelRepository.getOrdersToExportByStation(stationNo, sendDate.toLocalDate()) else parcelRepository.getOrdersToExportByStation(station = stationNo)
        if (orders.count() == 0)
            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = ExportService.ResponseMsg.NO_ORDERS_FOUND.value //"No orders found"
            )


        val allParcels = parcelRepository.getParcelsNotDeliveredByOrderids(orders
                .map { it.orderid.toLong() }
                .toList()
        )
                .groupBy { it.orderid }

        if (allParcels.count() == 0)
            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = ExportService.ResponseMsg.NO_PARCELS_FOUND.value //"No parcels found"
            )


        return orders.map {
            it.toOrderToExport().also { order ->
                order.parcels = allParcels
                        .getOrDefault(order.orderId.toDouble(), listOf())
                        .map { it.toParcelToExport() }
            }
        }.filter { it.parcels.count() > 0 }
    }

    override fun getParcelsToExportInBagByStationNo(stationNo: Int, sendDate: Date?): List<ExportService.Order> {

        val parcels = if (sendDate != null) parcelRepository.getParcelsToExportInBagByStation(stationNo, sendDate = sendDate.toLocalDate()) else parcelRepository.getParcelsToExportInBagByStation(station = stationNo)
        if (parcels.count() == 0)
            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = ExportService.ResponseMsg.NO_PARCELS_FOUND_FOR_STATION.value//"No parcels found for this station"
            )
        return parcels

    }

    override fun getCountToSendBackByStation(stationNo: Int): Int {
        return bagService.getCountToSendBackByStation(stationNo)
    }

    override fun getParcelsToExportByLoadingList(loadinglistNo: String): List<ExportService.Order> {
        val un = DekuUnitNumber.parseLabel(loadinglistNo)
        when {
            un.hasError -> {
                throw RestProblem(
                        status = Response.Status.NOT_FOUND,
                        title = ExportService.ResponseMsg.LL_WRONG_CHECKDIGIT.value//"Wrong check digit"
                )
            }
        }
        if (un.value.type != UnitNumber.Type.Parcel)

            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = ExportService.ResponseMsg.LL_NOT_VALID.value//"Loadinglist not valid"
            )
        val parcels = parcelRepository.getParcelsToExportByLoadingList(un.value.value.toLong())
        if (parcels.count() == 0)
            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = ExportService.ResponseMsg.NO_PARCELS_FOUND_FOR_LL.value//"No parcels found for this list"
            )
        return parcels
    }

    override fun getNewBagLoadinglistNo(): ExportService.Loadinglist {
        var loadlistNo: Long = 0
        do {
            loadlistNo = Routines.fTan(dsl.configuration(), counter.LOADING_LIST.value) + 10000
            var checklist = parcelRepository.getParcelsByLoadingList(loadlistNo)
        } while (checklist.count() > 0)
        return ExportService.Loadinglist(loadinglistNo = loadlistNo)
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    override fun setBagStationExportRedSeal(bagID: String, bagBackUnitNo: String, stationNo: Int, redSeal: String, text: String) {

        val bag = getAndCheckBag(stationNo, bagID)
        val backUnit = bag.unitNoBack
        backUnit ?: throw RestProblem(
                status = Response.Status.CONFLICT,
                title = ExportService.ResponseMsg.BAG_ID_OK_NO_BACK.value //"BagId found - no bagback-unit found"
        )

        if (bag.lastStation == 2 || bag.status == BagStatus.CLOSED_FROM_STATION || bag.status == BagStatus.CLOSED_FROM_HUB) {

            if (statusRepository.statusExist(backUnit, Event.EXPORT_LOADED.creator.toString(), Event.EXPORT_LOADED.concatId, Reason.NORMAL.id)) {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_ID_OK_ALREADY_EXPORTED.value//"BagId found - bag already exported"
                )
            } else {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_ID_OK_ALREADY_CLOSED.value//"BagId found - bag already closed - try to reopen"
                )
            }
        }
        val unBack = DekuUnitNumber.parseLabel(bagBackUnitNo)
        when {
            unBack.hasError -> {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_UNIT_WRONG_CHECKDIGIT.value//"Bag-UnitNo wrong check digit"
                )
            }
        }
        if (unBack.value.type != UnitNumber.Type.BagBack)

            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.BAG_UNIT_NOT_VALID.value//"Bag-UnitNo not valid"
            )

        if (unBack.value.value.toLong() != backUnit) {
            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.BAG_BACK_UNIT_DISMATCH.value//"Bag-BackUnitNo dismatch"
            )
        }
        val unRedSeal = DekuUnitNumber.parseLabel(redSeal)
        when {
            unRedSeal.hasError -> {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.RED_SEAL_WRONG_CHECKDIGIT.value//"Wrong check digit"
                )
            }
        }
        if (unRedSeal.value.type != UnitNumber.Type.ReserveSeal)

            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.RED_SEAL_NOT_VALID.value//"Redseal not valid"
            )
        val recSeal = stationRepository.getSeal(unRedSeal.value.value.toLong())
        recSeal ?: throw RestProblem(
                status = Response.Status.CONFLICT,
                title = ExportService.ResponseMsg.RED_SEAL_NOT_FOUND.value//"RedSeal not found"
        )
        val lastDepot = recSeal.lastdepot
        lastDepot ?: throw RestProblem(
                status = Response.Status.CONFLICT,
                title = ExportService.ResponseMsg.NO_STATION.value//"No station"
        )
        if (lastDepot.toInt() != stationNo) {
            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.LAST_DEPOT_STATION_MISMATCH.value//"Stationmismatch"
            )
        }
        val lastStatus = recSeal.status
        lastStatus ?: throw RestProblem(
                status = Response.Status.CONFLICT,
                title = ExportService.ResponseMsg.SEAL_STATUS_PROBLEM.value//"Sealstatus-problem"
        )
        if (lastStatus.toInt() == 2) {
            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.SEAL_ALREADY_IN_USE.value//"SealNo already in use"
            )
        }
        if (lastStatus.toInt() != 1) {
            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.SEAL_STATUS_PROBLEM.value//"Sealstatus problem"
            )
        }


        val exportUnitOrder = getAndCheckUnit(bagBackUnitNo, stationNo)

        val unitRecord = exportUnitOrder.unit
        val orderRecord = exportUnitOrder.order

        recSeal.status = 2.0
        var t = text
        if (t.length > 200)
            t = t.substring(0, 200)
        recSeal.bemerkung = t
        recSeal.store()

        dsl.update(Tables.SSO_S_MOVEPOOL)
                .set(Tables.SSO_S_MOVEPOOL.SEAL_NUMBER_RED, unRedSeal.value.value.toDouble())
                .where(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bag.bagNumber!!.toDouble())
                        .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                        .and(Tables.SSO_S_MOVEPOOL.LASTDEPOT.eq(stationNo.toDouble()))
                )
                .execute()


        orderRecord.plombennra = unRedSeal.value.value.toDouble()
        orderRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())


        unitRecord.plombennrc = unRedSeal.value.value.toDouble()
        unitRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    override fun reopenBagStationExport(bagID: String, stationNo: Int) {

        val bag = getAndCheckBag(stationNo, bagID)
        val backUnit = bag.unitNoBack
        backUnit ?: throw RestProblem(
                status = Response.Status.CONFLICT,
                title = ExportService.ResponseMsg.BAG_ID_OK_NO_BACK.value//"BagId found - no bagback-unit found"
        )

        if (bag.lastStation == 2 || bag.status == BagStatus.CLOSED_FROM_STATION || bag.status == BagStatus.CLOSED_FROM_HUB) {

            if (statusRepository.statusExist(backUnit, Event.EXPORT_LOADED.creator.toString(), Event.EXPORT_LOADED.concatId, Reason.NORMAL.id)) {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_ID_OK_ALREADY_EXPORTED.value//"BagId found - bag already exported"
                )
            } else {
                //update movepool
                try {
                    dsl.update(Tables.SSO_S_MOVEPOOL)
                            .set(Tables.SSO_S_MOVEPOOL.STATUS, BagStatus.OPENED.value.toDouble())
                            .set(Tables.SSO_S_MOVEPOOL.LASTDEPOT, stationNo.toDouble())
                            .where(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bag.bagNumber!!.toDouble())
                                    .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m")))
                            .execute()
                } catch (e: Exception) {
                    log.error(e.toString())
                    throw RestProblem(
                            status = Response.Status.CONFLICT,
                            title = e.toString()
                    )
                }
            }
        } else {
            throw RestProblem(
                    status = Response.Status.OK,
                    title = ExportService.ResponseMsg.BAG_ID_OK_ALREADY_OPEN.value //"BagId found - already open"
            )
        }


    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    override fun fillBagStationExport(bagID: String, bagBackUnitNo: String, stationNo: Int, unitNo: String, loadingListNo: String, yellowSealNo: String): String {

        val bag = getAndCheckBag(stationNo, bagID)
        val backUnit = bag.unitNoBack
        backUnit ?: throw RestProblem(
                status = Response.Status.CONFLICT,
                title = ExportService.ResponseMsg.BAG_ID_OK_NO_BACK.value//"BagId found - no bagback-unit found"
        )

        if (bag.lastStation == 2 || bag.status == BagStatus.CLOSED_FROM_STATION || bag.status == BagStatus.CLOSED_FROM_HUB) {

            if (statusRepository.statusExist(backUnit, Event.EXPORT_LOADED.creator.toString(), Event.EXPORT_LOADED.concatId, Reason.NORMAL.id)) {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_ID_OK_ALREADY_EXPORTED.value//"BagId found - bag already exported"
                )
            } else {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_ID_OK_ALREADY_CLOSED.value//"BagId found - bag already closed - try to reopen"
                )
            }
        }
        val unBack = DekuUnitNumber.parseLabel(bagBackUnitNo)
        when {
            unBack.hasError -> {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_UNIT_WRONG_CHECKDIGIT.value//"Bag-UnitNo wrong check digit"
                )
            }
        }
        if (unBack.value.type != UnitNumber.Type.BagBack)

            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.BAG_UNIT_NOT_VALID.value//"Bag-UnitNo not valid"
            )

        if (unBack.value.value.toLong() != backUnit) {
            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.BAG_BACK_UNIT_DISMATCH.value//"Bag-BackUnitNo dismatch"
            )
        }

        val un = DekuUnitNumber.parseLabel(loadingListNo)
        when {
            un.hasError -> {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.LL_WRONG_CHECKDIGIT.value//"Loadinglist - wrong check digit"
                )
            }
        }
        if (un.value.type != UnitNumber.Type.Parcel)

            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.LL_NOT_VALID.value//"Loadinglist not valid"
            )

        if (ExportService.Loadinglist(un.value.value.toLong()).loadinglistType != LoadinglistType.BAG)
            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.LL_WRONG_TYPE.value//"Loadinglist not of Bag-Type"
            )

        val checklistBagBackUnit = parcelRepository.getParcelsByLoadingList(un.value.value.toLong())
        if (checklistBagBackUnit.count() > 0) {
            val usedLoadinglistNoList = checklistBagBackUnit.map { it.bagbelegnrc.toLong() }.distinct()
            if (usedLoadinglistNoList.count() > 1)
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.LL_USED_FOR_MULTIPLE_BAGS.value//"Loadinglist used for multiple bags"
                )
            if (usedLoadinglistNoList.count() == 1 && usedLoadinglistNoList.first() != unBack.value.value.toLong())
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.LL_ALREADY_USED_FOR_ANOTHER_BAG.value//"Loadinglist already used for another bag"
                )
        }
        val checklistLoadinglistUnits = parcelRepository.findUnitsInBagBackByBagBackUnitNumber(unBack.value.value.toLong())
        if (checklistLoadinglistUnits.count() > 0) {
            val usedBagBackUnitNoList = checklistLoadinglistUnits.map { it.ladelistennummerd.toLong() }.distinct()
            if (usedBagBackUnitNoList.count() > 1)
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_BACK_UNIT_USED_FOR_MULTIPLE_LL.value//"BagBackUnitNo used for multiple loadinglists"
                )
            if (usedBagBackUnitNoList.count() == 1 && usedBagBackUnitNoList.first() != un.value.value.toLong())
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_BACK_UNIT_ALREADY_USED_FOR_ANOTHER_LL.value//"BagBackUnitNo already used for another loadinglist"
                )
        }

        val unSeal = DekuUnitNumber.parseLabel(yellowSealNo)
        when {
            unSeal.hasError -> {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.YELLOW_SEAL_WRONG_CHECKDIGIT.value//"Yellow seal number - wrong check digit"
                )
            }
        }
        if (unSeal.value.type != UnitNumber.Type.BackSeal && unSeal.value.type != UnitNumber.Type.ReserveSeal)
            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.YELLOW_SEAL_NOT_VALID.value//"Yellow seal number not valid, scanned type: ${unSeal.value.type}"
            )

        if (bag.sealNumberYellow != null) {
            if (bag.sealNumberYellow.toString().toLong() != unSeal.value.value.toLong()) {
                if (bag.sealNumberRed != null) {
                    if (bag.sealNumberRed.toString().toLong() != unSeal.value.value.toLong()) {
                        throw RestProblem(
                                status = Response.Status.CONFLICT,
                                title = ExportService.ResponseMsg.SEAL_MISMATCH.value//"Seal number mismatch"
                        )
                    }
                } else {
                    throw RestProblem(
                            status = Response.Status.CONFLICT,
                            title = ExportService.ResponseMsg.SEAL_MISMATCH.value//"Seal number mismatch"
                    )
                }
            }
        } else {
            if (bag.sealNumberRed != null) {
                if (bag.sealNumberRed.toString().toLong() != unSeal.value.value.toLong()) {
                    throw RestProblem(
                            status = Response.Status.CONFLICT,
                            title = ExportService.ResponseMsg.SEAL_MISMATCH.value //"Seal number mismatch"
                    )
                }
            }
            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.NO_SEAL_IN_BAG.value//"No seal number in bag"
            )
        }

        val exportUnitOrder = getAndCheckUnit(unitNo, stationNo)

        val unitRecord = exportUnitOrder.unit
        //val orderRecord = exportUnitAndOrder.order

        if (unitRecord.verpackungsart == 91) {//verpackungsart=Valore
            val station = stationService.getByStationNo(stationNo)
            if (!station.exportValuablesAllowed) {
                throw RestProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = ExportService.ResponseMsg.VAL_NOT_ALLOWED.value//"Valuables not allowed"
                )
            }

        } else {
            if (unitRecord.gewichteffektiv > maxWeightForParcelBag) {
                throw RestProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = ExportService.ResponseMsg.WEIGHT_GREATER_THAN_MAX.value//"Weight > $maxWeightForParcelBag kg"
                )
            }
        }

        var title = ""// "Ok"
        if (unitRecord.ladelistennummerd == null) {
        } else if (unitRecord.ladelistennummerd.toLong() == un.value.value.toLong()) {
            if (unitRecord.bagbelegnrc == unBack.value.value.toDouble())
            //doppelt gescannt
                throw RestProblem(
                        status = Response.Status.OK,
                        title = ExportService.ResponseMsg.PARCEL_ALREADY_SCANNED.value//"Parcel already scanned"
                )
        } else {
            //umbuchen auf andere ladeliste
            title = ExportService.ResponseMsg.LL_CHANGED.value//"Loadinglist changed"

        }
        unitRecord.ladelistennummerd = un.value.value.toDouble()

        //export erst beim schließen
        //exportUnit(ExportUnitAndOrder(unitRecord, orderRecord), un.value.value.toLong(), stationNo)

        if (unitRecord.bagbelegnrc == null || unitRecord.bagbelegnrc != unBack.value.value.toDouble()) {
            unitRecord.bagbelegnrc = unBack.value.value.toDouble()
        }
        if (unitRecord.bagidnrc == null || unitRecord.bagidnrc != bag.bagNumber!!.toDouble()) {
            unitRecord.bagidnrc = bag.bagNumber!!.toDouble()
        }
        if (unitRecord.plombennrc == null || unitRecord.plombennrc != unSeal.value.value.toDouble()) {
            unitRecord.plombennrc = unSeal.value.value.toDouble()
        }
        if (unitRecord.beladelinie == null || unitRecord.beladelinie != bag.bagNumber!!.toDouble()) {
            unitRecord.beladelinie = bag.bagNumber!!.toDouble()
        }
        unitRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
        val mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        return mapper.writeValueAsString(title)
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    override fun closeBagStationExport(bagID: String, bagBackUnitNo: String, stationNo: Int, loadingListNo: String) {

        val bag = getAndCheckBag(stationNo, bagID)
        val backUnit = bag.unitNoBack
        backUnit ?: throw RestProblem(
                status = Response.Status.CONFLICT,
                title = ExportService.ResponseMsg.BAG_ID_OK_NO_BACK.value//"BagId found - no bagback-unit found"
        )

        if (bag.lastStation == 2 || bag.status == BagStatus.CLOSED_FROM_STATION || bag.status == BagStatus.CLOSED_FROM_HUB) {

            if (statusRepository.statusExist(backUnit, Event.EXPORT_LOADED.creator.toString(), Event.EXPORT_LOADED.concatId, Reason.NORMAL.id)) {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_ID_OK_ALREADY_EXPORTED.value//"BagId found - bag already exported"
                )
            } else {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_ID_OK_ALREADY_CLOSED.value//"BagId found - bag already closed - try to reopen"
                )
            }
        }
        val unBack = DekuUnitNumber.parseLabel(bagBackUnitNo)
        when {
            unBack.hasError -> {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_UNIT_WRONG_CHECKDIGIT.value//"Bag-UnitNo wrong check digit"
                )
            }
        }
        if (unBack.value.type != UnitNumber.Type.BagBack)

            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.BAG_UNIT_NOT_VALID.value//"Bag-UnitNo not valid"
            )

        if (unBack.value.value.toLong() != backUnit) {
            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.BAG_BACK_UNIT_DISMATCH.value//"Bag-BackUnitNo dismatch"
            )
        }

        val un = DekuUnitNumber.parseLabel(loadingListNo)
        when {
            un.hasError -> {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.LL_WRONG_CHECKDIGIT.value//"Loadinglist - wrong check digit"
                )
            }
        }
        if (un.value.type != UnitNumber.Type.Parcel)

            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.LL_NOT_VALID.value//"Loadinglist not valid"
            )

        if (ExportService.Loadinglist(un.value.value.toLong()).loadinglistType != LoadinglistType.BAG)
            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.LL_WRONG_TYPE.value//"Loadinglist not of Bag-Type"
            )

        var sendStatusRequired = false

        val exportUnitOrder = getAndCheckUnit(bagBackUnitNo, stationNo)

        val unitRecord = exportUnitOrder.unit
        val orderRecord = exportUnitOrder.order

        dsl.update(Tables.SSO_S_MOVEPOOL)
                .set(Tables.SSO_S_MOVEPOOL.STATUS, BagStatus.CLOSED_FROM_HUB.value.toDouble())
                .set(Tables.SSO_S_MOVEPOOL.LASTDEPOT, 2.0)
                .where(Tables.SSO_S_MOVEPOOL.BAG_NUMBER.eq(bag.bagNumber!!.toDouble())
                        .and(Tables.SSO_S_MOVEPOOL.MOVEPOOL.eq("m"))
                        .and(Tables.SSO_S_MOVEPOOL.LASTDEPOT.eq(stationNo.toDouble()))
                )
                .execute()

        if (orderRecord.colliesgesamt != 1.toShort()) {
            orderRecord.colliesgesamt = 1
            orderRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
        }

        bag.ordersToexport = getParcelsFilledInBagBackByBagBackUnitNo(bag.unitNoBack)
        if (bag.ordersToexport.count() == 0) {//backsending empty bag -> weight=1
            if (unitRecord.gewichteffektiv != 1.0) {
                unitRecord.gewichteffektiv = 1.0
            }
            if (unitRecord.gewichtreal != 1.0) {
                unitRecord.gewichtreal = 1.0

            }
            unitRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())

            if (orderRecord.gewichtgesamt != 1.0) {
                orderRecord.gewichtgesamt = 1.0
                orderRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
            }

        } else {//weight correction
            val weightRealSum = bag.ordersToexport.sumByDouble { it.parcels.sumByDouble { it.realWeight } }
            if (orderRecord.gewichtgesamt != weightRealSum) {
                orderRecord.gewichtgesamt = weightRealSum
                orderRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
            }
            if (unitRecord.gewichtreal != weightRealSum) {
                unitRecord.gewichtreal = weightRealSum
            }
            if (unitRecord.gewichteffektiv != weightRealSum) {
                unitRecord.gewichteffektiv = weightRealSum
            }
            unitRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
            bag.ordersToexport.forEach { parcels ->
                parcels.parcels.forEach {
                    val inBagexportUnitOrder = getAndCheckUnit(DekuUnitNumber.parse(it.parcelNo.toString().padStart(11, '0')).value.label, stationNo)

                    val inBagunitRecord = inBagexportUnitOrder.unit
                    val inBagorderRecord = inBagexportUnitOrder.order
                    exportUnit(ExportUnitOrder(inBagunitRecord, inBagorderRecord), un.value.value.toLong(), stationNo)
                }
            }
        }
        if (sendStatusRequired) {
            if (orderRecord.sendstatus.toInt() != 0) {
                orderRecord.sendstatus = 0
                orderRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
            }
        }
    }

    override fun getBag(stationNo: Int, bagID: String): ExportService.Bag {

        val bag = getAndCheckBag(stationNo, bagID)
        val backUnit = bag.unitNoBack
        backUnit ?: throw RestProblem(
                status = Response.Status.CONFLICT,
                title = ExportService.ResponseMsg.BAG_ID_OK_NO_BACK.value//"BagId found - no bagback-unit found"
        )

        if (bag.lastStation == 2) {

            if (statusRepository.statusExist(backUnit, Event.EXPORT_LOADED.creator.toString(), Event.EXPORT_LOADED.concatId, Reason.NORMAL.id)) {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_ID_OK_ALREADY_EXPORTED.value//"BagId found - bag already exported"
                )
            } else {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_ID_OK_ALREADY_CLOSED.value//"BagId found - bag already closed - try to reopen"
                )
            }
        }

        bag.ordersToexport = getParcelsFilledInBagBackByBagBackUnitNo(bag.unitNoBack)

        return bag
    }

    open fun getAndCheckBag(stationNo: Int, bagID: String): ExportService.Bag {
        val un = DekuUnitNumber.parseLabel(bagID)
        when {
            un.hasError -> {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_ID_WRONG_CHECKDIGIT.value//"BagId wrong check digit"
                )
            }
        }
        if (un.value.type != UnitNumber.Type.BagId)

            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.BAG_ID_NOT_VALID.value //"BagId not valid"
            )

        val bag = stationRepository.getBag(un.value.value.toLong())?.toBag()
        bag ?: throw RestProblem(
                status = Response.Status.NOT_FOUND,
                title = ExportService.ResponseMsg.BAG_ID_NOT_FOUND.value//"BagId not found"
        )
        //check bag
        bag.bagNumber ?: throw RestProblem(
                status = Response.Status.CONFLICT,
                title = ExportService.ResponseMsg.BAG_ID_NULL.value//"Bagnumber null"
        )
        bag.lastStation ?: throw RestProblem(
                status = Response.Status.CONFLICT,
                title = ExportService.ResponseMsg.BAG_ID_WITHOUT_LAST_STATION.value//"BagId without lastStation"
        )

        if (!bag.movepool.equals("m")) {
            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.BAG_ID_NOT_IN_MOVE.value//"BagId not found in move-state"
            )
        }
        if ((bag.lastStation != stationNo) && (bag.lastStation != 2)) {
            throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.LAST_DEPOT_STATION_MISMATCH.value//"BagId found - station mismatch"
            )
        }

        bag.bagNumberLabel = un.value.label


        val oid = bag.orderhubTodepot
        if (oid != null) {
            val b = stationRepository.getUnitNo(oid)
            bag.unitNo = b
            if (b != null) {
                val unUn = DekuUnitNumber.parse(b.toString())
                if (!unUn.hasError) {
                    bag.unitNoLabel = unUn.value.label
                }
            }

        }
        val oidBack = bag.orderdepotTohub
        oidBack ?: throw RestProblem(
                status = Response.Status.CONFLICT,
                title = ExportService.ResponseMsg.BAG_ID_OK_NO_BACK_ORDER.value//"BagId found - no bagback-order found"
        )


        val backUnit = stationRepository.getUnitNo(oidBack)
        bag.unitNoBack = backUnit
        backUnit ?: throw RestProblem(
                status = Response.Status.CONFLICT,
                title = ExportService.ResponseMsg.BAG_ID_OK_NO_BACK.value//"BagId found - no bagback-unit found"
        )

        val unUnBack = DekuUnitNumber.parse(backUnit.toString())
        if (!unUnBack.hasError) {
            bag.unitBackLabel = unUnBack.value.label
        }

        if (bag.lastStation == 2) {
            val bagBackOrder = parcelRepository.getOrderById(oidBack)
            bagBackOrder ?: throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.BAG_ID_OK_NO_BACK_ORDER.value//"BagId found - bagback-order not found"
            )

            bagBackOrder.depotnrabd ?: throw RestProblem(
                    status = Response.Status.CONFLICT,
                    title = ExportService.ResponseMsg.BAG_ID_OK_BACK_ORDER_WITHOUT_ABD.value //"BagId found - bagback-order without depotnrabd"
            )

            if (bagBackOrder.depotnrabd.toInt() != stationNo) {
                throw RestProblem(
                        status = Response.Status.CONFLICT,
                        title = ExportService.ResponseMsg.BAG_ID_OK_BACK_ORDER_ABD_MISMATCH.value//"BagId found - bagback-order station mismatch depotnrabd"
                )
            }

        }
        if (bag.sealNumberGreen != null) {
            val unGreen = DekuUnitNumber.parse(bag.sealNumberGreen.toString())
            if (!unGreen.hasError) {
                bag.sealGreenLabel = unGreen.value.label
            }
        }
        if (bag.sealNumberYellow != null) {
            val unYellow = DekuUnitNumber.parse(bag.sealNumberYellow.toString())
            if (!unYellow.hasError) {
                bag.sealYellowLabel = unYellow.value.label
            }
        }
        if (bag.sealNumberRed != null) {
            val unRed = DekuUnitNumber.parse(bag.sealNumberRed.toString())
            if (!unRed.hasError) {
                bag.sealRedLabel = unRed.value.label
            }
        }
        return bag
    }

    open fun getParcelsFilledInBagBackByBagBackUnitNo(bagBackUnitNo: Long?): List<ExportService.Order> {
        bagBackUnitNo ?: return listOf()
        val parcels = parcelRepository.findUnitsInBagBackByBagBackUnitNumber(bagBackUnitNo).groupBy { it.orderid }
        if (parcels.count() == 0) {
            return listOf()

        }
        val orders = parcelRepository.getOrdersByIds(parcels.keys.map { it.toLong() }.toList().distinct())//?.groupBy { it.orderid }
        if (orders.count() == 0) {
            return listOf()

        }
        return orders.map {
            it.toOrderToExport().also { order ->
                order.parcels = parcels
                        .getOrDefault(order.orderId.toDouble(), listOf())
                        .map { it.toParcelToExport() }
            }
        }.filter { it.parcels.count() > 0 }
    }


    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun getAndCheckUnit(scanCode: String, stationNo: Int): ExportUnitOrder {
        val un = DekuUnitNumber.parseLabel(scanCode)
        var dekuNo: Long?
        when {
            un.hasError -> {
                val gun = GlsUnitNumber.parseLabel(scanCode)
                when {
                    gun.hasError -> {
                        val unitRecords = parcelRepository.getExportParcelsByCreferenceAndStation(stationNo, scanCode)
                        if (unitRecords.count() == 0)
                            throw RestProblem(
                                    status = Response.Status.NOT_FOUND,
                                    title = ExportService.ResponseMsg.PARCEL_NOT_FOUND.value//"Parcel not found"
                            )
                        if (unitRecords.count() > 1) {
                            throw RestProblem(
                                    status = Response.Status.BAD_REQUEST,
                                    title = ExportService.ResponseMsg.MORE_PARCELS_FOR_CREFERENCE.value//"More parcels found to this cReference"
                            )
                        }
                        if (unitRecords.count() == 0) {
                            throw RestProblem(
                                    status = Response.Status.NOT_FOUND,
                                    title = ExportService.ResponseMsg.PARCEL_NOT_FOUND.value//"Parcel not found"
                            )
                        }
                        dekuNo = unitRecords[0].colliebelegnr.toLong()

                    }
                    else -> {
                        dekuNo = gun.value.toUnitNumber().value.toLong()
                    }
                }
            }
            else -> {
                dekuNo = un.value.value.toLong()
            }
        }

        val unitRecord = parcelRepository.findParcelByUnitNumber(dekuNo)
        unitRecord ?: throw RestProblem(
                status = Response.Status.NOT_FOUND,
                title = ExportService.ResponseMsg.PARCEL_NOT_FOUND.value//"Parcel not found"
        )
        if (unitRecord.erstlieferstatus.toInt() == 4) {
            throw RestProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = ExportService.ResponseMsg.PARCEL_DELIVERED.value//"Parcel delivered"
            )
        }

        val orderRecord = parcelRepository.getOrderById(unitRecord.orderid.toLong())
        orderRecord ?: throw RestProblem(
                status = Response.Status.NOT_FOUND,
                title = ExportService.ResponseMsg.ORDER_NOT_FOUND.value//"Order not found"
        )

        if (orderRecord.kzTransportart.toInt() != 1) {
            throw RestProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = ExportService.ResponseMsg.NO_ONS.value//"No ONS"
            )
        }
        if (orderRecord.depotnrabd != stationNo) {
            //if (!(stationNo==800 && unitRecord.colliebelegnr.toLong().toString().startsWith("8"))){
            if (!(stationNo == 800 && orderRecord.depotnrabd in (800..900))) {
                throw RestProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = ExportService.ResponseMsg.STATION_DISMATCH.value//"Station dismatch"
                )
            }
        }
        var checkOk = true
        val allUnitsOfOrder = parcelRepository.getParcelsByOrderId(orderRecord.orderid.toLong())
        if (allUnitsOfOrder.count() == 0)
            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = ExportService.ResponseMsg.NO_PARCELS_FOUND.value//"Parcels not found"
            )
        allUnitsOfOrder.forEach {
            if (it.erstlieferstatus.toInt() == 8 && it.erstlieferfehler.toInt() != 30)
                checkOk = false
            if (it.erstlieferstatus.toInt() == 4)
                checkOk = false
        }
        val workDate = bagServiceCentral.getWorkingDate()
        if (checkOk) {

            when (orderRecord.kzTransportart.toInt()) {
                0, 1, 2, 4, 8 -> {
                    var doUpdate = false
                    var doUpdateDelivery = false

                    if (orderRecord.verladedatum != workDate.toTimestamp()) {
                        //Test Verladedatum
                        doUpdate = true
                        //setRoute

                        //Korrektur Datensatz abhängig von tbloptionen nr=1212 Wert=-1 ???
                        ////Verladedatum=workdate

                        ////Feiertag_1
                        ////FeiertagShlS
                        ////if sendStatus!=0 sendstatus=0

                        //collies korregieren falls erstlieferstatus=0 oder 8-30


                    }
                    if (orderRecord.dtauslieferung == null
                            || orderRecord.dtauslieferung <= java.time.LocalDateTime.now().toLocalDate().toTimestamp()
                            || orderRecord.dtauslieferung > java.time.LocalDateTime.now().plusDays(90).toLocalDate().toTimestamp()) {
                        //Test Lieferdatum=null,<now or >+90 Tage (referenzScan Teileinlieferung)
                        //doUpdate = true
                        doUpdateDelivery = true

                        //setRoute
                        //Korrektur Datensatz abhängig von tbloptionen nr=1212 Wert=-1 ???
                        ////dtAuslieferung
                        ////Feiertag_2
                        ////FeiertagShlD
                        ////if sendStatus!=0 sendstatus=0
                        //collies korregieren falls erstlieferstatus=0 oder 8-30
                    }
                    if (doUpdate || doUpdateDelivery) {
                        val routingRequest = RoutingService.Request(sendDate = ShortDate(workDate.toTimestamp()),
                                desiredDeliveryDate = null,
                                services = orderRecord.service.toInt(),
                                weight = unitRecord.gewichtreal.toFloat(),
                                sender = RoutingService.Request.Participant(
                                        country = orderRecord.lands,
                                        zip = orderRecord.plzs,
                                        timeFrom = null,
                                        timeTo = null,
                                        desiredStation = unitRecord.mydepotabd.toString()),
                                consignee = RoutingService.Request.Participant(
                                        country = orderRecord.landd,
                                        zip = orderRecord.plzd,
                                        timeFrom = orderRecord.dtterminVon.toShortTime().toString(),
                                        timeTo = orderRecord.dttermin.toShortTime().toString(),
                                        desiredStation = unitRecord.mydepotid2.toString()
                                )
                        )
                        val routing = routingService.request(routingRequest)
                        if (orderRecord.verladedatum.toTimestamp() != routing.sendDate!!.date.toTimestamp()) {

                            orderRecord.verladedatum = routing.sendDate!!.date.toTimestamp()
                            //orderRecord.feiertag_1 = routing.sendDate!!.date.toLocalDate().dayOfWeek.toString()
                            orderRecord.feiertag_1 = routing.sendDate!!.date.toLocalDate().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.GERMAN)
                            orderRecord.feiertagshls = DayTypeKey.valueOf(routing.sender!!.dayType.toUpperCase()).value
                            orderRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
                        }
//                        if (orderRecord.dtauslieferung == null
//                                || orderRecord.dtauslieferung.toTimestamp() != routing.deliveryDate!!.date.toTimestamp()
//                                ) {
                        //if (doUpdateDelivery) {
                        if (doUpdateDelivery || orderRecord.dtauslieferung.toTimestamp() < routing.deliveryDate!!.date.toTimestamp()) {
                            orderRecord.dtauslieferung = routing.deliveryDate!!.date.toTimestamp()
                            //orderRecord.feiertag_2 = routing.deliveryDate!!.date.toLocalDate().dayOfWeek.toString()
                            orderRecord.feiertag_2 = routing.deliveryDate!!.date.toLocalDate().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.GERMAN)
                            orderRecord.feiertagshld = DayTypeKey.valueOf(routing.consignee!!.dayType.toUpperCase()).value
                            orderRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
                        }
                        if (orderRecord.sendstatus.toInt() != 0) {
                            orderRecord.sendstatus = 0
                            orderRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
                        }

                    }
                    if (orderRecord.lockflag.toInt() == 3) {
                        //Lockflag=0 if =3
                        //sdgstatus="S" if !="S"
                        //sendstatus=0 if !=0
                        //collies korregieren falls erstlieferstatus=0 oder 8-30
                        orderRecord.lockflag = 0
                        if (!orderRecord.sdgstatus.equals("S", true)) {
                            orderRecord.sdgstatus = "S"
                        }
                        if (orderRecord.sendstatus.toInt() != 0) {
                            orderRecord.sendstatus = 0
                        }

                        orderRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
                    }
//                    allUnitsOfOrder.forEach {
//                        if (it.erstlieferstatus.toInt() == 8 && it.erstlieferfehler.toInt() == 30){
//                            it.lieferstatus=0
//                            it.lieferfehler=0
//                            it.erstlieferstatus=0
//                            it.store()
//                        }
//                    }
                    if (unitRecord.erstlieferstatus.toInt() == 8 && unitRecord.erstlieferfehler.toInt() == 30) {
                        unitRecord.lieferstatus = 0
                        unitRecord.lieferfehler = 0
                        unitRecord.erstlieferstatus = 0
                        unitRecord.store()
                    }

                }
            }
        }

        if (orderRecord.lockflag.toInt() == 3) {
            throw RestProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = ExportService.ResponseMsg.PARCEL_DELETED.value//"Parcel deleted"
            )
        }
//        if (unitRecord.erstlieferstatus.toInt() == 8 && unitRecord.erstlieferfehler.toInt() == 30) {
//            throw DefaultProblem(
//                    status = Response.Status.BAD_REQUEST,
//                    title = "parcel marked as missing - parts of order are delivered"
//            )
//        }
        if (orderRecord.verladedatum != workDate.toTimestamp()) {
            throw RestProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = ExportService.ResponseMsg.SEND_DATE_INVALID.value//"Invalid senddate"
            )
        }

        return ExportUnitOrder(unitRecord, orderRecord)
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun exportUnit(eu: ExportUnitOrder, loadingListNo: Long, stationNo: Int): ExportUnitOrder {

        val unitRecord = eu.unit
        val orderRecord = eu.order

        if (unitRecord.ladelistennummerd == null || unitRecord.ladelistennummerd.toLong() != loadingListNo) {
            unitRecord.ladelistennummerd = loadingListNo.toDouble()
            unitRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
        }
        val workDate = bagServiceCentral.getWorkingDate()
        if (unitRecord.dtausgangdepot2 == null || unitRecord.dtausgangdepot2 != workDate.toTimestamp()) {
            unitRecord.dtausgangdepot2 = workDate.toTimestamp()
            unitRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
        }
        if (unitRecord.tournr2 == null || unitRecord.tournr2 != 0) {
            unitRecord.tournr2 = 0
            unitRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
        }
        if (unitRecord.iScan == null || unitRecord.iScan != -1) {
            unitRecord.iScan = -1
            unitRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
        }
        if (unitRecord.nued2h2 == null || unitRecord.nued2h2 != -1) {
            unitRecord.nued2h2 = -1
            unitRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
        }
        if (unitRecord.nueh2d2 == null || unitRecord.nueh2d2 != -1) {
            unitRecord.nueh2d2 = -1
            unitRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
        }
        val scanTs = Date()
        val infotext = "WebExport"
        statusRepository.createIfNotExists(unitRecord.colliebelegnr.toLong(), scanTs, Event.EXPORT_RECEIVE, Reason.NORMAL, infotext, stationNo.toString())

        statusRepository.createIfNotExists(unitRecord.colliebelegnr.toLong(), scanTs, Event.EXPORT_LOADED, Reason.NORMAL, infotext, stationNo.toString())


        if (orderRecord.service.toLong() and 134217728.toLong() == 134217728.toLong()) {
            orderRecord.service = (orderRecord.service.toLong() - 134217728.toLong()).toInt().toUInteger()
            if (orderRecord.empfaenger != null && orderRecord.empfaenger.isNotEmpty()) {
                orderRecord.empfaenger = null
            }

        }

        if (orderRecord.uploadstatus == null || orderRecord.uploadstatus.toInt() != 1) {
            orderRecord.uploadstatus = 1
        }

        orderRecord.dtsendad2z = Date().toTimestamp()
        orderRecord.storeWithHistoryExportservice(unitRecord.colliebelegnr.toLong())
        return ExportUnitOrder(unitRecord, orderRecord)
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
}