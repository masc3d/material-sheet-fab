package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.deku.leoz.central.data.jooq.tables.records.TblauftragcolliesRecord
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named
import org.deku.leoz.central.data.jooq.tables.Tblauftragcollies
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.Tblauftrag
import org.deku.leoz.central.data.jooq.tables.records.TblauftragRecord
import org.deku.leoz.central.data.toUByte
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.service.internal.ParcelServiceV1
import org.deku.leoz.service.internal.entity.Address
import org.jooq.types.UInteger
import sx.time.toSqlDate
import sx.time.toTimestamp


/**
 * Created by JT on 18.07.17.
 */
@Named
class ParcelJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)

    lateinit var dslContext: DSLContext


    fun setSignaturePath(parcelNumber: String, path: String) {
        dslContext.update(Tables.TBLAUFTRAGCOLLIES)
                .set(Tables.TBLAUFTRAGCOLLIES.BMPFILENAME, path)
                .where(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR.eq(parcelNumber.toDouble()))
                .execute()
    }

    fun getParcelByParcelId(id:Long): TblauftragcolliesRecord? {
        if (id==0.toLong()) return null
        return dslContext.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(Tables.TBLAUFTRAGCOLLIES.PARCEL_ID.eq(id.toInt()))
                ?.fetchOneInto(Tblauftragcollies.TBLAUFTRAGCOLLIES)
    }

    fun findParcelByUnitNumber(unitNo: Long): TblauftragcolliesRecord? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(Tables.TBLAUFTRAGCOLLIES.COLLIEBELEGNR.eq(unitNo.toDouble()))
                .and(Tables.TBLAUFTRAGCOLLIES.ORDERPOS.greaterThan(0))
                .and(Tables.TBLAUFTRAGCOLLIES.ORDERID.greaterThan(0.0))
                ?.fetchOneInto(Tblauftragcollies.TBLAUFTRAGCOLLIES)
    }


    fun findUnitsInBagByBagUnitNumber(bagUnitNo: Long): List<TblauftragcolliesRecord>? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(Tables.TBLAUFTRAGCOLLIES.BAGBELEGNRABC.eq(bagUnitNo.toDouble()))
                .fetchInto(TblauftragcolliesRecord::class.java)
    }

    fun getParcelsByOrderId(orderId: Long): List<TblauftragcolliesRecord>? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(orderId.toDouble()))
                .and(Tables.TBLAUFTRAGCOLLIES.ORDERPOS.greaterThan(0))
                .and(Tables.TBLAUFTRAGCOLLIES.ORDERID.greaterThan(0.0))
                .fetchInto(TblauftragcolliesRecord::class.java)
    }

    fun getParcelsByLoadingList(loadinglistNo: Long): List<TblauftragcolliesRecord>? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(Tables.TBLAUFTRAGCOLLIES.LADELISTENNUMMERD.eq(loadinglistNo.toDouble()))
                .fetchInto(TblauftragcolliesRecord::class.java)
    }

    fun getOrderById(orderId: Long): TblauftragRecord? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAG)
                .where(Tables.TBLAUFTRAG.ORDERID.eq(orderId.toDouble()))
                .fetchOneInto(Tblauftrag.TBLAUFTRAG)
    }

    fun getParcels2ExportByLoadingList(loadinglistNo: Long): List<TblauftragcolliesRecord>? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(
                        Tables.TBLAUFTRAGCOLLIES.LADELISTENNUMMERD.eq(loadinglistNo.toDouble())
                                .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                                .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30)))//fehlendes Pkst raus
                )
                .fetchInto(TblauftragcolliesRecord::class.java)
    }

    fun getParcels2ExportInBagByStation(station: Int): List<TblauftragcolliesRecord>? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(
                        Tables.TBLAUFTRAGCOLLIES.MYDEPOTABD.eq(station)
                                .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                                .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30)))//fehlendes Pkst raus
                                .and(Tables.TBLAUFTRAGCOLLIES.VERPACKUNGSART.eq(91)//Valore
                                        .or(Tables.TBLAUFTRAGCOLLIES.GEWICHTEFFEKTIV.lessOrEqual(2.0)))
                                .and(Tables.TBLAUFTRAGCOLLIES.LADELISTENNUMMERD.isNull)
                )
                .fetchInto(TblauftragcolliesRecord::class.java)
    }

    fun getOrder2ExportById(orderId: Long): TblauftragRecord? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAG)
                .where(Tables.TBLAUFTRAG.ORDERID.eq(orderId.toDouble())
                        .and(Tables.TBLAUFTRAG.LOCKFLAG.eq(0))
                        .and(Tables.TBLAUFTRAG.SERVICE.bitAnd(UInteger.valueOf(134217728)).eq(UInteger.valueOf(0)))//fehlende anfahrt raus
                        .and(Tables.TBLAUFTRAG.KZ_TRANSPORTART.eq(1.toUByte()))//ONS
                )
                .fetchOneInto(Tblauftrag.TBLAUFTRAG)
    }

    fun getOrders2ExportByStation(station: Int): List<TblauftragRecord>? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAG)
                .where(Tables.TBLAUFTRAG.DEPOTNRABD.eq(station)
                        .and(Tables.TBLAUFTRAG.LOCKFLAG.eq(0))
                        .and(Tables.TBLAUFTRAG.SERVICE.bitAnd(UInteger.valueOf(134217728)).eq(UInteger.valueOf(0)))//fehlende anfahrt raus
                        .and(Tables.TBLAUFTRAG.EMPFAENGER.isNull)
                        .and(Tables.TBLAUFTRAG.KZ_TRANSPORTART.eq(1.toUByte()))//ONS
                )
                .fetchInto(TblauftragRecord::class.java)
    }


    fun getParcels2ExportByOrderid(orderId: Long): List<TblauftragcolliesRecord>? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(
                        Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(orderId.toDouble())
                                .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                                .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30))))//fehlendes Pkst raus
                .fetchInto(TblauftragcolliesRecord::class.java)
    }

    fun getLoadedParcels2ExportByOrderid(orderId: Long): List<TblauftragcolliesRecord>? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(
                        Tables.TBLAUFTRAGCOLLIES.ORDERID.eq(orderId.toDouble())
                                .and(Tables.TBLAUFTRAGCOLLIES.LADELISTENNUMMERD.greaterThan(100000.0))
                                .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                                .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30))))//fehlendes Pkst raus
                .fetchInto(TblauftragcolliesRecord::class.java)
    }

    fun getParcels2ExportByCreferenceAndStation(station: Int, cReference: String): List<TblauftragcolliesRecord>? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(
                        Tables.TBLAUFTRAGCOLLIES.MYDEPOTABD.eq(station)
                                .and(Tables.TBLAUFTRAGCOLLIES.CREFERENZ.eq(cReference))
                                .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                                .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30))))//fehlendes Pkst raus
                .fetchInto(TblauftragcolliesRecord::class.java)
    }

    fun getParcels2ExportByCreference(cReference: String): List<TblauftragcolliesRecord>? {
        return dslContext.select()
                .from(Tables.TBLAUFTRAGCOLLIES)
                .where(
                        Tables.TBLAUFTRAGCOLLIES.CREFERENZ.eq(cReference)
                                .and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.ne(4))//ausgeliefert
                                .andNot(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERSTATUS.eq(8).and(Tables.TBLAUFTRAGCOLLIES.ERSTLIEFERFEHLER.eq(30))))//fehlendes Pkst raus
                .fetchInto(TblauftragcolliesRecord::class.java)
    }

    fun getParcelsByCreferenceAndStation(station: Int, cReference: String): List<TblauftragcolliesRecord>? {
        val unitRecords: List<TblauftragcolliesRecord>?
        if (station == 800) {
            unitRecords = dslContext.select()
                    .from(Tables.TBLAUFTRAGCOLLIES)
                    .where(
                            Tables.TBLAUFTRAGCOLLIES.MYDEPOTABD.greaterOrEqual(800)
                                    .and(Tables.TBLAUFTRAGCOLLIES.MYDEPOTABD.lessOrEqual(900))
                                    .and(Tables.TBLAUFTRAGCOLLIES.CREFERENZ.eq(cReference))
                    )
                    .fetchInto(TblauftragcolliesRecord::class.java)
        } else {
            unitRecords = dslContext.select()
                    .from(Tables.TBLAUFTRAGCOLLIES)
                    .where(
                            Tables.TBLAUFTRAGCOLLIES.MYDEPOTABD.eq(station)
                                    .and(Tables.TBLAUFTRAGCOLLIES.CREFERENZ.eq(cReference))
                    )
                    .fetchInto(TblauftragcolliesRecord::class.java)
        }
        return unitRecords
    }
}

fun TblauftragRecord.toOrder2Export(): ParcelServiceV1.Order2Export {
    val order = ParcelServiceV1.Order2Export(
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

fun TblauftragcolliesRecord.toParcel2Export(): ParcelServiceV1.Parcel2Export {
    val parcel = ParcelServiceV1.Parcel2Export(
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