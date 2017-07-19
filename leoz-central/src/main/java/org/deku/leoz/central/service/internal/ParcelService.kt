package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.TblstatusRecord
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.node.rest.DefaultProblem
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.rs.auth.ApiKey
import javax.inject.Named
import javax.ws.rs.Path
import org.deku.leoz.service.internal.ParcelServiceV1
import org.jooq.DSLContext

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import javax.inject.Inject
import javax.ws.rs.core.Response


/**
 * Created by JT on 17.07.17.
 */
@Named
@ApiKey(false)
@Path("internal/v1/event")

/**
 * Parcel service message handler
 */
open class ParcelServiceV1 :
        org.deku.leoz.service.internal.ParcelServiceV1,
        MqHandler<ParcelServiceV1.ParcelMessage> {

    private val log = LoggerFactory.getLogger(this.javaClass)
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext
    @Inject
    private lateinit var parcelRepository: ParcelJooqRepository

    override fun onMessage(message: ParcelServiceV1.ParcelMessage, replyChannel: MqChannel?) {

        val events = message.dataPoints?.toList()
                ?: throw DefaultProblem(
                detail = "Missing data",
                status = Response.Status.BAD_REQUEST)

        log.trace("Received ${events.count()} from [${message.nodeId}] user [${message.userId}]")

        events.forEach {
            val r: TblstatusRecord
            r = dslContext.newRecord(Tables.TBLSTATUS)
            r.packstuecknummer = it.parcelScan.toDouble()
            r.datum = it.time.toString()
            r.zeit = it.time.toString()
            r.poslat = it.latitude
            r.poslong = it.longitude

            r.infotext = it.note
            r.kzStatuserzeuger = it.event.toString()
            r.fehlercode = it.reason.toUInteger()
            parcelRepository.save(r)
        }
    }
}

