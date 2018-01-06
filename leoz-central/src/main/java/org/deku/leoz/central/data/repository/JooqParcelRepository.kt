package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TblauftragcolliesRecord
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named
import org.deku.leoz.central.data.jooq.dekuclient.tables.Tblauftragcollies
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.Tblauftrag
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TblauftragRecord
import org.deku.leoz.central.data.toUByte
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.model.maxWeightForParcelBag
import org.deku.leoz.service.internal.ParcelServiceV1
import org.deku.leoz.service.internal.entity.Address
import org.deku.leoz.service.internal.ExportService
import org.jooq.types.UInteger
import sx.time.toSqlDate
import sx.time.toTimestamp
import sx.time.workDate
import java.time.LocalDate


/**
 * Created by JT on 18.07.17.
 */
@Named
class JooqParcelRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)

    lateinit var dsl: DSLContext

    fun getCountParcelsByBmp(bmpFilename: String): Int {
        return dsl.selectCount()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(Tables.TBLAUFTRAGCOLLIES.BMPFILENAME.eq(bmpFilename)
                )// .and(Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(orderid.toDouble())))
                .fetchOne(0, Int::class.java)
    }

    fun setSignaturePath(parcelNumber: String, path: String) {
        dsl.update(Tables.TBLAUFTRAGCOLLIES)
                .set(Tables.TBLAUFTRAGCOLLIES.BMPFILENAME, path)
                .where(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR.eq(parcelNumber.toDouble()))
                .execute()
    }

    fun getParcelByParcelId(id: Long): TblauftragcolliesRecord? {
        if (id == 0.toLong()) return null
        return dsl.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(Tables.TBLAUFTRAGCOLLIES.PARCEL_ID.eq(id.toInt()))
                ?.fetchOneInto(Tblauftragcollies.TBLAUFTRAGCOLLIES)
    }

    fun findParcelByUnitNumber(unitNo: Long): TblauftragcolliesRecord? {
        if (unitNo == 0.toLong()) return null
        return dsl.fetchOne(
                Tables.TBLAUFTRAGCOLLIES,
                Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR.eq(unitNo.toDouble())
        )

    }


    fun findUnitsInBagByBagUnitNumber(bagUnitNo: Long): List<TblauftragcolliesRecord> {
        return dsl.fetch(
                Tables.TBLAUFTRAGCOLLIES,
                Tables.TBLAUFTRAGCOLLIES.BAGBELEGNRABC.eq(bagUnitNo.toDouble())
        )
    }

    fun findUnitsInBagBackByBagBackUnitNumber(bagBackUnitNo: Long): List<TblauftragcolliesRecord> {
        return dsl.fetch(
                Tables.TBLAUFTRAGCOLLIES,
                Tables.TBLAUFTRAGCOLLIES.BAGBELEGNRC.eq(bagBackUnitNo.toDouble())
                        .and(Tables.TBLAUFTRAGCOLLIES.BAGBELEGNRC.ne(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR))
        )
    }

    fun getParcelsByOrderId(orderId: Long): List<TblauftragcolliesRecord> {
        return dsl.fetch(
                Tables.TBLAUFTRAGCOLLIES,
                Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(orderId.toDouble())
                //  .and(Tables.TBLAUFTRAGCOLLIES.ORDERPOS.greaterThan(0))
                //  .and(Tables.TBLAUFTRAGCOLLIES.ORDERID.greaterThan(0.0))
        )
    }

    fun getParcelsByLoadingList(loadinglistNo: Long): List<TblauftragcolliesRecord> {
        return dsl.fetch(
                Tables.TBLAUFTRAGCOLLIES,
                Tables.TBLAUFTRAGCOLLIES.LADELISTENNUMMERD.eq(loadinglistNo.toDouble())
        )
    }

    fun getOrderById(orderId: Long): TblauftragRecord? {
        return dsl.fetchOne(
                Tables.TBLAUFTRAG,
                Tables.TBLAUFTRAG.ORDERID.eq(orderId.toDouble())
        )
    }

    fun getOrdersByIds(orderIds: List<Long>): List<TblauftragRecord> {
        return dsl.fetch(
                Tables.TBLAUFTRAG,
                Tables.TBLAUFTRAG.ORDERID.`in`(orderIds.map { it.toDouble() })
        )

    }

    //fun getParcelsToExportByLoadingList(loadinglistNo: Long, sendDate: LocalDate = java.time.LocalDateTime.now().workDate()): List<ExportService.Order> {
    fun getParcelsToExportByLoadingList(loadinglistNo: Long): List<ExportService.Order> {
        val result = listOf<ExportService.Order>()
        val records = dsl.fetch(
                Tables.TBLAUFTRAGCOLLIES
                        .join(Tables.TBLAUFTRAG)
                        .on(Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(Tables.TBLAUFTRAG.ORDERID)),

                Tables.TBLAUFTRAGCOLLIES.LADELISTENNUMMERD.eq(loadinglistNo.toDouble())
                        .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                        .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30)))//fehlendes Pkst raus
                        //.and(Tables.TBLAUFTRAG.VERLADEDATUM.eq(sendDate.toTimestamp()))
                        .and(Tables.TBLAUFTRAG.LOCKFLAG.eq(0))
                        .and(Tables.TBLAUFTRAG.SERVICE.bitAnd(UInteger.valueOf(134217728)).eq(UInteger.valueOf(0)))//fehlende anfahrt raus
                        .and(Tables.TBLAUFTRAG.KZ_TRANSPORTART.eq(1.toUByte()))//ONS
        )
        //.fetchInto(TblauftragcolliesRecord::class.java)
        records ?: return result
        if (records.count() == 0)
            return result
        val orders = records.into(Tables.TBLAUFTRAG).distinct()
        if (orders.count() == 0)
            return result
        val parcels = records.into(Tables.TBLAUFTRAGCOLLIES).groupBy { it.orderid }
        if (parcels.count() == 0)
            return result
        return orders.map {
            it.toOrderToExport().also { order ->
                order.parcels = parcels
                        .getOrDefault(order.orderId.toDouble(), listOf())
                        .map { it.toParcelToExport() }
            }
        }.filter { it.parcels.count() > 0 }


    }

    fun getParcelsToExportInBagByStation(station: Int, sendDate: LocalDate = java.time.LocalDateTime.now().workDate()): List<ExportService.Order> {
        val result = listOf<ExportService.Order>()
        val records = dsl.fetch(
                Tables.TBLAUFTRAGCOLLIES
                        .join(Tables.TBLAUFTRAG)
                        .on(Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(Tables.TBLAUFTRAG.ORDERID)),

                Tables.TBLAUFTRAGCOLLIES.MYDEPOTABD.eq(station)
                        .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                        .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30)))//fehlendes Pkst raus
                        .and(Tables.TBLAUFTRAGCOLLIES.VERPACKUNGSART.eq(91)//Valore
                                .or(Tables.TBLAUFTRAGCOLLIES.GEWICHTEFFEKTIV.lessOrEqual(maxWeightForParcelBag)))
                        .and(Tables.TBLAUFTRAGCOLLIES.LADELISTENNUMMERD.isNull)
                        .and(Tables.TBLAUFTRAG.VERLADEDATUM.eq(sendDate.toTimestamp()))
                        .and(Tables.TBLAUFTRAG.LOCKFLAG.eq(0))
                        .and(Tables.TBLAUFTRAG.SERVICE.bitAnd(UInteger.valueOf(134217728)).eq(UInteger.valueOf(0)))//fehlende anfahrt raus
                        .and(Tables.TBLAUFTRAG.KZ_TRANSPORTART.eq(1.toUByte()))//ONS
        )
        //.fetchInto(TblauftragcolliesRecord::class.java)

        records ?: return result
        if (records.count() == 0)
            return result
        val orders = records.into(Tables.TBLAUFTRAG).distinct()
        if (orders.count() == 0)
            return result
        val parcels = records.into(Tables.TBLAUFTRAGCOLLIES).groupBy { it.orderid }
        if (parcels.count() == 0)
            return result
        return orders.map {
            it.toOrderToExport().also { order ->
                order.parcels = parcels
                        .getOrDefault(order.orderId.toDouble(), listOf())
                        .map { it.toParcelToExport() }
            }
        }.filter { it.parcels.count() > 0 }
    }

    fun getOrderToExportById(orderId: Long): TblauftragRecord? {
        return dsl.fetchOne(
                Tables.TBLAUFTRAG,
                Tables.TBLAUFTRAG.ORDERID.eq(orderId.toDouble())
                        .and(Tables.TBLAUFTRAG.LOCKFLAG.eq(0))
                        .and(Tables.TBLAUFTRAG.SERVICE.bitAnd(UInteger.valueOf(134217728)).eq(UInteger.valueOf(0)))//fehlende anfahrt raus
                        .and(Tables.TBLAUFTRAG.KZ_TRANSPORTART.eq(1.toUByte()))//ONS
        )

    }

    fun getOrdersToExportByStation(station: Int, sendDate: LocalDate = java.time.LocalDateTime.now().workDate()): List<TblauftragRecord> {
        return dsl.fetch(
                Tables.TBLAUFTRAG,
                Tables.TBLAUFTRAG.DEPOTNRABD.eq(station)
                        .and(Tables.TBLAUFTRAG.LOCKFLAG.eq(0))
                        .and(Tables.TBLAUFTRAG.SERVICE.bitAnd(UInteger.valueOf(134217728)).eq(UInteger.valueOf(0)))//fehlende anfahrt raus
                        .and(Tables.TBLAUFTRAG.EMPFAENGER.isNull)
                        .and(Tables.TBLAUFTRAG.KZ_TRANSPORTART.eq(1.toUByte()))//ONS
                        .and(Tables.TBLAUFTRAG.VERLADEDATUM.eq(sendDate.toTimestamp()))
        )

    }


    fun getParcelsToExportByOrderid(orderId: Long): List<TblauftragcolliesRecord> {
        return dsl.fetch(
                Tables.TBLAUFTRAGCOLLIES,
                Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(orderId.toDouble())
                        .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                        .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30)))//fehlendes Pkst raus
        )

    }

    fun getParcelsToExportByOrderids(orderIds: List<Long>): List<TblauftragcolliesRecord> {
//        return dsl.select()
//                .from(Tables.TBLAUFTRAGCOLLIES)
//                .where(
//                        Tables.TBLAUFTRAGCOLLIES.ORDERID.`in`(orderIds.map { it.toDouble() })
//                                .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
//                                .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30))))//fehlendes Pkst raus
//                .fetchInto(TblauftragcolliesRecord::class.java)
        return dsl.fetch(
                Tables.TBLAUFTRAGCOLLIES,
                Tables.TBLAUFTRAGCOLLIES.ORDERID.`in`(orderIds.map { it.toDouble() })
                        .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                        .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30)))//fehlendes Pkst raus
        )
    }

    fun getLoadedParcelsToExportByOrderid(orderId: Long): List<TblauftragcolliesRecord> {
        return dsl.fetch(
                Tables.TBLAUFTRAGCOLLIES,
                Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(orderId.toDouble())
                        .and(Tables.TBLAUFTRAGCOLLIES.LADELISTENNUMMERD.greaterThan(100000.0))
                        .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                        .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30)))//fehlendes Pkst raus
        )

    }

    fun getLoadedParcelsToExportByOrderids(orderIds: List<Long>): List<TblauftragcolliesRecord> {
        return dsl.fetch(
                Tables.TBLAUFTRAGCOLLIES,
                Tables.TBLAUFTRAGCOLLIES.ORDERID.`in`(orderIds.map { it.toDouble() })
                        .and(Tables.TBLAUFTRAGCOLLIES.LADELISTENNUMMERD.greaterThan(100000.0))
                        .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                        .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30)))//fehlendes Pkst raus
        )

    }

    fun getParcelsToExportByCreferenceAndStation(station: Int, cReference: String): List<TblauftragcolliesRecord>? {
        return dsl.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .join(Tables.TBLAUFTRAG)
                .on(Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(Tables.TBLAUFTRAG.ORDERID))
                .where(
                        Tables.TBLAUFTRAGCOLLIES.MYDEPOTABD.eq(station)
                                .and(Tables.TBLAUFTRAGCOLLIES.CREFERENZ.eq(cReference))
                                .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                                .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30)))//fehlendes Pkst raus
                        //.and(Tables.TBLAUFTRAG.VERLADEDATUM.eq(sendDate.toTimestamp()))
                )
                .fetchInto(TblauftragcolliesRecord::class.java)
    }

    fun getParcelsToExportByCreference(cReference: String): List<TblauftragcolliesRecord> {
        return dsl.fetch(
                Tables.TBLAUFTRAGCOLLIES,
                Tables.TBLAUFTRAGCOLLIES.CREFERENZ.eq(cReference)
                        .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                        .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30)))//fehlendes Pkst raus
        )
    }

    fun getParcelsByCreferenceAndStation(station: Int, cReference: String): List<TblauftragcolliesRecord> {
        val unitRecords: List<TblauftragcolliesRecord>
        if (station == 800) {
            unitRecords = dsl.fetch(
                    Tables.TBLAUFTRAGCOLLIES,
                    Tables.TBLAUFTRAGCOLLIES.MYDEPOTABD.greaterOrEqual(800)
                            .and(Tables.TBLAUFTRAGCOLLIES.MYDEPOTABD.lessOrEqual(900))
                            .and(Tables.TBLAUFTRAGCOLLIES.CREFERENZ.eq(cReference))
            )

        } else {
            unitRecords = dsl.fetch(
                    Tables.TBLAUFTRAGCOLLIES,
                    Tables.TBLAUFTRAGCOLLIES.MYDEPOTABD.eq(station)
                            .and(Tables.TBLAUFTRAGCOLLIES.CREFERENZ.eq(cReference))
            )

        }
        return unitRecords
    }
}

fun TblauftragRecord.toOrderToExport(): ExportService.Order {
    val order = ExportService.Order(
            orderId = this.orderid.toLong(),
            deliveryAddress = Address(
                    line1 = this.firmad,
                    line2 = this.firmad2,
                    line3 = this.firmad3,
                    countryCode = this.landd ?: "",
                    zipCode = this.plzd ?: "",
                    city = this.ortd,
                    street = this.strassed,
                    streetNo = this.strnrd),
            deliveryStation = this.depotnrld ?: 0,
            shipmentDate = this.verladedatum?.toTimestamp()?.toSqlDate()
    )
    return order
}

fun TblauftragcolliesRecord.toParcelToExport(): ExportService.Parcel {
    val parcel = ExportService.Parcel(
            orderId = this.orderid.toLong(),
            parcelNo = this.colliebelegnr.toLong(),
            parcelPosition = this.orderpos.toInt(),
            loadinglistNo = this.ladelistennummerd?.toLong(),
            typeOfPackaging = this.verpackungsart,
            realWeight = this.gewichtreal,
            dateOfStationOut = this.dtausgangdepot2?.toTimestamp()?.toSqlDate(),
            cReference = this.creferenz

    )
    return parcel
}