package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Routines
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.model.*
import org.deku.leoz.node.rest.ServiceException
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.*
import org.deku.leoz.service.internal.BagService
import org.deku.leoz.service.internal.ExportService
import org.deku.leoz.service.internal.LoadinglistService
import org.deku.leoz.service.internal.ParcelServiceV1
import org.deku.leoz.service.internal.UserService
import org.deku.leoz.service.pub.RoutingService
import org.deku.leoz.time.toShortTime
import org.deku.leoz.time.toString_ddMMyyyy_PointSeparated
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional
import sx.rs.DefaultProblem
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Named
@Path("internal/v1/export")
open class ExportService : org.deku.leoz.service.internal.ExportService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    @Inject
    private lateinit var userService: UserService

    @Inject
    private lateinit var bagService: BagService

    @Inject
    private lateinit var depotRepository: DepotJooqRepository

    @Inject
    private lateinit var parcelRepository: ParcelJooqRepository

    @Inject
    private lateinit var fieldHistoryRepository: FieldHistoryJooqRepository

    @Inject
    private lateinit var routingService: org.deku.leoz.node.service.pub.RoutingService

    @Inject
    private lateinit var stationService: StationService

    @Inject
    private lateinit var bagServiceCentral: org.deku.leoz.central.service.internal.BagService

    @Inject
    private lateinit var statusRepository: StatusJooqRepository

    @Transactional(PersistenceConfiguration.QUALIFIER)
    override fun export(scanCode: String, loadingListNo: Long, stationNo: Int): Boolean {
        val user = userService.get()

        //check stationNo in user-stationsAllowed

        val un = DekuUnitNumber.parseLabel(scanCode)
        var dekuNo: Long? = null
        when {
            un.hasError -> {
                val gun = GlsUnitNumber.parseLabel(scanCode)
                when {
                    gun.hasError -> {
                        val unitRecords = parcelRepository.getParcelsByCreferenceAndStation(stationNo, scanCode)
                        if (unitRecords.count() == 0)
                            throw DefaultProblem(
                                    status = Response.Status.NOT_FOUND,
                                    title = "Parcel not found"
                            )
                        if (unitRecords.count() > 1) {
                            throw DefaultProblem(
                                    status = Response.Status.BAD_REQUEST,
                                    title = "More parcels found to this cReference"
                            )
                        }
                        if (unitRecords.count() == 0) {
                            throw DefaultProblem(
                                    status = Response.Status.NOT_FOUND,
                                    title = "Parcel not found"
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
        unitRecord ?: throw DefaultProblem(
                status = Response.Status.NOT_FOUND,
                title = "Parcel not found"
        )
        if (unitRecord.erstlieferstatus.toInt() == 4) {
            throw DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "Parcel delivered"
            )
        }

        val orderRecord = parcelRepository.getOrderById(unitRecord.orderid.toLong())
        orderRecord ?: throw DefaultProblem(
                status = Response.Status.NOT_FOUND,
                title = "Order not found"
        )

        if (orderRecord.kzTransportart.toInt() != 1) {
            throw DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "No ONS"
            )
        }
        if (orderRecord.depotnrabd != stationNo) {
            //if (!(stationNo==800 && unitRecord.colliebelegnr.toLong().toString().startsWith("8"))){
            if (!(stationNo == 800 && orderRecord.depotnrabd in (800..900))) {
                throw DefaultProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "Station dismatch"
                )
            }
        }
        var checkOk = true
        val allUnitsOfOrder = parcelRepository.getParcelsByOrderId(orderRecord.orderid.toLong())
        if (allUnitsOfOrder.count() == 0)
            throw DefaultProblem(
                    status = Response.Status.NOT_FOUND,
                    title = "Parcels not found"
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
                            || orderRecord.dtauslieferung < java.time.LocalDateTime.now().toLocalDate().toTimestamp()
                            || orderRecord.dtauslieferung > java.time.LocalDateTime.now().plusDays(90).toLocalDate().toTimestamp()) {
                        //Test Lieferdatum=null,<now or >+90 Tage (referenzScan Teileinlieferung)
                        doUpdate = true
                        //setRoute
                        //Korrektur Datensatz abhängig von tbloptionen nr=1212 Wert=-1 ???
                        ////dtAuslieferung
                        ////Feiertag_2
                        ////FeiertagShlD
                        ////if sendStatus!=0 sendstatus=0
                        //collies korregieren falls erstlieferstatus=0 oder 8-30
                    }
                    if (doUpdate) {
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
                            val oldSendDate = orderRecord.verladedatum?.toString_ddMMyyyy_PointSeparated() ?: ""

                            orderRecord.verladedatum = routing.sendDate!!.date.toTimestamp()
                            if (orderRecord.store() > 0) {
                                fieldHistoryRepository.addEntry(
                                        orderId = unitRecord.orderid.toLong(),
                                        unitNo = unitRecord.colliebelegnr.toLong(),
                                        fieldName = "verladedatum",
                                        oldValue = oldSendDate,
                                        newValue = routing.sendDate!!.date.toTimestamp().toString_ddMMyyyy_PointSeparated(),
                                        changer = "WEB",
                                        point = "EX"
                                )
                            }
                        }
                        if (orderRecord.dtauslieferung == null || orderRecord.dtauslieferung.toTimestamp() != routing.deliveryDate!!.date.toTimestamp()) {
                            val oldDeliveryDate = orderRecord.dtauslieferung?.toString_ddMMyyyy_PointSeparated() ?: ""
                            orderRecord.dtauslieferung = routing.deliveryDate!!.date.toTimestamp()
                            if (orderRecord.store() > 0) {
                                fieldHistoryRepository.addEntry(
                                        orderId = unitRecord.orderid.toLong(),
                                        unitNo = unitRecord.colliebelegnr.toLong(),
                                        fieldName = "dtauslieferung",
                                        oldValue = oldDeliveryDate,
                                        newValue = routing.deliveryDate!!.date.toTimestamp().toString_ddMMyyyy_PointSeparated(),
                                        changer = "WEB",
                                        point = "EX"
                                )
                            }
                        }
                        if (orderRecord.sendstatus.toInt() != 0) {
                            val oldSendstate = orderRecord.sendstatus?.toString() ?: ""
                            orderRecord.sendstatus = 0
                            if (orderRecord.store() > 0) {
                                fieldHistoryRepository.addEntry(
                                        orderId = unitRecord.orderid.toLong(),
                                        unitNo = unitRecord.colliebelegnr.toLong(),
                                        fieldName = "sendstatus",
                                        oldValue = oldSendstate,
                                        newValue = "0",
                                        changer = "WEB",
                                        point = "EX"
                                )
                            }
                        }

                    }
                    if (orderRecord.lockflag.toInt() == 3) {
                        //Lockflag=0 if =3
                        //sdgstatus="S" if !="S"
                        //sendstatus=0 if !=0
                        //collies korregieren falls erstlieferstatus=0 oder 8-30
                        orderRecord.lockflag = 0
                        if (orderRecord.store() > 0) {
                            fieldHistoryRepository.addEntry(
                                    orderId = unitRecord.orderid.toLong(),
                                    unitNo = unitRecord.colliebelegnr.toLong(),
                                    fieldName = "lockflag",
                                    oldValue = "3",
                                    newValue = "0",
                                    changer = "WEB",
                                    point = "EX"
                            )
                        }
                        if (!orderRecord.sdgstatus.equals("S", true)) {
                            val oldSdgstate = orderRecord.sdgstatus ?: ""
                            orderRecord.sdgstatus = "S"
                            if (orderRecord.store() > 0) {
                                fieldHistoryRepository.addEntry(
                                        orderId = unitRecord.orderid.toLong(),
                                        unitNo = unitRecord.colliebelegnr.toLong(),
                                        fieldName = "sdgstatus",
                                        oldValue = oldSdgstate,
                                        newValue = "S",
                                        changer = "WEB",
                                        point = "EX"
                                )
                            }
                        }
                        if (orderRecord.sendstatus.toInt() != 0) {
                            val oldSendstate = orderRecord.sendstatus.toString()
                            orderRecord.sendstatus = 0
                            if (orderRecord.store() > 0) {
                                fieldHistoryRepository.addEntry(
                                        orderId = unitRecord.orderid.toLong(),
                                        unitNo = unitRecord.colliebelegnr.toLong(),
                                        fieldName = "sendstatus",
                                        oldValue = oldSendstate,
                                        newValue = "0",
                                        changer = "WEB",
                                        point = "EX"
                                )
                            }
                        }


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
            throw DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "Parcel deleted"
            )
        }
//        if (unitRecord.erstlieferstatus.toInt() == 8 && unitRecord.erstlieferfehler.toInt() == 30) {
//            throw DefaultProblem(
//                    status = Response.Status.BAD_REQUEST,
//                    title = "parcel marked as missing - parts of order are delivered"
//            )
//        }
        if (orderRecord.verladedatum != workDate.toTimestamp()) {
            throw DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "Invalid senddate"
            )
        }

        if (unitRecord.verpackungsart == 91) {//verpackungsart=Valore
            val station = stationService.getByStationNo(stationNo)
            if (!station.exportValuablesAllowed) {
                throw DefaultProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "Valuables not allowed"
                )
            }
            if (!station.exportValuablesWithoutBagAllowed) {
                throw DefaultProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "Valuables not allowed without bag"
                )
            }
        }
        var title = "Ok"
        if (unitRecord.ladelistennummerd == null) {
        } else if (unitRecord.ladelistennummerd.toLong() == loadingListNo) {
            //doppelt gescannt
            throw DefaultProblem(
                    status = Response.Status.OK,
                    title = "Parcel already scanned"
            )
        } else {
            //umbuchen auf andere ladeliste
            title = "Loadinglist changed"

        }
        val oldLoadinglist = unitRecord.ladelistennummerd?.toLong()?.toString() ?: ""
        unitRecord.ladelistennummerd = loadingListNo.toDouble()
        if (unitRecord.store() > 0) {
            fieldHistoryRepository.addEntry(
                    orderId = unitRecord.orderid.toLong(),
                    unitNo = unitRecord.colliebelegnr.toLong(),
                    fieldName = "ladelistennummerd",
                    oldValue = oldLoadinglist,
                    newValue = loadingListNo.toString(),
                    changer = "WEB",
                    point = "EX"
            )
        }
        if (unitRecord.dtausgangdepot2 == null || unitRecord.dtausgangdepot2 != workDate.toTimestamp()) {
            val oldDtAusgangDepot2 = unitRecord.dtausgangdepot2?.toString_ddMMyyyy_PointSeparated() ?: ""
            unitRecord.dtausgangdepot2 = workDate.toTimestamp()
            if (unitRecord.store() > 0) {
                fieldHistoryRepository.addEntry(
                        orderId = unitRecord.orderid.toLong(),
                        unitNo = unitRecord.colliebelegnr.toLong(),
                        fieldName = "dtausgangdepot2",
                        oldValue = oldDtAusgangDepot2,
                        newValue = workDate.toTimestamp().toString_ddMMyyyy_PointSeparated(),
                        changer = "WEB",
                        point = "EX"
                )
            }
        }
        if (unitRecord.iScan == null || unitRecord.iScan != -1) {
            val oldIscan = unitRecord.iScan?.toString() ?: ""
            unitRecord.iScan = -1
            if (unitRecord.store() > 0) {
                fieldHistoryRepository.addEntry(
                        orderId = unitRecord.orderid.toLong(),
                        unitNo = unitRecord.colliebelegnr.toLong(),
                        fieldName = "iScan",
                        oldValue = oldIscan,
                        newValue = "-1",
                        changer = "WEB",
                        point = "EX"
                )
            }
        }
        if (unitRecord.nued2h2 == null || unitRecord.nued2h2 != -1) {
            val oldNueD2H2 = unitRecord.nued2h2?.toString() ?: ""
            unitRecord.nued2h2 = -1
            if (unitRecord.store() > 0) {
                fieldHistoryRepository.addEntry(
                        orderId = unitRecord.orderid.toLong(),
                        unitNo = unitRecord.colliebelegnr.toLong(),
                        fieldName = "nued2h2",
                        oldValue = oldNueD2H2,
                        newValue = "-1",
                        changer = "WEB",
                        point = "EX"
                )
            }
        }
        if (unitRecord.nueh2d2 == null || unitRecord.nueh2d2 != -1) {
            val oldNueH2D2 = unitRecord.nueh2d2?.toString() ?: ""
            unitRecord.nueh2d2 = -1
            if (unitRecord.store() > 0) {
                fieldHistoryRepository.addEntry(
                        orderId = unitRecord.orderid.toLong(),
                        unitNo = unitRecord.colliebelegnr.toLong(),
                        fieldName = "nueh2d2",
                        oldValue = oldNueH2D2,
                        newValue = "-1",
                        changer = "WEB",
                        point = "EX"
                )
            }
        }
        val scanTs = Date()
        val infotext = "WebExport"
        var existStatus = statusRepository.statusExist(unitRecord.colliebelegnr.toLong(), "A", 2, 0)
        if (!existStatus) {
            val r = dslContext.newRecord(Tables.TBLSTATUS)
            r.packstuecknummer = unitRecord.colliebelegnr
            r.setDate(scanTs)
            r.setTime(scanTs)
            r.infotext = infotext

            r.kzStatuserzeuger = "A"
            r.kzStatus = 2.toUInteger()
            r.timestamp2 = Date().toTimestamp()
            r.fehlercode = 0.toUInteger()

            r.erzeugerstation = stationNo.toString()
            if (!statusRepository.saveEvent(r))
                log.error("Insert status failed")
        }
        existStatus = statusRepository.statusExist(unitRecord.colliebelegnr.toLong(), "A", 4, 0)
        if (!existStatus) {
            val r = dslContext.newRecord(Tables.TBLSTATUS)
            r.packstuecknummer = unitRecord.colliebelegnr
            r.setDate(scanTs)
            r.setTime(scanTs)
            r.infotext = infotext

            r.kzStatuserzeuger = "A"
            r.kzStatus = 4.toUInteger()
            r.timestamp2 = Date().toTimestamp()
            r.fehlercode = 0.toUInteger()

            r.erzeugerstation = stationNo.toString()
            if (!statusRepository.saveEvent(r))
                log.error("Insert status failed")
        }

        throw DefaultProblem(
                status = Response.Status.OK,
                title = title
        )
        return true
    }

    override fun getLoadedParcels2ExportByStationNo(stationNo: Int): List<ExportService.Order> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        val orders = parcelRepository.getOrders2ExportByStation(stationNo)
