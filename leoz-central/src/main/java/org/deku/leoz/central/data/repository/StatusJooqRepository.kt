package org.deku.leoz.central.data.repository

import org.deku.leoz.central.config.PersistenceConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.deku.leoz.central.data.jooq.tables.records.TblstatusRecord
import org.jooq.DSLContext
import javax.inject.Inject
import javax.inject.Named
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.Tblstatus
import org.deku.leoz.central.data.toUInteger
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*

@Named
class StatusJooqRepository {
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)

    lateinit var dslContext: DSLContext
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun statusExist(unitNo: Long, creator: String, status: Int, reason: Int): Boolean {
        val exist = dslContext.selectCount().from(Tblstatus.TBLSTATUS)
                .where(Tables.TBLSTATUS.PACKSTUECKNUMMER.eq(unitNo.toDouble()))
                .and(Tables.TBLSTATUS.KZ_STATUSERZEUGER.eq(creator))
                .and(Tables.TBLSTATUS.KZ_STATUS.eq(status.toUInteger()))
                .and(Tables.TBLSTATUS.FEHLERCODE.eq(reason.toUInteger()))
                .fetchOne(0, Int::class.java)
        return exist != 0
    }
}

fun TblstatusRecord.setDate(date: Date) {
    this.datum = SimpleDateFormat("yyyyMMdd").format(date)
}

fun TblstatusRecord.setTime(date: Date) {
    this.zeit = SimpleDateFormat("HHmm").format(date)
}