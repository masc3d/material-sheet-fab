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
import java.util.*
import javax.inject.Named
import javax.ws.rs.Path
import org.deku.leoz.service.internal.ParcelServiceV1
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.time.toLocalDate
import java.text.SimpleDateFormat
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

        val events = message.events?.toList()
                ?: throw DefaultProblem(
                detail = "Missing data",
                status = Response.Status.BAD_REQUEST)

        log.trace("Received ${events.count()} from [${message.nodeId}] user [${message.userId}]")

        events.forEach {
            val r: TblstatusRecord
            r = dslContext.newRecord(Tables.TBLSTATUS)
            r.packstuecknummer = it.parcelNumber.toDouble()
//            r.erzeugerstation = it.eventValue.toString()
            r.datum = SimpleDateFormat("yyyyMMdd").parse(it.time.toLocalDate().toString()).toString()
            r.zeit = SimpleDateFormat("HHmm").parse(it.time.toLocalDate().toString()).toString()
            r.poslat = it.latitude
            r.poslong = it.longitude

            // TODO: check mainly for event/reason, then check for additional info structures as applicable
            if (it.deliveredInfo != null) {
                r.text = (it.deliveredInfo as ParcelServiceV1.DeliveredInfo).recipient
                r.kzStatuserzeuger = "E"
                r.kzStatus = 4.toUInteger()
                saveSignature(it.time, (it.deliveredInfo as ParcelServiceV1.DeliveredInfo).signature, it.parcelNumber, message.nodeId)
            }
            if (it.deliveredAtNeighborInfo != null) {
                //r.text = (it.evtResDeliverdNeighbor as ParcelServiceV1.EvtResDeliveredNeighbor).nameNeighbor
                val neighborEvent = it.deliveredAtNeighborInfo
                r.text = neighborEvent?.name ?: "" + " " + neighborEvent?.address ?: ""
                r.kzStatuserzeuger = "E"
                r.kzStatus = 4.toUInteger()
                saveSignature(it.time, (it.deliveredAtNeighborInfo as ParcelServiceV1.DeliveredAtNeighborInfo).signature, it.parcelNumber, message.nodeId)
            }
            if (it.notDeliveredRefusedInfo != null) {
                r.infotext = (it.notDeliveredRefusedInfo as ParcelServiceV1.NotDeliveredRefusedInfo).cause
                r.kzStatuserzeuger = "E"
                r.kzStatus = 8.toUInteger()
                r.fehlercode = 99.toUInteger()
            }
//            if (it.evtReasonNotDeliveredWrongAddress != null) {
//                r.kzStatuserzeuger = "E"
//                r.kzStatus = 8.toUInteger()
//                r.fehlercode = it.evtReasonNotDeliveredWrongAddress?.eventReason?.toUInteger() ?: 0.toUInteger()
//            }
            parcelRepository.saveEvent(r)
        }
    }

    fun saveSignature(date: Date, signatureBase64: String?, number: String, nodeId: String?) {

        var path = "c:\\deku2004\\SynchToSaveServer\\" +
                SimpleDateFormat("yyyy").parse(date.toLocalDate().toString()) + "\\SB\\" +
                SimpleDateFormat("MM").parse(date.toString()) + "\\" +
                SimpleDateFormat("dd").parse(date.toString())

        //create path if not exsists

        //  signatureBase64.decode64
        //  convert to .bmp

        var file = number + "_" + nodeId.toString() + "_" + SimpleDateFormat("yyyyMMddHHmmssSSS").parse(date.toLocalDate().toString()) + "_MOB.bmp"
        // 2017\SB\06\09\83352287467_1804_2017060908550500_sca.bmp
        // .save file

        // Set Reference in parcelrecord
        parcelRepository.setSignaturePath(number, file)

        //false -> log

        return

    }
}