//        orders ?: throw DefaultProblem(
//                status = Response.Status.NOT_FOUND,
//                title = "No orders found"
//        )
//        val orders2export = orders.map { it.toOrder2Export() }
//        loop@ for (it in orders2export) {
//            val parcels = parcelRepository.getLoadedParcels2ExportByOrderid(it.orderId)
//            parcels ?: continue@loop
//            if (parcels.count() == 0) {
//                it.parcels = null
//                continue@loop
//            }
//            it.parcels = parcels.map { f -> f.toParcel2Export() }
//        }
//        val ordersFiltered = orders2export.filter { it.parcels != null }
//        if (ordersFiltered.count() == 0)
//            throw DefaultProblem(
//                    status = Response.Status.NOT_FOUND,
//                    title = "No parcels found"
//            )
//        return ordersFiltered
    }

    override fun getNewLoadinglistNo(): LoadinglistService.Loadinglist {
        val user = userService.get()

        return LoadinglistService.Loadinglist(loadinglistNo = Routines.fTan(dslContext.configuration(), counter.LOADING_LIST.value) + 300000)
    }

    override fun getParcels2ExportByStationNo(stationNo: Int): List<ExportService.Order> {
        val orders = parcelRepository.getOrders2ExportByStation(stationNo)
        if (orders.count() == 0)
            throw DefaultProblem(
                    status = Response.Status.NOT_FOUND,
                    title = "No orders found"
            )


        var allParcels = parcelRepository.getParcels2ExportByOrderids(orders.map { it.orderid.toLong() }.toList())?.groupBy { it.orderid }
        if (allParcels.count() == 0)
            throw DefaultProblem(
                    status = Response.Status.NOT_FOUND,
                    title = "No parcels found"
            )


        return orders.map {
            it.toOrder2Export().also { order ->
                order.parcels = allParcels
                        .getOrDefault(order.orderId.toDouble(), listOf())
                        ?.map { it.toParcel2Export() }
            }
        }.filter { it.parcels.count() > 0 }
    }

    override fun getParcels2ExportInBagByStationNo(stationNo: Int): List<ExportService.Order> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        val parcels = parcelRepository.getParcels2ExportInBagByStation(stationNo)
