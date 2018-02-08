package org.deku.leoz.central.service.internal

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Routines
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TblauftragRecord
import org.deku.leoz.central.data.jooq.dekuclient.tables.records.TblauftragcolliesRecord
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.model.*
import org.deku.leoz.service.entity.DayTypeKey
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.*
import org.deku.leoz.service.internal.ImportService
import org.deku.leoz.service.internal.UserService
import org.deku.leoz.time.toShortTime
import org.deku.leoz.time.toGregorianLongDateString
import org.deku.leoz.time.toGregorianLongDateTimeString
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

    override fun getParcelsToImportByStationNo(stationNo: Int, deliveryDate: Date?): List<ImportService.Order> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun import(scanCode: String, stationNo: Int): ImportService.Parcel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setProperties(parcel: ImportService.Parcel, stationNo: Int): ImportService.Parcel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}