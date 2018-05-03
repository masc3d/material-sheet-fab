package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.Tblstatus
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TblstatusRecord
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.model.Event
import org.deku.leoz.model.Reason
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.threeten.bp.format.DateTimeFormatter
import sx.time.threeten.toLocalDateTime
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject

@Component
class JooqStatusRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)

    lateinit var dsl: DSLContext
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun statusExist(unitNo: Long, creator: String, status: Int, reason: Int): Boolean {
        val exist = dsl.selectCount().from(Tblstatus.TBLSTATUS)
                .where(Tables.TBLSTATUS.PACKSTUECKNUMMER.eq(unitNo.toDouble()))
                .and(Tables.TBLSTATUS.KZ_STATUSERZEUGER.eq(creator))
                .and(Tables.TBLSTATUS.KZ_STATUS.eq(status.toUInteger()))
                .and(Tables.TBLSTATUS.FEHLERCODE.eq(reason.toUInteger()))
                .fetchOne(0, Int::class.java)
        return exist != 0
    }
    fun insertStatus(unitNo: Long, statusTimestamp:Date, event:Event, reason: Reason=Reason.NORMAL, info:String="", creatorStation:String,text:String?=null,longitude:Double?=null, latitude:Double?=null){
        val r = dsl.newRecord(Tables.TBLSTATUS)
        r.packstuecknummer=unitNo.toDouble()
        var infotext=info
        if (infotext.length > 60)
            infotext = infotext.substring(0, 60)
        r.infotext=infotext
        r.setDate(statusTimestamp)
        r.setTime(statusTimestamp)
        r.erzeugerstation=creatorStation
        r.text=text
        r.poslong=longitude
        r.poslat=latitude
        r.kzStatuserzeuger = event.creator.toString()
        r.kzStatus = event.concatId.toUInteger()
        r.timestamp2 = Date().toTimestamp()
        r.fehlercode = reason.oldValue.toUInteger()
        r.store()
    }

    fun createIfNotExists(unitNo: Long, statusTimestamp:Date, event:Event, reason: Reason=Reason.NORMAL, info:String="", creatorStation:String,text:String?=null,longitude:Double?=null, latitude:Double?=null)
    {
        statusExist(unitNo, event.creator.toString(), event.concatId, reason.oldValue).also {
            if (!it)
                insertStatus(unitNo, statusTimestamp, event, reason, info, creatorStation,text,longitude,latitude)
        }
    }

}

fun TblstatusRecord.setDate(date: Date) {
    this.datum = DateTimeFormatter.ofPattern("yyyyMMdd").format(date.toLocalDateTime())
}

fun TblstatusRecord.setTime(date: Date) {
    this.zeit = DateTimeFormatter.ofPattern("HHmm").format(date.toLocalDateTime())
}