//        parcels ?: throw DefaultProblem(
//                status = Response.Status.NOT_FOUND,
//                title = "No parcels found for this station"
//        )
//        if (parcels.count() == 0)
//            throw DefaultProblem(
//                    status = Response.Status.NOT_FOUND,
//                    title = "No parcels found for this station"
//            )
//        val orderIdList = parcels.map { it.orderid }.distinct()
//        val orderList: MutableList<ParcelServiceV1.Order2Export> = mutableListOf<ParcelServiceV1.Order2Export>()
//        loop@ for (it in orderIdList) {
//            val orderRecord = parcelRepository.getOrder2ExportById(it.toLong())
//            if (orderRecord != null) {
//                if (orderRecord.bagidnra != null)
//                    continue@loop
//                val order = orderRecord.toOrder2Export()
//                val pp = parcels.filter { f -> f.orderid == it }
//                if (pp.count() > 0) {
//                    order.parcels = pp.map { it.toParcel2Export() }
//                    orderList.add(order)
//                }
//            }
//        }
//        return orderList
    }

    override fun getCount2SendBackByStation(stationNo: Int): Int {
        return bagService.getCount2SendBackByStation(stationNo)
    }

    override fun getParcels2ExportByLoadingList(loadinglistNo: Long): List<ExportService.Order> {
        val parcels = parcelRepository.getParcels2ExportByLoadingList(loadinglistNo)
        if (parcels.count() == 0)
            throw DefaultProblem(
                    status = Response.Status.NOT_FOUND,
                    title = "No parcels found for this list"
            )
        return parcels
    }

    override fun getNewBagLoadinglistNo(): LoadinglistService.Loadinglist {
        val user = userService.get()

        return LoadinglistService.Loadinglist(loadinglistNo = Routines.fTan(dslContext.configuration(), counter.LOADING_LIST.value) + 10000)
    }

    override fun setBagStationExportRedSeal(bagID: Long, stationNo: Int, redSeal: Long, text: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun reopenBagStationExport(bagID: Long, stationNo: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fillBagStationExport(bagID: Long, stationNo: Int, unitNo: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeBagStationExport(bagID: Long, stationNo: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBag(bagID: Long): ExportService.Bag {
        val un = DekuUnitNumber.parseLabel(bagID.toString())
        when {
            un.hasError -> {
                throw DefaultProblem(
                        status = Response.Status.NOT_FOUND,
                        title = "BagId wrong check digit"
                )
            }
        }
        if (un.value.type != UnitNumber.Type.BagId)

            throw DefaultProblem(
                    status = Response.Status.NOT_FOUND,
                    title = "BagId not valid"
            )

        val bag = depotRepository.getBag(un.value.value.toLong())?.toBag()
        bag ?:
                throw DefaultProblem(
                        status = Response.Status.NOT_FOUND,
                        title = "BagId not found"
                )
        val oid = bag.orderhub2depot
        if (oid != null) {
            bag.unitNo = depotRepository.getUnitNo(oid)
        }
        val oidBack = bag.orderdepot2hub
        if (oidBack != null) {
            bag.unitNoBack = depotRepository.getUnitNo(oidBack)
            bag.orders2export = getParcelsFilledInBagByBagOrderID(oidBack)
        }
        return bag
    }

    fun getParcelsFilledInBagByBagOrderID(bagId:Long): List<ExportService.Order>{
        TODO("not implemented")
    }
}