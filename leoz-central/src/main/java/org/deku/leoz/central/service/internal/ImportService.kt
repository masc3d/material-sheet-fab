package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.central.data.toDoubleWithTwoDecimalDigits
import org.deku.leoz.model.*
import org.deku.leoz.service.internal.ImportService
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import sx.rs.RestProblem
import sx.time.toLocalDate
import sx.time.toTimestamp
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

@Component
@Path("internal/v1/import")
class ImportService : org.deku.leoz.service.internal.ImportService {

    val importServiceInfotext = "WebImport"

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var parcelRepository: JooqParcelRepository
    @Inject
    private lateinit var stationRepository: JooqStationRepository
    @Inject
    private lateinit var statusRepository: JooqStatusRepository

    @Context
    private lateinit var httpRequest: HttpServletRequest

    override fun getParcelsToImportByStationNo(stationNo: Int, deliveryDate: Date?): List<ImportService.Order> {

        val orders = if (deliveryDate != null) parcelRepository.getOrdersToImportByStation(stationNo, deliveryDate.toLocalDate()) else parcelRepository.getOrdersToImportByStation(station = stationNo)
        if (orders.count() == 0)
            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = ImportService.ResponseMsg.NO_ORDERS_FOUND.value //"No orders found"
            )
        val allParcels = parcelRepository.getParcelsNotDeliveredByOrderids(orders
                .map { it.orderid.toLong() }
                .toList()
        )
                .groupBy { it.orderid }

