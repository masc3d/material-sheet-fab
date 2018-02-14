package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.model.*
import org.deku.leoz.service.internal.ImportService
import org.deku.leoz.service.internal.UserService
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.RestProblem
import sx.time.toLocalDate
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Named
@Path("internal/v1/import")
open class ImportService : org.deku.leoz.service.internal.ImportService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var userService: UserService
    @Inject
    private lateinit var parcelRepository: JooqParcelRepository
    @Inject
    private lateinit var stationRepository: JooqStationRepository
    @Inject
    private lateinit var statusRepository: JooqStatusRepository

    override fun getParcelsToImportByStationNo(stationNo: Int, deliveryDate: Date?): List<ImportService.Order> {
        userService.get()

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

    override fun import(scanCode: String, stationNo: Int): ImportService.Parcel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setProperties(parcel: ImportService.Parcel, stationNo: Int): ImportService.Parcel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getParcel(scanCode: String, stationNo: Int): ImportService.Parcel {
        userService.get()

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
            parcelImport.tourNo = stationRepository.getStationTour(orderRecord.plzd, stationNo)
        }


        parcelImport.isMissing = statusRepository.statusExist(unitRecord.colliebelegnr.toLong(), Event.DELIVERY_FAIL.creator.toString(), Event.DELIVERY_FAIL.concatId, Reason.PARCEL_MISSING.id)
        parcelImport.isWrongRouted = statusRepository.statusExist(unitRecord.colliebelegnr.toLong(), Event.DELIVERY_FAIL.creator.toString(), Event.DELIVERY_FAIL.concatId, Reason.WRONG_ROUTING.id)
        parcelImport.isWrongLoaded = statusRepository.statusExist(unitRecord.colliebelegnr.toLong(), Event.DELIVERY_FAIL.creator.toString(), Event.DELIVERY_FAIL.concatId, Reason.WRONG_LOADED.id)

        return parcelImport
    }
}