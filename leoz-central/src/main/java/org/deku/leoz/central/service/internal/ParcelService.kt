package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.central.data.jooq.tables.records.TblstatusRecord
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.model.AdditionalInfo
import org.deku.leoz.model.Event
import org.deku.leoz.model.Reason
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

        //val events = message.events?.toList()
        val events = message.events?.toList()
                ?: throw DefaultProblem(
                detail = "Missing data",
                status = Response.Status.BAD_REQUEST)

        log.trace("Received ${events.count()} from [${message.nodeId}] user [${message.userId}]")

        events.forEach {
            val r = dslContext.newRecord(Tables.TBLSTATUS)
            val parcelScan = it.parcelScancode
            if (parcelScan.isEmpty())
                throw DefaultProblem(
                        title = "Missing parcelScan"
                )
            val parcelNo: Double?
            if (!parcelScan.all { it.isDigit() })
                parcelNo = parcelRepository.getUnitNo(it.parcelId)
            else {
                parcelNo = parcelScan.toDouble()
            }

            parcelNo ?:
                    throw DefaultProblem(
                            title = "Missing parcelNo"
                    )
            r.packstuecknummer = parcelNo
            //r.packstuecknummer = it.parcelScancode.toDouble()
//            r.erzeugerstation = it.eventValue.toString()
            //r.datum = SimpleDateFormat("yyyyMMdd").parse(it.time.toLocalDate().toString()).toString()
            r.setDate(it.time)
            //r.zeit = SimpleDateFormat("HHmm").parse(it.time.toLocalDate().toString()).toString()
            r.setTime(it.time)
            r.poslat = it.latitude
            r.poslong = it.longitude

            //TODO: Die Werte kz_status und -erzeuger sollten vermutlich über die Enumeration gesetzt werden, damit man die (aktuellen) Primärschlüssel nicht an mehreren Stellen pflegen muss, oder?
            val eventId = it.event
            val event = Event.values().find { it.value == eventId }!!
            r.kzStatuserzeuger = event.creator.toString()
            r.kzStatus = event.concatId.toUInteger()

            val reasonId = it.reason
            val reason = Reason.values().find { it.id == reasonId }!!
            r.fehlercode = reason.oldValue.toUInteger()

            r.erzeugerstation = "002"

            val parcelRecord = parcelRepository.findParcelByUnitNumber(parcelNo)
            parcelRecord ?:
                    throw DefaultProblem(
                            title = "Missing parcelRecord"
                    )
            val orderRecord = parcelRepository.findOrderByOrderNumber(parcelRecord.orderid)
            orderRecord ?:
                    throw DefaultProblem(
                            title = "Missing orderRecord"
                    )
            val pasClearingartmaster = orderRecord.clearingartmaster
            val pasCleared: Boolean
            if (pasClearingartmaster != null) {
                pasCleared = (4096.and(pasClearingartmaster.toInt())) == 4096
            } else
                pasCleared = false

            when (event) {
                Event.DELIVERED -> {
                    when (reason) {
                        Reason.NORMAL -> {
                            //if (it.deliveredInfo == null)
                            if (it.additionalInfo == null)
                                throw DefaultProblem(
                                        title = "Missing structure [DeliveredInfo] for event [$event].[$reason]"
                                )

                            val addInfo = it.additionalInfo
                            when (addInfo) {
                                is AdditionalInfo.EmptyInfo -> throw DefaultProblem(
                                        title = "Missing structure [DeliveredInfo] for event [$event].[$reason]"
                                )
                                is AdditionalInfo.DeliveredInfo -> {

                                    if (pasCleared) {
                                        //TODO WLtransfer Auslieferdaten nach Abrechnung
                                    }

                                    r.text = addInfo.recipient ?: ""
                                    //saveSignature(it.time, addInfo.signature, it.parcelScancode, message.nodeId)
                                    val sigPath = saveSignature(it.time, addInfo.signature, parcelScan, message.nodeId)
                                    parcelRecord.bmpfilename = sigPath

                                    val oldValue = parcelRecord.lieferstatus
                                    parcelRecord.lieferstatus = r.kzStatus.toShort() //4
                                    if (parcelRecord.store() > 0) {
                                        val fieldHistoryRecord = dslContext.newRecord(Tables.TBLFELDHISTORIE)
                                        fieldHistoryRecord.orderid = parcelRecord.orderid
                                        fieldHistoryRecord.belegnummer = parcelRecord.colliebelegnr
                                        fieldHistoryRecord.feldname = "lieferstatus"
                                        fieldHistoryRecord.oldvalue = oldValue?.toString() ?: ""
                                        fieldHistoryRecord.newvalue = r.kzStatus.toString()//"4"
                                        fieldHistoryRecord.changer = "SP"
                                        fieldHistoryRecord.point = "IM"
                                        fieldHistoryRecord.store()
                                    }

                                    val oldRecipient = orderRecord.empfaenger ?: ""
                                    orderRecord.empfaenger = r.text
                                    if (orderRecord.store() > 0 && !oldRecipient.equals(r.text)) {
                                        val fieldHistoryRecord = dslContext.newRecord(Tables.TBLFELDHISTORIE)
                                        fieldHistoryRecord.orderid = parcelRecord.orderid
                                        fieldHistoryRecord.belegnummer = parcelRecord.colliebelegnr
                                        fieldHistoryRecord.feldname = "empfaenger"
                                        fieldHistoryRecord.oldvalue = oldRecipient
                                        fieldHistoryRecord.newvalue = r.text
                                        fieldHistoryRecord.changer = "I"
                                        fieldHistoryRecord.point = "IM"
                                        fieldHistoryRecord.store()
                                    }
                                    if (!orderRecord.empfaenger.equals(r.text)) {
                                        //TODO WLtransfer ASD D in Auftrag gescheitert
                                    }

                                    val oldDeliveryDate = orderRecord.dtauslieferdatum
                                    val oldDeliveryTime = orderRecord.dtauslieferzeit


                                    //
                                    val from = it.from
                                    if (from != null) {
                                        if (from.equals("956") || from.equals("935"))
                                            if (parcelNo.toString().startsWith("10071")) {
                                                val bagRecords = parcelRepository.findBagsByUnitNumber(parcelNo)
                                                if (bagRecords != null) {
                                                    bagRecords.forEach {

                                                    }
                                                }

                                            }
                                    }
                                }
                            }
                        }
                        Reason.NEIGHBOUR -> {
                            val addInfo = it.additionalInfo
                            when (addInfo) {
                                is AdditionalInfo.EmptyInfo -> throw DefaultProblem(
                                        title = "Missing structure [DeliveredInfo] for event [$event].[$reason]"
                                )
                                is AdditionalInfo.DeliveredAtNeighborInfo -> {
                                    if (pasCleared) {
                                        //TODO WLtransfer
                                    }

                                    r.text = addInfo.name ?: "" + ";adr " + addInfo.address ?: ""
                                    //saveSignature(it.time, addInfo.signature, it.parcelScancode, message.nodeId)
                                    val sigPath = saveSignature(it.time, addInfo.signature, parcelScan, message.nodeId)
                                    parcelRecord.bmpfilename = sigPath

                                    val oldValue = parcelRecord.lieferstatus
                                    parcelRecord.lieferstatus = r.kzStatus.toShort()//4
                                    if (parcelRecord.store() > 0) {
                                        val fieldHistoryRecord = dslContext.newRecord(Tables.TBLFELDHISTORIE)
                                        fieldHistoryRecord.orderid = parcelRecord.orderid
                                        fieldHistoryRecord.belegnummer = parcelRecord.colliebelegnr
                                        fieldHistoryRecord.feldname = "lieferstatus"
                                        fieldHistoryRecord.oldvalue = oldValue?.toString() ?: ""
                                        fieldHistoryRecord.newvalue = r.kzStatus.toString()//"4"
                                        fieldHistoryRecord.changer = "SP"
                                        fieldHistoryRecord.point = "IM"
                                        //getTan
                                        // fieldHistoryRecord.id=
                                        fieldHistoryRecord.store()
                                    }

                                    val oldRecipient = orderRecord.empfaenger ?: ""
                                    orderRecord.empfaenger = r.text
                                    if (orderRecord.store() > 0 && !oldRecipient.equals(r.text)) {
                                        val fieldHistoryRecord = dslContext.newRecord(Tables.TBLFELDHISTORIE)
                                        fieldHistoryRecord.orderid = parcelRecord.orderid
                                        fieldHistoryRecord.belegnummer = parcelRecord.colliebelegnr
                                        fieldHistoryRecord.feldname = "empfaenger"
                                        fieldHistoryRecord.oldvalue = oldRecipient
                                        fieldHistoryRecord.newvalue = r.text
                                        fieldHistoryRecord.changer = "I"
                                        fieldHistoryRecord.point = "IM"
                                        fieldHistoryRecord.store()
                                    }


                                }
                            }
                        }
                        Reason.CUSTOMER_REFUSED -> {
                            val addInfo = it.additionalInfo
                            when (addInfo) {
                                is AdditionalInfo.EmptyInfo -> throw DefaultProblem(
                                        title = "Missing structure [DeliveredInfo] for event [$event].[$reason]"
                                )
                                is AdditionalInfo.NotDeliveredRefusedInfo -> {
                                    r.infotext = addInfo.cause ?: ""
                                }
                            }
                        }

                    }
                }

                Event.DELIVERY_FAIL -> {

                }
            }
            /**
            // TODO: check mainly for event/reason, then check for additional info structures as applicable
            if (it.deliveredAtNeighborInfo != null) {
            //r.text = (it.evtResDeliverdNeighbor as ParcelServiceV1.EvtResDeliveredNeighbor).nameNeighbor
            val neighborEvent = it.deliveredAtNeighborInfo
            r.text = neighborEvent?.name ?: "" + ";adr " + neighborEvent?.address ?: ""
            //                r.kzStatuserzeuger = "E"
            //                r.kzStatus = 4.toUInteger()
            saveSignature(it.time, (it.deliveredAtNeighborInfo as ParcelServiceV1.DeliveredAtNeighborInfo).signature, it.parcelNumber, message.nodeId)
            }
            if (it.notDeliveredRefusedInfo != null) {
            r.infotext = (it.notDeliveredRefusedInfo as ParcelServiceV1.NotDeliveredRefusedInfo).cause
            //                r.kzStatuserzeuger = "E"
            //                r.kzStatus = 8.toUInteger()
            //                r.fehlercode = 99.toUInteger()
            }
            //            if (it.evtReasonNotDeliveredWrongAddress != null) {
            //                r.kzStatuserzeuger = "E"
            //                r.kzStatus = 8.toUInteger()
            //                r.fehlercode = it.evtReasonNotDeliveredWrongAddress?.eventReason?.toUInteger() ?: 0.toUInteger()
            //            }
             **/
            parcelRepository.saveEvent(r)
        }
    }

    fun saveSignature(date: Date, signatureBase64: String?, number: String, nodeId: String?): String {

        var path = "c:\\deku2004\\SynchToSaveServer\\" +
                SimpleDateFormat("yyyy").format(date) + "\\SB\\" +
                SimpleDateFormat("MM").format(date) + "\\" +
                SimpleDateFormat("dd").format(date)

        //create path if not exsists

        //  signatureBase64.decode64
        //  convert to .bmp

        var file = number + "_" + nodeId.toString() + "_" + SimpleDateFormat("yyyyMMddHHmmssSSS").format(date) + "_MOB.bmp"
        // 2017\SB\06\09\83352287467_1804_2017060908550500_sca.bmp
        // .save file

        // Set Reference in parcelrecord
        //parcelRepository.setSignaturePath(number, file)

        //false -> log

        return file

    }
}

