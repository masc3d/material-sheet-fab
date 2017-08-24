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
import org.deku.leoz.central.data.jooq.Routines
import org.deku.leoz.node.Storage
import org.deku.leoz.time.toShortTime
import org.springframework.transaction.annotation.Transactional
import sx.time.toSqlDate
import sx.time.toTimestamp
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Paths
import java.sql.Timestamp
import javax.imageio.ImageIO
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.JPEGTranscoder
import org.apache.batik.transcoder.*
import org.deku.leoz.model.*
import org.deku.leoz.time.toDateOnlyTime
import org.deku.leoz.time.toDateWithoutTime
import org.deku.leoz.time.toString_ddMMyyyy_PointSeparated
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import javax.imageio.IIOImage
import javax.ws.rs.core.MediaType
import javax.imageio.*
import javax.imageio.plugins.bmp.BMPImageWriteParam

/**
 * Parcel service v1 implementation
 * Created by JT on 17.07.17.
 */
@Named
@ApiKey(false)
@Path("internal/v1/event")
open class ParcelServiceV1 :
        org.deku.leoz.service.internal.ParcelServiceV1,
        MqHandler<ParcelServiceV1.ParcelMessage> {

    private val log = LoggerFactory.getLogger(this.javaClass)
    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext
    @Inject
    private lateinit var parcelRepository: ParcelJooqRepository

    @Inject
    private lateinit var fieldHistoryRepository: FieldHistoryJooqRepository

    @Inject
    private lateinit var storage: Storage

    /**
     * Parcel service message handler
     */
    @Transactional(PersistenceConfiguration.QUALIFIER)
    override fun onMessage(message: ParcelServiceV1.ParcelMessage, replyChannel: MqChannel?) {
        log.debug(message.toString())

        //val events = message.events?.toList()
        val events = message.events?.toList()
                ?: throw DefaultProblem(
                detail = "Missing data",
                status = Response.Status.BAD_REQUEST)

        log.trace("Received ${events.count()} from [${message.nodeId}] user [${message.userId}]")

        events.forEach {
            var insertStatus = true
            val r = dslContext.newRecord(Tables.TBLSTATUS)
            //val parcelScan = it.parcelScancode
            /*if (parcelScan.isEmpty())
                throw DefaultProblem(
                        title = "Missing parcelScan"
                )
                */
            val parcelNo: Double?
            //if (!parcelScan.all { it.isDigit() })
            parcelNo = parcelRepository.getUnitNo(it.parcelId)
            /*else {
                parcelNo = parcelScan.toDouble()
            }*/
            parcelNo ?:
                    throw DefaultProblem(
                            title = "Missing parcelId"
                    )

            val parcelScan = parcelNo.toLong().toString()

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

            r.infotext = message.nodeId.toString().substringBefore("-")// "ScannerXY"

            //TODO: Die Werte kz_status und -erzeuger sollten vermutlich über die Enumeration gesetzt werden, damit man die (aktuellen) Primärschlüssel nicht an mehreren Stellen pflegen muss, oder?
            val eventId = it.event
            val event = Event.values().find { it.value == eventId }!!
            r.kzStatuserzeuger = event.creator.toString()
            r.kzStatus = event.concatId.toUInteger()
            r.timestamp2 = Date().toTimestamp()
            val reasonId = it.reason
            val reason = Reason.values().find { it.id == reasonId }!!
            r.fehlercode = reason.oldValue.toUInteger()

            val from = it.from
            r.erzeugerstation = "002"
            if (it.fromStation) {
                if (from != null) {
                    r.erzeugerstation = from
                }
            }

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
            var pasReset = false

            when (event) {
                Event.DELIVERED -> {
                    pasReset = true
                    val recipientInfo = StringBuilder()
                    var signature: String? = null
                    var mimetype = "svg"
                    when (reason) {
                        Reason.POSTBOX -> {
                            recipientInfo.append("Postbox")
                        }
                        Reason.NORMAL -> {
                            //if (it.deliveredInfo == null)
//                            if (it.additionalInfo == null)
//                                throw DefaultProblem(
//                                        title = "Missing structure [DeliveredInfo] for event [$event].[$reason]"
//                                )
                            if (message.deliveredInfo == null)
                                throw DefaultProblem(
                                        title = "Missing structure [DeliveredInfo] for event [$event].[$reason]"
                                )
                            recipientInfo.append((message.deliveredInfo as ParcelServiceV1.ParcelMessage.DeliveredInfo).recipient ?: "")
                            signature = (message.deliveredInfo as ParcelServiceV1.ParcelMessage.DeliveredInfo).signature
                            mimetype = (message.deliveredInfo as ParcelServiceV1.ParcelMessage.DeliveredInfo).mimetype
                            //val addInfo = it. additionalInfo
                            //recipientInfo.append(.deliveredInfo.recipient ?: "")
                            //signature = event.declaringClass.  message.deliveredInfo.signature
                            //mimetype = message.deliveredInfo.mimetype
/*

                            val addInfo = it.additionalInfo
                            when (addInfo) {
                                is AdditionalInfo.EmptyInfo -> throw DefaultProblem(
                                        title = "Missing structure [DeliveredInfo] for event [$event].[$reason]"
                                )
                                is AdditionalInfo.DeliveredInfo -> {

                                    if (pasCleared) {
                                        //TODO WLtransfer Auslieferdaten nach Abrechnung
                                    }
                                    recipientInfo.append(addInfo.recipient ?: "")
                                    signature = addInfo.signature
                                    mimetype = addInfo.mimetype
                                }

                            }
*/
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
                                    //val recipientInfo = StringBuilder()
                                    recipientInfo.append(addInfo.name ?: "").append(";adr ").append(addInfo.address ?: "")
                                    signature = addInfo.signature
                                    mimetype = addInfo.mimetype
                                }
                            }
                        }


                    }
                    r.text = recipientInfo.toString()
                    if (signature != null) {
                        val sigPath = saveImage(it.time, Location.SB, signature, parcelScan, message.nodeId, mimetype, Location.SB_Original)
                        if (sigPath != "")
                            parcelRecord.bmpfilename = sigPath
                    }

                    val oldValue = parcelRecord.lieferstatus
                    parcelRecord.lieferstatus = r.kzStatus.toShort() //4
                    parcelRecord.erstlieferstatus = r.kzStatus.toShort()
                    if (parcelRecord.store() > 0) {

                        fieldHistoryRepository.addEntry(
                                orderId = parcelRecord.orderid,
                                unitNo = parcelRecord.colliebelegnr,
                                fieldName = "lieferstatus",
                                oldValue = oldValue?.toString() ?: "",
                                newValue = r.kzStatus.toString(),
                                changer = "SP",
                                point = "IM"
                        )
                    }

                    val oldRecipient = orderRecord.empfaenger ?: ""
                    orderRecord.empfaenger = r.text
                    if (orderRecord.store() > 0 && !oldRecipient.equals(r.text)) {


                        fieldHistoryRepository.addEntry(
                                orderId = parcelRecord.orderid,
                                unitNo = parcelRecord.colliebelegnr,
                                fieldName = "empfaenger",
                                oldValue = oldRecipient,
                                newValue = r.text,
                                changer = "I",
                                point = "IM"
                        )
                    }
                    if (!orderRecord.empfaenger.equals(r.text)) {
                        //TODO WLtransfer ASD D in Auftrag gescheitert
                    }

                    val oldDeliveryDate: String //= orderRecord.dtauslieferdatum?.toLocalDate().toString() ?: ""
                    if (orderRecord.dtauslieferdatum == null) {
                        oldDeliveryDate = ""
                    } else {
                        //oldDeliveryDate = SimpleDateFormat("dd.MM.yyyy").format(orderRecord.dtauslieferdatum)
                        oldDeliveryDate = orderRecord.dtauslieferdatum.toString_ddMMyyyy_PointSeparated()
                    }
                    val oldDeliveryTime = orderRecord.dtauslieferzeit?.toShortTime().toString() ?: ""
                    //val deliveryTime = SimpleDateFormat("yyyy-MM-dd HH:mm").parse("1899-12-30 " + (it.time.toShortTime().toString()))
                    val deliveryTime = it.time.toDateOnlyTime()
                    //val deliveryDate = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(it.time.toLocalDate().toString() + (" 00:00"))
                    val deliveryDate = it.time.toDateWithoutTime()

                    orderRecord.dtauslieferdatum = deliveryDate.toTimestamp()
                    orderRecord.dtauslieferzeit = deliveryTime.toTimestamp()
                    if (orderRecord.store() > 0) {
                        if (oldDeliveryTime != it.time.toTimestamp().toShortTime().toString()) {

                            fieldHistoryRepository.addEntry(
                                    orderId = parcelRecord.orderid,
                                    unitNo = parcelRecord.colliebelegnr,
                                    fieldName = "dtauslieferzeit",
                                    oldValue = oldDeliveryTime,
                                    newValue = it.time.toTimestamp().toShortTime().toString(),
                                    changer = "I",
                                    point = "IM"
                            )

                        }
                        //if (oldDeliveryDate != it.time.toTimestamp().toLocalDate().toString()) {
                        //if (oldDeliveryDate != SimpleDateFormat("dd.MM.yyyy").format(it.time)) {
                        if (oldDeliveryDate != it.time.toString_ddMMyyyy_PointSeparated()) {


                            fieldHistoryRepository.addEntry(
                                    orderId = parcelRecord.orderid,
                                    unitNo = parcelRecord.colliebelegnr,
                                    fieldName = "dtauslieferdatum",
                                    oldValue = oldDeliveryDate,
                                    newValue = it.time.toString_ddMMyyyy_PointSeparated(), //SimpleDateFormat("dd.MM.yyyy").format(it.time),
                                    changer = "I",
                                    point = "IM"
                            )
                        }

                    }
                    //

                    if (from != null) {
                        if (from.equals("956") || from.equals("935"))
                            if (UnitNumber.parse(parcelScan).value.type == UnitNumber.Type.Bag) {
                                //if (parcelScan.startsWith("10071")) {
                                val unitInBagUnitRecords = parcelRepository.findUnitsInBagByBagUnitNumber(parcelNo)
                                if (unitInBagUnitRecords != null) {
                                    unitInBagUnitRecords.forEach {
                                        val unitInBagStatusRecord = dslContext.newRecord(Tables.TBLSTATUS)
                                        unitInBagStatusRecord.packstuecknummer = it.colliebelegnr
                                        unitInBagStatusRecord.datum = r.datum
                                        unitInBagStatusRecord.zeit = r.zeit
                                        unitInBagStatusRecord.poslat = r.poslat
                                        unitInBagStatusRecord.poslong = r.poslong
                                        unitInBagStatusRecord.kzStatuserzeuger = r.kzStatuserzeuger
                                        unitInBagStatusRecord.kzStatus = r.kzStatus
                                        unitInBagStatusRecord.erzeugerstation = r.erzeugerstation
                                        unitInBagStatusRecord.fehlercode = r.fehlercode
                                        unitInBagStatusRecord.text = r.text
                                        unitInBagStatusRecord.infotext = r.infotext
                                        r.store()

                                        val unitInBagOrderRecord = parcelRepository.findOrderByOrderNumber(it.orderid)
                                        if (unitInBagOrderRecord != null) {
                                            val unitInBagPasClearingartmaster = unitInBagOrderRecord.clearingartmaster
                                            val unitInBagPasCleared: Boolean
                                            if (unitInBagPasClearingartmaster != null) {
                                                unitInBagPasCleared = (4096.and(unitInBagPasClearingartmaster.toInt())) == 4096
                                            } else
                                                unitInBagPasCleared = false
                                            if (unitInBagPasCleared) {
                                                //TODO WLtransfer Auslieferdaten nach Abrechnung
                                            }
                                            it.bmpfilename = parcelRecord.bmpfilename
                                            val unitInBagOldValue = it.lieferstatus
                                            it.lieferstatus = r.kzStatus.toShort() //4
                                            if (it.store() > 0) {


                                                fieldHistoryRepository.addEntry(
                                                        orderId = it.orderid,
                                                        unitNo = it.colliebelegnr,
                                                        fieldName = "lieferstatus",
                                                        oldValue = unitInBagOldValue?.toString() ?: "",
                                                        newValue = r.kzStatus.toString(),
                                                        changer = "SP",
                                                        point = "IM"
                                                )
                                            }

                                            val unitInBagOldRecipient = unitInBagOrderRecord.empfaenger ?: ""
                                            unitInBagOrderRecord.empfaenger = r.text
                                            if (unitInBagOrderRecord.store() > 0 && !unitInBagOldRecipient.equals(r.text)) {


                                                fieldHistoryRepository.addEntry(
                                                        orderId = it.orderid,
                                                        unitNo = it.colliebelegnr,
                                                        fieldName = "empfaenger",
                                                        oldValue = unitInBagOldRecipient,
                                                        newValue = r.text,
                                                        changer = "I",
                                                        point = "IM"
                                                )
                                            }
                                            if (!unitInBagOrderRecord.empfaenger.equals(r.text)) {
                                                //TODO WLtransfer ASD D in Auftrag gescheitert
                                            }

                                            //val unitInBagOldDeliveryDate = unitInBagOrderRecord.dtauslieferdatum?.toLocalDate().toString() ?: ""
                                            val unitInBagOldDeliveryDate: String
                                            if (unitInBagOrderRecord.dtauslieferdatum == null) {
                                                unitInBagOldDeliveryDate = ""
                                            } else {
                                                //unitInBagOldDeliveryDate = SimpleDateFormat("dd.MM.yyyy").format(unitInBagOrderRecord.dtauslieferdatum)
                                                unitInBagOldDeliveryDate = unitInBagOrderRecord.dtauslieferdatum.toString_ddMMyyyy_PointSeparated()
                                            }
                                            val unitInBagOldDeliveryTime = unitInBagOrderRecord.dtauslieferzeit?.toShortTime().toString() ?: ""


                                            unitInBagOrderRecord.dtauslieferdatum = deliveryDate.toTimestamp()
                                            unitInBagOrderRecord.dtauslieferzeit = deliveryTime.toTimestamp()
                                            if (unitInBagOrderRecord.store() > 0) {
                                                if (unitInBagOldDeliveryTime != deliveryTime.toTimestamp().toShortTime().toString()) {


                                                    fieldHistoryRepository.addEntry(
                                                            orderId = it.orderid,
                                                            unitNo = it.colliebelegnr,
                                                            fieldName = "dtauslieferzeit",
                                                            oldValue = unitInBagOldDeliveryTime,
                                                            newValue = deliveryTime.toTimestamp().toShortTime().toString(),
                                                            changer = "I",
                                                            point = "IM"
                                                    )
                                                }
                                                //if (unitInBagOldDeliveryDate != deliveryDate.toTimestamp().toLocalDate().toString()) {
                                                //if (unitInBagOldDeliveryDate != SimpleDateFormat("dd.MM.yyyy").format(deliveryDate)) {
                                                if (unitInBagOldDeliveryDate != deliveryDate.toString_ddMMyyyy_PointSeparated()) {


                                                    fieldHistoryRepository.addEntry(
                                                            orderId = it.orderid,
                                                            unitNo = it.colliebelegnr,
                                                            fieldName = "dtauslieferdatum",
                                                            oldValue = unitInBagOldDeliveryDate,
                                                            newValue = deliveryDate.toString_ddMMyyyy_PointSeparated(), //SimpleDateFormat("dd.MM.yyyy").format(deliveryDate),
                                                            changer = "I",
                                                            point = "IM"
                                                    )
                                                }

                                            }

                                        }
                                    }
                                }

                            }
                    }
                }

                Event.DELIVERY_FAIL -> {

                    val firstDeliveryStatus = parcelRecord.erstlieferstatus ?: 0
                    if (firstDeliveryStatus.toInt() != (4)) {
                        pasReset = true
                        if (pasCleared) {
                            //TODO WLtransfer Auslieferung nach Abrechnung
                        }
                        if (firstDeliveryStatus.toInt() == 0) {
                            val oldDeliveryStatus = parcelRecord.lieferstatus?.toString() ?: ""
                            val oldDeliveryError = parcelRecord.lieferfehler?.toString() ?: ""
                            parcelRecord.lieferstatus = r.kzStatus.toShort()
                            parcelRecord.lieferfehler = r.fehlercode.toShort()
                            if (parcelRecord.store() > 0) {
                                if (!oldDeliveryStatus.equals(parcelRecord.lieferstatus.toString())) {
                                    fieldHistoryRepository.addEntry(
                                            orderId = parcelRecord.orderid,
                                            unitNo = parcelRecord.colliebelegnr,
                                            fieldName = "lieferstatus",
                                            oldValue = oldDeliveryStatus,
                                            newValue = parcelRecord.lieferstatus.toString(),
                                            changer = "SP",
                                            point = "IM"
                                    )
                                }
                                if (!oldDeliveryError.equals(parcelRecord.lieferfehler.toString())) {
                                    fieldHistoryRepository.addEntry(
                                            orderId = parcelRecord.orderid,
                                            unitNo = parcelRecord.colliebelegnr,
                                            fieldName = "lieferfehler",
                                            oldValue = oldDeliveryError,
                                            newValue = parcelRecord.lieferfehler.toString(),
                                            changer = "SP",
                                            point = "IM"
                                    )
                                }
                            }
                        }
                    }
                    val addInfo = it.additionalInfo
                    if (addInfo != null) {
                        when (addInfo) {
                            is AdditionalInfo.NotDeliveredInfo -> {
                                r.infotext = addInfo.text ?: ""
                            }
                        }
                    }
                    when (reason) {
                        Reason.CUSTOMER_REFUSED -> {

                            when (addInfo) {
                                is AdditionalInfo.EmptyInfo -> throw DefaultProblem(
                                        title = "Missing structure [DeliveredInfo] for event [$event].[$reason]"
                                )
                                is AdditionalInfo.NotDeliveredRefusedInfo -> {
                                    r.infotext = addInfo.cause ?: ""
                                }

                            }
                        }
                        Reason.PARCEL_DAMAGED -> {
                            when (addInfo) {
                                is AdditionalInfo.DamagedInfo -> {
                                    r.infotext = addInfo.description ?: ""
                                    if (addInfo.photo != null) {
//                                        val path = SimpleDateFormat("yyyy").format(it.time) + "/sca_pic/" +
//                                                SimpleDateFormat("MM").format(it.time) + "/" +
//                                                SimpleDateFormat("dd").format(it.time) + "/"
//
//                                        saveImage(it.time, addInfo.photo, parcelScan, message.nodeId, addInfo.mimetype)
                                    }
                                }
                            }
                        }

                    }

                }
                Event.IMPORT_RECEIVE -> {


                    val oldValue: String//= parcelRecord.dteingangdepot2
                    if (parcelRecord.dteingangdepot2 == null) {
                        oldValue = ""
                    } else {
                        //oldValue = SimpleDateFormat("dd.MM.yyyy").format(parcelRecord.dteingangdepot2)
                        oldValue = parcelRecord.dteingangdepot2.toString_ddMMyyyy_PointSeparated()
                    }
                    val importDate = it.time.toDateWithoutTime()//SimpleDateFormat("yyyy-MM-dd HH:mm").parse(it.time.toLocalDate().toString() + (" 00:00"))
                    parcelRecord.dteingangdepot2 = importDate.toTimestamp()// it.time.toTimestamp()
                    if (parcelRecord.store() > 0) {
                        //if (oldValue != SimpleDateFormat("dd.MM.yyyy").format(it.time)) {
                        if (oldValue != it.time.toString_ddMMyyyy_PointSeparated()) {

                            fieldHistoryRepository.addEntry(
                                    orderId = parcelRecord.orderid,
                                    unitNo = parcelRecord.colliebelegnr,
                                    fieldName = "dteingangdepot2",
                                    oldValue = oldValue,
                                    newValue = it.time.toString_ddMMyyyy_PointSeparated(), //SimpleDateFormat("dd.MM.yyyy").format(it.time),
                                    changer = "SP",
                                    point = "IM"
                            )
                        }
                    }
                }
                Event.IN_DELIVERY -> {
                    //ticket #260
                    var existStatus = parcelRepository.statusExist(parcelRecord.colliebelegnr, "H", 2)
                    if (!existStatus)
                        existStatus = parcelRepository.statusExist(parcelRecord.colliebelegnr, "H", 4)
                    if (!existStatus)
                        existStatus = parcelRepository.statusExist(parcelRecord.colliebelegnr, "E", 1)
                    if (!existStatus)
                        insertStatus = false
                }
                Event.EXPORT_LOADED -> {
                    if (it.additionalInfo == null)
                        throw DefaultProblem(
                                title = "Missing structure [LoadingListInfo] for event [$event].[$reason]"
                        )

                    val addInfo = it.additionalInfo
                    when (addInfo) {
                        is AdditionalInfo.EmptyInfo -> throw DefaultProblem(
                                title = "Missing structure [LoadingListInfo] for event [$event].[$reason]"
                        )
                        is AdditionalInfo.LoadingListInfo -> {
                            r.text = addInfo.loadingListNo.toString()
                            val oldSend = orderRecord.dtsendad2z?.toString() ?: ""
                            orderRecord.dtsendad2z = it.time.toTimestamp()
                            if (orderRecord.store() > 0) {
                                if (!oldSend.equals(orderRecord.dtsendad2z.toString())) {
                                    fieldHistoryRepository.addEntry(
                                            orderId = parcelRecord.orderid,
                                            unitNo = parcelRecord.colliebelegnr,
                                            fieldName = "dtsendad2z",
                                            oldValue = oldSend,
                                            newValue = orderRecord.dtsendad2z.toString(),
                                            changer = "I",
                                            point = ""
                                    )
                                }

                            }
                            val depotOutDate = it.time.toDateWithoutTime()//SimpleDateFormat("yyyy-MM-dd HH:mm").parse(it.time.toLocalDate().toString() + (" 00:00"))
                            val oldDepotOut: String
                            if (parcelRecord.dtausgangdepot2 == null) {
                                oldDepotOut = ""
                            } else {
                                //oldDepotOut = SimpleDateFormat("dd.MM.yyyy").format(parcelRecord.dtausgangdepot2)
                                oldDepotOut = parcelRecord.dtausgangdepot2.toString_ddMMyyyy_PointSeparated()
                            }
                            parcelRecord.dtausgangdepot2 = depotOutDate.toTimestamp()
                            if (parcelRecord.store() > 0) {
                                //if (oldDepotOut != SimpleDateFormat("dd.MM.yyyy").format(it.time)) {
                                if (oldDepotOut != it.time.toString_ddMMyyyy_PointSeparated()) {

                                    fieldHistoryRepository.addEntry(
                                            orderId = parcelRecord.orderid,
                                            unitNo = parcelRecord.colliebelegnr,
                                            fieldName = "dtausgangdepot2",
                                            oldValue = oldDepotOut,
                                            newValue = it.time.toString_ddMMyyyy_PointSeparated(), //SimpleDateFormat("dd.MM.yyyy").format(it.time),
                                            changer = "SP",
                                            point = "IM"
                                    )
                                }
                            }
                            val oldLoadingNo = parcelRecord.ladelistennummerd?.toString() ?: ""
                            parcelRecord.ladelistennummerd = addInfo.loadingListNo.toDouble()
                            if (parcelRecord.store() > 0) {
                                if (!oldLoadingNo.equals(addInfo.loadingListNo.toString())) {
                                    fieldHistoryRepository.addEntry(
                                            orderId = parcelRecord.orderid,
                                            unitNo = parcelRecord.colliebelegnr,
                                            fieldName = "ladelistennummerd",
                                            oldValue = oldLoadingNo,
                                            newValue = addInfo.loadingListNo.toString(),
                                            changer = "SP",
                                            point = "IM"
                                    )
                                }
                            }
                        }
                    }
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
            if (insertStatus) {
                parcelRepository.saveEvent(r)
            }
        }
    }

    fun saveImage(date: Date, location: Location, image: String?, number: String, nodeId: String?, mimetype: String, locationOriginal: Location?): String {
        if (image != null) {
            val keepOriginal = if (locationOriginal != null) true else false
            val pathMobile = storage.mobileDataDirectory.toPath()

            val addInfo = nodeId.toString().substringBefore("-")
            val mobileFilename = FileName(number, date, location, pathMobile, addInfo)
            val relPathMobile = mobileFilename.getPath()

            val path = storage.workTmpDataDirectory.toPath()

            val mobileWorkFilename = FileName(number, date, location, path, addInfo)
            val relPath = mobileWorkFilename.getPath()

            var fileExtension: String
            when (mimetype) {
                MediaType.APPLICATION_SVG_XML -> fileExtension = "svg"
                else -> fileExtension = "jpg"
            }
            val file = mobileFilename.getFilenameWithoutExtension() + "." + fileExtension
            val pathFile = relPath.resolve(file).toFile().toPath()
            val pathFileMobile = relPathMobile.resolve(file).toFile().toPath()



            try {
                var imgPath = pathFile
                if (fileExtension.equals("svg")) {
                    Files.write(pathFile, image.toByteArray()!!, java.nio.file.StandardOpenOption.CREATE_NEW).toString()
                    imgPath = transSvg2Jpg(pathFile)
                } else {
                    val img = Base64.getDecoder().decode(image)
                    Files.write(pathFile, img, java.nio.file.StandardOpenOption.CREATE_NEW).toString()
                }

                if (keepOriginal) {
                    val mobileOriginalFilename = FileName(number, date, locationOriginal!!, pathMobile, addInfo)
                    val relPathMobileOriginal = mobileOriginalFilename.getPath()
                    val pathFileMobileOriginal = relPathMobileOriginal.resolve(file).toFile().toPath()
                    Files.copy(pathFile, pathFileMobileOriginal)
                }


                val bmpFile = imgPath.toFile().parentFile.toPath()
                        .resolve(imgPath.toFile().nameWithoutExtension + ".bmp").toFile()
                val bmpFileMobile = pathFileMobile.toFile().parentFile.toPath()
                        .resolve(imgPath.toFile().nameWithoutExtension + ".bmp").toFile()


                var ret: String
                if (fileExtension.equals("svg")) {
                    if (writeAsBMP(imgPath, bmpFile.toPath())) {
                        Files.copy(bmpFile.toPath(), bmpFileMobile.toPath())
                        ret = bmpFileMobile.toString().substringAfter(pathMobile.toString()).substring(1)
                    } else
                        ret = pathFile.toString().substringAfter(path.toString()).substring(1)
                } else {
                    if (writePhotoAsBMP(imgPath, bmpFile.toPath())) {
                        Files.copy(bmpFile.toPath(), bmpFileMobile.toPath())
                        ret = bmpFileMobile.toString().substringAfter(pathMobile.toString()).substring(1)
                    } else
                        ret = pathFile.toString().substringAfter(path.toString()).substring(1)
                }
                if (!imgPath.equals(pathFile)) {
                    Files.delete(imgPath)
                }
                Files.delete(pathFile)
                Files.delete(bmpFile.toPath())
                return ret
            } catch (e: Exception) {
                log.error("Write File " + e.toString())
                return ""
            }
        } else
            return ""
    }

    fun writeAsBMP(pathFile: java.nio.file.Path, pathBmpFile: java.nio.file.Path): Boolean {
        try {
            val bufferedImageLoad = ImageIO.read(File(pathFile.toUri())) //ImageIO.read(ByteArrayInputStream(img))
            val fileObj = File(pathBmpFile.toUri())

            val bufferedImage = BufferedImage(bufferedImageLoad.width, bufferedImageLoad.height, BufferedImage.TYPE_BYTE_BINARY)

            for (y in 0..bufferedImageLoad.height - 1) {
                for (x in 0..bufferedImageLoad.width - 1) {
                    bufferedImage.setRGB(x, y, bufferedImageLoad.getRGB(x, y))
                }
            }

            return ImageIO.write(bufferedImage, "bmp", fileObj)


        } catch (e: Exception) {
            log.error("convert to bmp :" + e.toString())
            return false
        }


    }

    fun writePhotoAsBMP(pathFile: java.nio.file.Path, pathBmpFile: java.nio.file.Path): Boolean {
        try {
            val bufferedImage = ImageIO.read(File(pathFile.toUri()))
            val fileObj = File(pathBmpFile.toUri())

            return ImageIO.write(bufferedImage, "bmp", fileObj)


        } catch (e: Exception) {
            log.error("convert to bmp :" + e.toString())
            return false
        }


    }

    fun transSvg2Jpg(pathFile: java.nio.file.Path): java.nio.file.Path {

        val inputTranscoder = TranscoderInput(File(pathFile.toString()).toURI().toURL().toString())
        val imgFile = File(pathFile.toString())

        val jpgFile = imgFile.parentFile.toPath().resolve(imgFile.nameWithoutExtension + ".jpg").toFile()


        FileOutputStream(jpgFile).use {
            val outputTranscoder = TranscoderOutput(it)
            val converter = JPEGTranscoder()
            converter.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, 0.9.toFloat())
            converter.addTranscodingHint(JPEGTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE)
            converter.transcode(inputTranscoder, outputTranscoder)

        }
        return jpgFile.toPath()

    }


}