        if (allParcels.count() == 0)
            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = ImportService.ResponseMsg.NO_PARCELS_FOUND.value //"No parcels found"
            )


        return orders.map {
            it.toOrderToImport().also { order ->
                order.parcels = allParcels
                        .getOrDefault(order.orderId.toDouble(), listOf())
                        .map { it.toParcelToImport() }
            }
        }.filter { it.parcels.count() > 0 }
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    override fun import(scanCode: String, stationNo: Int): ImportService.Parcel {
        val parcel = getParcel(scanCode, stationNo)
        statusRepository.createIfNotExists(parcel.parcelNo, Date(), Event.IMPORT_RECEIVE, Reason.NORMAL, importServiceInfotext, stationNo.toString())
        val unitRecord = parcelRepository.findParcelByUnitNumber(parcel.parcelNo)
        unitRecord ?: throw RestProblem(
                status = Response.Status.NOT_FOUND,
                title = ImportService.ResponseMsg.PARCEL_NOT_FOUND.value//"Parcel not found"
        )
        unitRecord.dteingangdepot2 = Date().toTimestamp()
        unitRecord.storeWithHistoryImportservice(unitRecord.colliebelegnr.toLong())
        return getParcel(parcel.parcelNo)
    }

    @Transactional(PersistenceConfiguration.QUALIFIER)
    override fun setProperties(parcel: ImportService.Parcel): ImportService.Parcel {
        val parcelOriginal = getParcel(parcel.parcelNo)
        val unitRecord = parcelRepository.findParcelByUnitNumber(parcel.parcelNo)
        unitRecord ?: throw RestProblem(
                status = Response.Status.NOT_FOUND,
                title = ImportService.ResponseMsg.PARCEL_NOT_FOUND.value//"Parcel not found"
        )
        val parcelIsDamaged = parcel.isDamaged
        if (parcelIsDamaged != null) {
            if (parcelOriginal.isDamaged != parcelIsDamaged) {
                unitRecord.isDamaged = if (parcelIsDamaged) -1 else 0
                unitRecord.storeWithHistoryImportservice(unitRecord.colliebelegnr.toLong())
                if (parcelIsDamaged) {
                    statusRepository.createIfNotExists(parcel.parcelNo, Date(), Event.DELIVERY_FAIL, Reason.PARCEL_DAMAGED, importServiceInfotext, unitRecord.mydepotid2.toString())

                }
            }
        }
        val parcelIsMissing = parcel.isMissing
        if (parcelIsMissing != null) {
            if (parcelOriginal.isMissing != parcelIsMissing && parcelIsMissing) {
                //PASreset=true
                //if PAScleared WLtransfer
                if (unitRecord.erstlieferstatus.toInt() == 0) {
                    unitRecord.erstlieferstatus = Event.DELIVERY_FAIL.concatId.toShort()
                    unitRecord.erstlieferfehler = Reason.PARCEL_MISSING.oldValue.toShort()
                }
                unitRecord.lieferstatus = Event.DELIVERY_FAIL.concatId.toShort()
                unitRecord.lieferfehler = Reason.PARCEL_MISSING.oldValue.toShort()
                unitRecord.storeWithHistoryImportservice(unitRecord.colliebelegnr.toLong())
                statusRepository.createIfNotExists(parcel.parcelNo, Date(), Event.DELIVERY_FAIL, Reason.PARCEL_MISSING, importServiceInfotext, unitRecord.mydepotid2.toString())

            }
        }
        val parcelIsWrongLoaded = parcel.isWrongLoaded
        if (parcelIsWrongLoaded != null) {
            if (parcelOriginal.isWrongLoaded != parcelIsWrongLoaded && parcelIsWrongLoaded) {
                if (unitRecord.erstlieferstatus.toInt() == 0) {
                    unitRecord.erstlieferstatus = Event.DELIVERY_FAIL.concatId.toShort()
                    unitRecord.erstlieferfehler = Reason.WRONG_LOADED.oldValue.toShort()
                }
                unitRecord.lieferstatus = Event.DELIVERY_FAIL.concatId.toShort()
                unitRecord.lieferfehler = Reason.WRONG_LOADED.oldValue.toShort()
                unitRecord.storeWithHistoryImportservice(unitRecord.colliebelegnr.toLong())
                statusRepository.createIfNotExists(parcel.parcelNo, Date(), Event.DELIVERY_FAIL, Reason.WRONG_LOADED, importServiceInfotext, unitRecord.mydepotid2.toString())
            }
        }
        val parcelIsWrongRouted = parcel.isWrongRouted
        if (parcelIsWrongRouted != null) {
            if (parcelOriginal.isWrongRouted != parcelIsWrongRouted && parcelIsWrongRouted) {
                //PASreset=true
                //if PAScleared WLtransfer
                if (unitRecord.erstlieferstatus.toInt() == 0) {
                    unitRecord.erstlieferstatus = Event.DELIVERY_FAIL.concatId.toShort()
                    unitRecord.erstlieferfehler = Reason.WRONG_ROUTING.oldValue.toShort()
                }
                unitRecord.lieferstatus = Event.DELIVERY_FAIL.concatId.toShort()
                unitRecord.lieferfehler = Reason.WRONG_ROUTING.oldValue.toShort()
                unitRecord.storeWithHistoryImportservice(unitRecord.colliebelegnr.toLong())
                statusRepository.createIfNotExists(parcel.parcelNo, Date(), Event.DELIVERY_FAIL, Reason.WRONG_ROUTING, importServiceInfotext, unitRecord.mydepotid2.toString())
            }
        }

        val orderRecord = parcelRepository.getOrderById(unitRecord.orderid.toLong())
        orderRecord ?: throw RestProblem(
                status = Response.Status.NOT_FOUND,
                title = ImportService.ResponseMsg.ORDER_NOT_FOUND.value//"Order not found"
        )
        var parcelWeightChanged = false
        var parcelVolWeightChanged = false
        var parcelLengthChanged = false
        var parcelWidthChanged = false
        var parcelHeightChanged = false
        var parcelEffWeightChanged = false
        val parcelWeight = parcel.realWeight ?: 0.0
        val parcelLength = parcel.length ?: 0
        val parcelWidth = parcel.width ?: 0
        val parcelHeight = parcel.height ?: 0
        var parcelEffWeight:Double
        var orderSumWeight = orderRecord.gewichtgesamt ?: 0.0
        if (parcelWeight > 0 && parcelWeight != parcelOriginal.realWeight) {
            parcelWeightChanged = true
        }

        if (parcelLength > 0 && parcelLength != parcelOriginal.length) {
            parcelLengthChanged = true
        }

        if (parcelWidth > 0 && parcelWidth != parcelOriginal.width) {
            parcelWidthChanged = true
        }
        if (parcelHeight > 0 && parcelHeight != parcelOriginal.height) {
            parcelHeightChanged = true
        }

        //val parcelVolWeight = BigDecimal((parcelLength * parcelWidth * parcelHeight) / 6000).setScale(2,BigDecimal.ROUND_HALF_UP).toDouble()
        val parcelVolWeight = BigDecimal((parcelLength * parcelWidth * parcelHeight) / 6000).toDoubleWithTwoDecimalDigits()
        if (parcelVolWeight > 0) {
            if (parcelVolWeight != parcelOriginal.volWeight) {
                parcelVolWeightChanged = true
            }
        } else {
            parcelLengthChanged = false
            parcelWidthChanged = false
            parcelHeightChanged = false
        }
        if (parcelWeightChanged) {
            if (parcelVolWeightChanged) {
                if (parcelVolWeight > parcelWeight) {
                    parcelEffWeight = parcelVolWeight
                } else {
                    parcelEffWeight = parcelWeight
                }
            } else {
                if (parcelOriginal.volWeight!! > parcelWeight) {
                    parcelEffWeight = parcelOriginal.volWeight!!
                } else {
                    parcelEffWeight = parcelWeight
                }
            }
        } else {
            if (parcelVolWeightChanged) {
                if (parcelVolWeight > parcelOriginal.realWeight!!) {
                    parcelEffWeight = parcelVolWeight
                } else {
                    parcelEffWeight = parcelOriginal.realWeight!!
                }
            } else {
                parcelEffWeight = 0.0
            }
        }
        if (parcelEffWeight > 0.0) {
            if (parcelEffWeight != parcelOriginal.volWeight!!) {
                parcelEffWeightChanged = true
            }
        }
        if (parcelWeightChanged || parcelLengthChanged || parcelWidthChanged || parcelHeightChanged || parcelVolWeightChanged || parcelEffWeightChanged) {
            statusRepository.insertStatus(parcel.parcelNo, Date(), Event.ADJUSTMENT_WEIGHT, Reason.NORMAL, importServiceInfotext, unitRecord.mydepotid2.toString())
            if (parcelWeightChanged)
                unitRecord.gewichtreal = parcelWeight
            if (parcelLengthChanged)
                unitRecord.laenge = parcelLength.toShort()
            if (parcelWidthChanged)
                unitRecord.breite = parcelWidth.toShort()
            if (parcelHeightChanged)
                unitRecord.hoehe = parcelHeight.toShort()
            if (parcelVolWeightChanged)
                unitRecord.gewichtlbh = parcelVolWeight
            if (parcelEffWeightChanged)
                unitRecord.gewichteffektiv = parcelEffWeight
            unitRecord.storeWithHistoryImportservice(unitRecord.colliebelegnr.toLong())
        }
        val newSumWeightReal = parcelRepository.getSumUnitRealWeight(unitRecord.orderid.toLong())?.toDoubleWithTwoDecimalDigits()
        newSumWeightReal ?: throw RestProblem(
                status = Response.Status.NOT_FOUND,
                title = ImportService.ResponseMsg.PARCEL_NOT_FOUND.value//"Parcel not found"
        )
        val newSumWeightVol = parcelRepository.getSumUnitVolWeight(unitRecord.orderid.toLong())?.toDoubleWithTwoDecimalDigits()
        newSumWeightVol ?: throw RestProblem(
                status = Response.Status.NOT_FOUND,
                title = ImportService.ResponseMsg.PARCEL_NOT_FOUND.value//"Parcel not found"
        )
        val newOrderSumWeight: Double
        if (newSumWeightReal > newSumWeightVol)
            newOrderSumWeight = newSumWeightReal
        else
            newOrderSumWeight = newSumWeightVol
        if (orderSumWeight != newOrderSumWeight) {
            orderRecord.sendstatus = 0
            orderRecord.gewichtgesamt = newOrderSumWeight
            orderRecord.storeWithHistoryImportservice(unitRecord.colliebelegnr.toLong())
        }


        return getParcel(parcel.parcelNo)

    }

    override fun getParcel(scanCode: String, stationNo: Int): ImportService.Parcel {

        val un = DekuUnitNumber.parseLabel(scanCode)
        var dekuNo: Long?
        when {
            un.hasError -> {
                val gun = GlsUnitNumber.parseLabel(scanCode)
                when {
                    gun.hasError -> {
                        val unitRecords = parcelRepository.getImportParcelsByCreferenceAndStation(stationNo, scanCode)
                        if (unitRecords.count() == 0)
                            throw RestProblem(
                                    status = Response.Status.NOT_FOUND,
                                    title = ImportService.ResponseMsg.PARCEL_NOT_FOUND.value//"Parcel not found"
                            )
                        if (unitRecords.count() > 1) {
                            throw RestProblem(
                                    status = Response.Status.BAD_REQUEST,
                                    title = ImportService.ResponseMsg.MORE_PARCELS_FOR_CREFERENCE.value//"More parcels found to this cReference"
                            )
                        }
                        if (unitRecords.count() == 0) {
                            throw RestProblem(
                                    status = Response.Status.NOT_FOUND,
                                    title = ImportService.ResponseMsg.PARCEL_NOT_FOUND.value//"Parcel not found"
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
        return getParcel(dekuNo)

    }

    private fun getParcel(dekuNo: Long): ImportService.Parcel {
        val unitRecord = parcelRepository.findParcelByUnitNumber(dekuNo)
        unitRecord ?: throw RestProblem(
                status = Response.Status.NOT_FOUND,
                title = ImportService.ResponseMsg.PARCEL_NOT_FOUND.value//"Parcel not found"
        )
        val orderRecord = parcelRepository.getOrderById(unitRecord.orderid.toLong())
        orderRecord ?: throw RestProblem(
                status = Response.Status.NOT_FOUND,
                title = ImportService.ResponseMsg.ORDER_NOT_FOUND.value//"Order not found"
        )
        val parcelImport = unitRecord.toParcelToImport()
        if (parcelImport.tourNo == 0) {
            parcelImport.tourNo = stationRepository.getStationTour(orderRecord.plzd, orderRecord.depotnrld)
        }


        parcelImport.isMissing = statusRepository.statusExist(unitRecord.colliebelegnr.toLong(), Event.DELIVERY_FAIL.creator.toString(), Event.DELIVERY_FAIL.concatId, Reason.PARCEL_MISSING.oldValue)
        parcelImport.isWrongRouted = statusRepository.statusExist(unitRecord.colliebelegnr.toLong(), Event.DELIVERY_FAIL.creator.toString(), Event.DELIVERY_FAIL.concatId, Reason.WRONG_ROUTING.oldValue)
        parcelImport.isWrongLoaded = statusRepository.statusExist(unitRecord.colliebelegnr.toLong(), Event.DELIVERY_FAIL.creator.toString(), Event.DELIVERY_FAIL.concatId, Reason.WRONG_LOADED.oldValue)

        return parcelImport
    }
}