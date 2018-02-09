package org.deku.leoz.central.service.internal

import com.fasterxml.jackson.databind.ObjectMapper
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.config.ParcelServiceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.model.*
import org.deku.leoz.node.Storage
import org.deku.leoz.time.toTimeWithoutDate
import org.deku.leoz.time.toDateWithoutTime
import org.deku.leoz.time.toShortTime
import org.deku.leoz.time.toGregorianLongDateString
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption


@Named
open class ParcelProcessingService {

    class ParcelProcessingException(message: String, inner: Throwable? = null) : Exception(message, inner)

    @Inject
    private lateinit var storage: Storage

    @Inject
    private lateinit var messagesRepository: JooqMessagesRepository

    @Inject
    private lateinit var userRepository: JooqUserRepository

    @Inject
    private lateinit var parcelRepository: JooqParcelRepository

    @Inject
    private lateinit var statusRepository: JooqStatusRepository

    @Inject
    private lateinit var fieldHistoryRepository: JooqFieldHistoryRepository

    @Inject
    private lateinit var orderRepository: JooqOrderRepository

    @Inject
    private lateinit var parcelServiceSettings: ParcelServiceConfiguration.Settings

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Transactional(PersistenceConfiguration.QUALIFIER)
    open fun processMessages() {
        if (parcelServiceSettings.skipParcelProcessing) {
            log.trace("Parcel processing is disabled")
            return
        }

        val events = messagesRepository.findUnprocessedMsg()

        //events.forEach {
        loop@ for (it in events) {
            try {
                //region Processing
                var pathToDelete: Path? = null

                val addInfo = it.additionalInfo?.toString() ?: "{}"

                val mapper = ObjectMapper()
                val parcelAddInfo: ParcelDeliveryAdditionalinfo = mapper.readValue(addInfo, ParcelDeliveryAdditionalinfo::class.java)
                val checkPictureFile = parcelAddInfo.pictureFileUID != null

                val parcel = it.parcelId ?: 0.toLong()
                val parcelRecord = parcelRepository.getParcelByParcelId(parcel)

                when (parcel) {
                    0.toLong() -> {
                        log.info("parcelId=0,messageID=${it.id.toString()}")
                        it.isProccessed = 1
                        it.store()
                        //return@forEach
                        continue@loop
                    }
                    else -> {
                    }
                }

                if (parcelRecord == null) {
                    log.info("parcelId=$parcel,messageID=${it.id.toString()} no parcelRecord")
                    it.isProccessed = 1
                    it.store()
                    //return@forEach
                    continue@loop
                }
                val orderRecord = orderRepository.findOrderByOrderNumber(parcelRecord.orderid.toLong())
                if (orderRecord == null) {
                    log.info("parcelId=$parcel,messageID=${it.id.toString()} no orderRecord")
                    it.isProccessed = 1
                    it.store()
                    //return@forEach
                    continue@loop
                }

                var insertStatus = true
                val r = dsl.newRecord(Tables.TBLSTATUS)

                val parcelNo = parcelRecord.colliebelegnr.toLong()

                val parcelScan = parcelNo.toString()

                val user = userRepository.findById(it.userId)
                val userEmail = user?.email ?: ""
                var infotext = "MOB $userEmail"
                if (infotext.length > 60)
                    infotext = infotext.substring(0, 60)

                r.packstuecknummer = parcelNo.toDouble()
                r.setDate(it.scanned)
                r.setTime(it.scanned)
                r.poslat = it.latitude
                r.poslong = it.longitude
                r.infotext = infotext

                val eventId = it.eventValue
                val event = Event.values().find { it.value == eventId }!!
                r.kzStatuserzeuger = event.creator.toString()
                r.kzStatus = event.concatId.toUInteger()
                r.timestamp2 = Date().toTimestamp()
                val reasonId = it.reasonId
                val reason = Reason.values().find { it.id == reasonId }!!
                r.fehlercode = reason.oldValue.toUInteger()

                r.erzeugerstation = "002"

                val userId = it.userId
                //if (userId != null) {
                //val station = userRepository.findStationNrByUserId(userId)
                val station = orderRecord.depotnrld
                if (station != null) {
                    r.erzeugerstation = station.toString().padStart(3, '0')
                }
                //}
                val from = r.erzeugerstation

                val pasClearingartmaster = orderRecord.clearingartmaster
                val pasCleared: Boolean
                if (pasClearingartmaster != null) {
                    pasCleared = (4096.and(pasClearingartmaster.toInt())) == 4096
                } else
                    pasCleared = false
                //TODO
                // var pasReset = false
                val checkDamaged = parcelAddInfo.damagedFileUIDs != null

                if (checkDamaged) {
                    val uids = parcelAddInfo.damagedFileUIDs
                    if (uids != null) {
                        //TODO
//                        for (i in 0..uids.size - 1) {
//                            val damagedFileUID = uids[i]
//                        }
                        val rDamaged = dsl.newRecord(Tables.TBLSTATUS)

                        rDamaged.packstuecknummer = r.packstuecknummer
                        rDamaged.datum = r.datum
                        rDamaged.zeit = r.zeit
                        rDamaged.poslat = r.poslat
                        rDamaged.poslong = r.poslong
                        rDamaged.infotext = r.infotext

                        val damaged_event = Event.DELIVERY_FAIL
                        rDamaged.kzStatuserzeuger = damaged_event.creator.toString()
                        rDamaged.kzStatus = damaged_event.concatId.toUInteger()
                        rDamaged.timestamp2 = r.timestamp2
                        val damaged_reason = Reason.PARCEL_DAMAGED
                        rDamaged.fehlercode = damaged_reason.oldValue.toUInteger()

                        rDamaged.erzeugerstation = r.erzeugerstation

                        statusRepository.statusExist(parcelRecord.colliebelegnr.toLong(), Event.DELIVERY_FAIL.creator.toString(), Event.DELIVERY_FAIL.concatId, Reason.PARCEL_DAMAGED.oldValue).also {
                            if (it == false)
                                rDamaged.store()
                        }
                    }
                }


                if (checkPictureFile) {
                    val pictureUID = parcelAddInfo.pictureFileUID
                    if (pictureUID == null) {
                        log.info("parcelId=$parcel,messageID=${it.id.toString()} pictureID null")
                        //return@forEach
                        continue@loop
                    }
                    val pathMobile = storage.mobileDataDirectory.toPath()
                    val fileNameInfo = userId.toString()
                    val loc = Location.SB
                    val mobileFilename = FileName(
                            value = pictureUID,
                            date = it.scanned.toTimestamp(),
                            type = loc,
                            basePath = pathMobile,
                            additionalInfo = fileNameInfo
                    )
                    val newFile = mobileFilename.getFilenameWithoutExtension() + ".jpg"
                    val pathFileMobile = mobileFilename.getPath().resolve(newFile).toFile().toPath()
                    val bmp = pathFileMobile.toString().substringAfter(pathMobile.toString()).substring(1)
                    val file = File(storage.workTmpDataDirectory, "$pictureUID.jpg")
                    if (!file.exists()) {
                        val countBmp = parcelRepository.getCountParcelsByBmp(bmp)
                        //if (mobileFilename.getPath().resolve(newFile).toFile().exists()) { //LeoRobo war evtl. schneller
                        if (countBmp > 0) {
//wenn LEO soweit ist .jpg anzuzeigen...
                            parcelRecord.bmpfilename = bmp
                            parcelRecord.store()

                        } else
                        //return@forEach
                            continue@loop
                    } else {


                        val copyOption = StandardCopyOption.REPLACE_EXISTING
                        Files.copy(file.toPath(), pathFileMobile, copyOption)
                        if (mobileFilename.getPath().resolve(newFile).toFile().exists()) {//LeoRobo war evtl. schneller??
                            pathToDelete = file.toPath()

//wenn LEO soweit ist .jpg anzuzeigen...
                            parcelRecord.bmpfilename = bmp
                            parcelRecord.store()
                        } else
                        //return@forEach
                            continue@loop
                    }
                }

                val checkPicturePath = parcelAddInfo.pictureLocation != null
                if (checkPicturePath) {

                    val pathMobile = storage.mobileDataDirectory.toPath()

                    val fileNameInfo = userId.toString()//.substringBefore("-")
                    val loc = Location.valueOf(parcelAddInfo.pictureLocation!!)

                    val mobileFilename = FileName(
                            value = parcelScan,
                            date = it.scanned.toTimestamp(),
                            type = loc,
                            basePath = pathMobile,
                            additionalInfo = fileNameInfo
                    )
                    val relPathMobile = mobileFilename.getPath()
                    //val file = mobileFilename.getFilenameWithoutExtension() + ".bmp"
                    val file = parcelAddInfo.pictureFileName!!
                    val pathFileMobile = relPathMobile.resolve(file).toFile().toPath()
                    val bmp = pathFileMobile.toString().substringAfter(pathMobile.toString()).substring(1)

                    parcelRecord.bmpfilename = bmp
                    parcelRecord.store()
                }

                when (event) {
                    Event.DELIVERED -> {
                        //TODO
                        //pasReset=true
                        val recipientInfo = StringBuilder()
                        val check = parcelAddInfo.recipient != null
                        if (check) {
                            recipientInfo.append(parcelAddInfo.recipient)
                        } else {
                            recipientInfo.append("")
                        }
                        when (reason) {
                            Reason.POSTBOX -> {
                                recipientInfo.append("BK")
                            }
                            Reason.NORMAL -> {
                            }
                            Reason.NEIGHBOUR -> {
                                //val addInfo = it.additionalInfo
                            }
                            else -> {
                            }
                        }
                        val textForStatus = if (recipientInfo.toString().length > 200) recipientInfo.toString().substring(0, 200) else recipientInfo.toString()
                        //r.text = recipientInfo.toString()
                        r.text = textForStatus
                        val textForOrder = if (recipientInfo.toString().length > 35) recipientInfo.toString().substring(0, 35) else recipientInfo.toString()

                        val oldValue = parcelRecord.lieferstatus
                        parcelRecord.lieferstatus = r.kzStatus.toShort() //4
                        parcelRecord.erstlieferstatus = r.kzStatus.toShort()
                        if (parcelRecord.store() > 0) {

                            fieldHistoryRepository.addEntry(
                                    orderId = parcelRecord.orderid.toLong(),
                                    unitNo = parcelRecord.colliebelegnr.toLong(),
                                    fieldName = "lieferstatus",
                                    oldValue = oldValue?.toString() ?: "",
                                    newValue = r.kzStatus.toString(),
                                    changer = "Z",
                                    point = "PP"
                            )
                        }

                        val oldRecipient = orderRecord.empfaenger ?: ""
                        //orderRecord.empfaenger = r.text
                        orderRecord.empfaenger = textForOrder
                        //if (orderRecord.store() > 0 && !oldRecipient.equals(r.text)) {
                        if (orderRecord.store() > 0 && !oldRecipient.equals(textForOrder)) {

                            fieldHistoryRepository.addEntry(
                                    orderId = parcelRecord.orderid.toLong(),
                                    unitNo = parcelRecord.colliebelegnr.toLong(),
                                    fieldName = "empfaenger",
                                    oldValue = oldRecipient,
                                    //newValue = r.text,
                                    newValue = textForOrder,
                                    changer = "Z",
                                    point = "PP"
                            )
                        }
                        //if (!orderRecord.empfaenger.equals(r.text)) {
                        if (!orderRecord.empfaenger.equals(textForOrder)) {
                            //TODO WLtransfer ASD D in Auftrag gescheitert
                        }

                        val oldDeliveryDate: String
                        if (orderRecord.dtauslieferdatum == null) {
                            oldDeliveryDate = ""
                        } else {
                            oldDeliveryDate = orderRecord.dtauslieferdatum.toGregorianLongDateString()
                        }
                        val oldDeliveryTime = orderRecord.dtauslieferzeit?.toShortTime()?.toString() ?: ""
                        val deliveryTime = it.scanned.toTimeWithoutDate()
                        val deliveryDate = it.scanned.toDateWithoutTime()

                        orderRecord.dtauslieferdatum = deliveryDate.toTimestamp()
                        orderRecord.dtauslieferzeit = deliveryTime.toTimestamp()
                        if (orderRecord.store() > 0) {
                            if (oldDeliveryTime != it.scanned.toTimestamp().toShortTime().toString()) {

                                fieldHistoryRepository.addEntry(
                                        orderId = parcelRecord.orderid.toLong(),
                                        unitNo = parcelRecord.colliebelegnr.toLong(),
                                        fieldName = "dtauslieferzeit",
                                        oldValue = oldDeliveryTime,
                                        newValue = it.scanned.toTimestamp().toShortTime().toString(),
                                        changer = "Z",
                                        point = "PP"
                                )

                            }
                            if (oldDeliveryDate != it.scanned.toGregorianLongDateString()) {


                                fieldHistoryRepository.addEntry(
                                        orderId = parcelRecord.orderid.toLong(),
                                        unitNo = parcelRecord.colliebelegnr.toLong(),
                                        fieldName = "dtauslieferdatum",
                                        oldValue = oldDeliveryDate,
                                        newValue = it.scanned.toGregorianLongDateString(),
                                        changer = "Z",
                                        point = "PP"
                                )
                            }

                        }
                        //

                        if (from != null) {
                            if (from.equals("956") || from.equals("909"))
                            //todo take list of alowed deliveryStations from sys_prperties
                                if (DekuUnitNumber.parse(parcelScan).value.type == UnitNumber.Type.Bag) {
                                    //if (parcelScan.startsWith("10071")) {
                                    val unitInBagUnitRecords = parcelRepository.findUnitsInBagByBagUnitNumber(parcelNo)
                                    unitInBagUnitRecords.forEach {
                                        val unitInBagStatusRecord = dsl.newRecord(Tables.TBLSTATUS)
                                        unitInBagStatusRecord.packstuecknummer = it.colliebelegnr
                                        unitInBagStatusRecord.datum = r.datum
                                        unitInBagStatusRecord.zeit = r.zeit
                                        unitInBagStatusRecord.poslat = r.poslat
                                        unitInBagStatusRecord.poslong = r.poslong
                                        unitInBagStatusRecord.kzStatuserzeuger = r.kzStatuserzeuger
                                        unitInBagStatusRecord.kzStatus = r.kzStatus
                                        unitInBagStatusRecord.erzeugerstation = r.erzeugerstation
                                        unitInBagStatusRecord.fehlercode = r.fehlercode
                                        //unitInBagStatusRecord.text = r.text
                                        unitInBagStatusRecord.text = textForStatus
                                        unitInBagStatusRecord.infotext = r.infotext
                                        unitInBagStatusRecord.store()

                                        val unitInBagOrderRecord = orderRepository.findOrderByOrderNumber(it.orderid.toLong())
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
                                                        orderId = it.orderid.toLong(),
                                                        unitNo = it.colliebelegnr.toLong(),
                                                        fieldName = "lieferstatus",
                                                        oldValue = unitInBagOldValue?.toString() ?: "",
                                                        newValue = r.kzStatus.toString(),
                                                        changer = "Z",
                                                        point = "PP"
                                                )
                                            }

                                            val unitInBagOldRecipient = unitInBagOrderRecord.empfaenger ?: ""
                                            //unitInBagOrderRecord.empfaenger = r.text
                                            unitInBagOrderRecord.empfaenger = textForOrder
                                            //if (unitInBagOrderRecord.store() > 0 && !unitInBagOldRecipient.equals(r.text)) {
                                            if (unitInBagOrderRecord.store() > 0 && !unitInBagOldRecipient.equals(textForOrder)) {

                                                fieldHistoryRepository.addEntry(
                                                        orderId = it.orderid.toLong(),
                                                        unitNo = it.colliebelegnr.toLong(),
                                                        fieldName = "empfaenger",
                                                        oldValue = unitInBagOldRecipient,
                                                        //newValue = r.text,
                                                        newValue = textForOrder,
                                                        changer = "Z",
                                                        point = "PP"
                                                )
                                            }
                                            //if (!unitInBagOrderRecord.empfaenger.equals(r.text)) {
                                            if (!unitInBagOrderRecord.empfaenger.equals(textForOrder)) {
                                                //TODO WLtransfer ASD D in Auftrag gescheitert
                                            }

                                            val unitInBagOldDeliveryDate: String
                                            if (unitInBagOrderRecord.dtauslieferdatum == null) {
                                                unitInBagOldDeliveryDate = ""
                                            } else {
                                                unitInBagOldDeliveryDate = unitInBagOrderRecord.dtauslieferdatum.toGregorianLongDateString()
                                            }
                                            val unitInBagOldDeliveryTime = unitInBagOrderRecord.dtauslieferzeit?.toShortTime()?.toString() ?: ""


                                            unitInBagOrderRecord.dtauslieferdatum = deliveryDate.toTimestamp()
                                            unitInBagOrderRecord.dtauslieferzeit = deliveryTime.toTimestamp()
                                            if (unitInBagOrderRecord.store() > 0) {
                                                if (unitInBagOldDeliveryTime != deliveryTime.toTimestamp().toShortTime().toString()) {


                                                    fieldHistoryRepository.addEntry(
                                                            orderId = it.orderid.toLong(),
                                                            unitNo = it.colliebelegnr.toLong(),
                                                            fieldName = "dtauslieferzeit",
                                                            oldValue = unitInBagOldDeliveryTime,
                                                            newValue = deliveryTime.toTimestamp().toShortTime().toString(),
                                                            changer = "Z",
                                                            point = "PP"
                                                    )
                                                }
                                                if (unitInBagOldDeliveryDate != deliveryDate.toGregorianLongDateString()) {


                                                    fieldHistoryRepository.addEntry(
                                                            orderId = it.orderid.toLong(),
                                                            unitNo = it.colliebelegnr.toLong(),
                                                            fieldName = "dtauslieferdatum",
                                                            oldValue = unitInBagOldDeliveryDate,
                                                            newValue = deliveryDate.toGregorianLongDateString(),
                                                            changer = "Z",
                                                            point = "PP"
                                                    )
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
                            //TODO
                            //pasReset = true
                            if (pasCleared) {
                                //TODO WLtransfer Auslieferung nach Abrechnung
                            }
                            if (firstDeliveryStatus.toInt() == 0) {
                                parcelRecord.lieferstatus = r.kzStatus.toShort()
                                parcelRecord.erstlieferstatus = r.kzStatus.toShort()
                                parcelRecord.lieferfehler = r.fehlercode.toShort()
                                parcelRecord.erstlieferfehler = r.fehlercode.toShort()
                                parcelRecord.storeWithHistoryParcelprocessing(parcelRecord.colliebelegnr.toLong())
                            }
                        }
                        /*val addInfo = it.additionalInfo
                    if (addInfo != null) {
                        when (addInfo) {
                            is AdditionalInfo.NotDeliveredInfo -> {
                                r.text = addInfo.text ?: ""
                            }
                        }
                    }*/
                        when (reason) {
                            Reason.CUSTOMER_REFUSED -> {

                                /*when (addInfo) {
                                is AdditionalInfo.EmptyInfo -> throw DefaultProblem(
                                        title = "Missing structure [DeliveredInfo] for event [$event].[$reason]"
                                )
                                is AdditionalInfo.NotDeliveredRefusedInfo -> {
                                    r.text = addInfo.cause ?: ""
                                }

                            }*/
                            }
                            Reason.PARCEL_DAMAGED -> {
                                /*when (addInfo) {
                                is AdditionalInfo.DamagedInfo -> {
                                    r.text = addInfo.description ?: ""
                                    if (addInfo.photo != null) {

                                    }
                                }
                            }*/
                            }
                            else -> {
                            }

                        }

                    }
                    Event.IMPORT_RECEIVE -> {


                        val oldValue: String//= parcelRecord.dteingangdepot2
                        if (parcelRecord.dteingangdepot2 == null) {
                            oldValue = ""
                        } else {
                            oldValue = parcelRecord.dteingangdepot2.toGregorianLongDateString()
                        }
                        val importDate = it.scanned.toDateWithoutTime()
                        parcelRecord.dteingangdepot2 = importDate.toTimestamp()// it.time.toTimestamp()
                        if (parcelRecord.store() > 0) {
                            if (oldValue != it.scanned.toGregorianLongDateString()) {

                                fieldHistoryRepository.addEntry(
                                        orderId = parcelRecord.orderid.toLong(),
                                        unitNo = parcelRecord.colliebelegnr.toLong(),
                                        fieldName = "dteingangdepot2",
                                        oldValue = oldValue,
                                        newValue = it.scanned.toGregorianLongDateString(),
                                        changer = "Z",
                                        point = "PP"
                                )
                            }
                        }
                    }
                    Event.IN_DELIVERY -> {
                        //ticket #260

                        var existStatus = statusRepository.statusExist(parcelRecord.colliebelegnr.toLong(), Event.HUB_OFFLOADED.creator.toString(), Event.HUB_OFFLOADED.concatId, Reason.NORMAL.id)
                        if (!existStatus)
                            existStatus = statusRepository.statusExist(parcelRecord.colliebelegnr.toLong(), Event.HUB_LOADED.creator.toString(), Event.HUB_LOADED.concatId, Reason.NORMAL.id)
                        if (!existStatus)
                            existStatus = statusRepository.statusExist(parcelRecord.colliebelegnr.toLong(), Event.IMPORT_RECEIVE.creator.toString(), Event.IMPORT_RECEIVE.concatId, Reason.NORMAL.id)
                        if (!existStatus)
                            insertStatus = false
                        existStatus = statusRepository.statusExist(parcelRecord.colliebelegnr.toLong(), Event.IN_DELIVERY.creator.toString(), Event.IN_DELIVERY.concatId, Reason.NORMAL.id)



                        if (existStatus)
                            insertStatus = false
                    }
                    Event.NOT_IN_DELIVERY -> {

                        var existStatus = statusRepository.statusExist(parcelRecord.colliebelegnr.toLong(), Event.NOT_IN_DELIVERY.creator.toString(), Event.NOT_IN_DELIVERY.concatId, Reason.NORMAL.id)


                        if (existStatus)
                            insertStatus = false
                    }
                    Event.EXPORT_LOADED -> {/*
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
                                orderRecord.dtsendad2z = it.scanned.toTimestamp()
                                if (orderRecord.store() > 0) {
                                    if (!oldSend.equals(orderRecord.dtsendad2z.toString())) {
                                        fieldHistoryRepository.addEntry(
                                                orderId = parcelRecord.orderid.toLong(),
                                                unitNo = parcelRecord.colliebelegnr.toLong(),
                                                fieldName = "dtsendad2z",
                                                oldValue = oldSend,
                                                newValue = orderRecord.dtsendad2z.toString(),
                                                changer = "I",
                                                point = ""
                                        )
                                    }

                                }
                                val depotOutDate = it.scanned.toDateWithoutTime()
                                val oldDepotOut: String
                                if (parcelRecord.dtausgangdepot2 == null) {
                                    oldDepotOut = ""
                                } else {
                                    oldDepotOut = parcelRecord.dtausgangdepot2.toString_ddMMyyyy_PointSeparated()
                                }
                                parcelRecord.dtausgangdepot2 = depotOutDate.toTimestamp()
                                if (parcelRecord.store() > 0) {
                                    if (oldDepotOut != it.scanned.toString_ddMMyyyy_PointSeparated()) {

                                        fieldHistoryRepository.addEntry(
                                                orderId = parcelRecord.orderid.toLong(),
                                                unitNo = parcelRecord.colliebelegnr.toLong(),
                                                fieldName = "dtausgangdepot2",
                                                oldValue = oldDepotOut,
                                                newValue = it.scanned.toString_ddMMyyyy_PointSeparated(),
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
                                                orderId = parcelRecord.orderid.toLong(),
                                                unitNo = parcelRecord.colliebelegnr.toLong(),
                                                fieldName = "ladelistennummerd",
                                                oldValue = oldLoadingNo,
                                                newValue = addInfo.loadingListNo.toString(),
                                                changer = "SP",
                                                point = "IM"
                                        )
                                    }
                                }
                            }
                        }*/
                    }
                    else -> {
                    }
                }

                if (insertStatus) {
                    try {
                        r.store()

                    } catch (e: Throwable) {

                        throw e
                    }
                    pathToDelete?.also { Files.delete(it) }

                }
                //endregion

                it.isProccessed = 1
                it.store()

            } catch (e: Throwable) {
                log.error("Parcel processing failed for [${it.id}]: ${e.message}", e)
            }
        }
    }
}