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
import org.deku.leoz.service.internal.ParcelServiceV1
import org.deku.leoz.service.internal.entity.Address


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
}

fun TblauftragRecord.toOrder(): ParcelServiceV1.Order {
val order=ParcelServiceV1.Order(
        orderId = this.orderid.toLong(),
        deliveryAddress = Address(
                line1=this.firmad,
                line2=this.firmad2,
                line3=this.firmad3,
                countryCode = this.landd,
                zipCode = this.plzd,
                city=this.ortd,
                street = this.strassed,
                streetNo=this.strnrd),
        deliveryStation = this.depotnrld,
        shipmentDate = this.verladedatum
)
    return order
}

fun TblauftragcolliesRecord.toParcel():ParcelServiceV1.Parcel{
    val parcel=ParcelServiceV1.Parcel(
            orderId = this.orderid.toLong(),
            parcelNo = this.colliebelegnr.toLong(),
            parcelPosition = this.orderpos.toInt(),
            loadinglistNo = this.ladelistennummerd.toLong(),
            typeOfPackaging = this.verpackungsart,
            realWeight = this.gewichtreal,
            dateOfStationOut = this.dtausgangdepot2,
            cReference = this.creferenz

    )
    return parcel
}