package org.deku.leoz.central.service.internal

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.JPEGTranscoder
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.ParcelProcessing
import org.deku.leoz.central.data.jooq.Routines
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.node.Storage
import sx.rs.DefaultProblem
import org.deku.leoz.service.internal.ParcelServiceV1
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.time.toTimestamp
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.*
import javax.imageio.ImageIO
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import org.deku.leoz.central.data.repository.*
import org.deku.leoz.central.data.toUInteger
import org.deku.leoz.model.*
import org.deku.leoz.service.internal.StationService
import org.deku.leoz.service.internal.UserService
import org.deku.leoz.time.toString_ddMMyyyy_PointSeparated
import sx.io.serialization.Serializable

/**
 * Parcel service v1 implementation
 * Created by JT on 17.07.17.
 */
@Named
@Path("internal/v1/parcel")
open class ParcelServiceV1 :
        org.deku.leoz.service.internal.ParcelServiceV1,
        MqHandler<ParcelServiceV1.ParcelMessage> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Serializable(0x5880838e3ce330)
    private data class MessageInfo(
            var total: Int = 0,
            var parts: Array<Int> = arrayOf()
    )


    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext
    @Inject
    private lateinit var orderRepository: OrderJooqRepository


    @Inject
    private lateinit var storage: Storage


    @Inject
    private lateinit var messagesRepository: MessagesJooqRepository

    @Inject
    private lateinit var parcelProcessing: ParcelProcessing

    @Inject
    private lateinit var userService: UserService

    @Inject
    private lateinit var parcelRepository: ParcelJooqRepository

    @Inject
    private lateinit var bagService: BagService

    @Inject
    private lateinit var stationService: StationService

    @Inject
    private lateinit var fieldHistoryRepository: FieldHistoryJooqRepository

    @Inject
    private lateinit var statusRepository: StatusJooqRepository

    /**
     * Parcel service message handler
     */
    @Transactional(PersistenceConfiguration.QUALIFIER)
    override fun onMessage(message: ParcelServiceV1.ParcelMessage, replyChannel: MqChannel?) {
        log.debug(message.toString())

        val events = message.events?.toList()
                ?: throw DefaultProblem(
                detail = "Missing data",
                status = Response.Status.BAD_REQUEST)

        log.trace("Received ${events.count()} from [${message.nodeId}] user [${message.userId}]")

        val parcelIds = events.map { it.parcelId }.toList()
        val mapParcels = orderRepository.getUnitNumbers(parcelIds)

        events.forEach {

            val scannedDate = it.time.toTimestamp()

            //val parcelNo = parcelRepository.getUnitNo(it.parcelId)
            val parcelNo = mapParcels[it.parcelId.toDouble()]?.toLong()

            parcelNo ?:
                    throw DefaultProblem(
                            title = "Missing parcelNo"
                    )
            val parcelScan = parcelNo.toString()
            val recordMessages = dslContext.newRecord(Tables.TAD_PARCEL_MESSAGES)
            recordMessages.userId = message.userId
            recordMessages.nodeId = message.nodeId
            recordMessages.parcelId = it.parcelId
            recordMessages.parcelNo = parcelScan
            recordMessages.scanned = scannedDate
            recordMessages.eventValue = it.event
            recordMessages.reasonId = it.reason
            recordMessages.latitude = it.latitude
            recordMessages.longitude = it.longitude
            recordMessages.isProccessed = 0
            if (!messagesRepository.saveMsg(recordMessages)) {
                log.error("Problem saving parcel-messages")
            }
            var parcelAddInfo = ParcelDeliveryAdditionalinfo()

            var damagedInfo = it.damagedInfo
            if (damagedInfo != null) {
                //if (damagedInfo.pictureFileUids != null) {
                parcelAddInfo.damagedFileUIDs = damagedInfo.pictureFileUids.map { j -> j.toString() }.toList()
                //}
            }

            val eventId = it.event
            val event = Event.values().find { it.value == eventId }!!
            val reasonId = it.reason
            val reason = Reason.values().find { it.id == reasonId }!!


            when (event) {
                Event.DELIVERED -> {

                    var signature: String? = null
                    var mimetype = "svg"
                    when (reason) {
                        Reason.POSTBOX -> {
                            when (message.postboxDeliveryInfo) {
                                null -> {
                                }
                                else -> {
                                    val addInfo = message.postboxDeliveryInfo
                                    if (addInfo != null) {
                                        if (addInfo.pictureFileUid != null) {
                                            parcelAddInfo.pictureFileUID = addInfo.pictureFileUid.toString()
                                        }
                                    }
                                }
                            }
                        }
                        Reason.NORMAL -> {
                            when (message.deliveredInfo) {
                                null -> {
                                    when (message.signatureOnPaperInfo) {
                                        null -> {
                                            // throw DefaultProblem(title = "Missing structure [signatureOnPaperInfo] for event [$event].[$reason]")
                                        }
                                        else -> {
                                            val addInfo = message.signatureOnPaperInfo
                                            if (addInfo != null) {
                                                if (addInfo.recipient != null) {
                                                    parcelAddInfo.recipient = addInfo.recipient
                                                }
                                                if (addInfo.pictureFileUid != null) {
                                                    parcelAddInfo.pictureFileUID = addInfo.pictureFileUid.toString()
                                                }
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    val addInfo = message.deliveredInfo
                                    if (addInfo != null) {
                                        if (addInfo.recipient != null) {
                                            parcelAddInfo.recipient = addInfo.recipient
                                        }
                                        if (addInfo.signature != null) {
                                            signature = addInfo.signature
                                        }
                                        //if (addInfo.mimetype != null) {
                                        mimetype = addInfo.mimetype
                                        //}
                                    }
                                }
                            }
                        }
                        Reason.NEIGHBOUR -> {
                            when (message.deliveredInfo) {
                                null -> {
                                    when (message.signatureOnPaperInfo) {
                                        null -> {
                                            // throw DefaultProblem(title = "Missing structure [signatureOnPaperInfo] for event [$event].[$reason]")
                                        }
                                        else -> {
                                            val addInfo = message.signatureOnPaperInfo

                                            if (addInfo != null) {
                                                if (addInfo.recipient != null) {
                                                    parcelAddInfo.recipient = addInfo.recipient
                                                }
                                                if (addInfo.pictureFileUid != null) {
                                                    parcelAddInfo.pictureFileUID = addInfo.pictureFileUid.toString()
                                                }
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    val addInfo = message.deliveredInfo

                                    if (addInfo != null) {
                                        if (addInfo.recipient != null) {
                                            parcelAddInfo.recipient = addInfo.recipient
                                        }
                                        if (addInfo.signature != null) {
                                            signature = addInfo.signature
                                        }
                                        //if (addInfo.mimetype != null) {
                                        mimetype = addInfo.mimetype
                                        //}
                                    }
                                }
                            }
                        }
                        else -> {
                        }
                    }

                    if (signature != null) {
                        val sigFilename = saveImage(scannedDate, Location.SB, signature, parcelScan, message.userId, mimetype, Location.SB_Original)
                        if (sigFilename != "") {
                            //parcelRepository.setSignaturePath(parcelScan, sigPath)
                            parcelAddInfo.pictureLocation = Location.SB.toString()
                            parcelAddInfo.pictureFileName = sigFilename
                        }
                    }
                }

                Event.DELIVERY_FAIL -> {

                }
                Event.IMPORT_RECEIVE -> {

                }
                Event.IN_DELIVERY -> {


                }
                Event.NOT_IN_DELIVERY -> {

                }
                Event.EXPORT_LOADED -> {
                    val addInfo = it.additionalInfo
                    when (addInfo) {
                        is AdditionalInfo.LoadingListInfo -> {
                            //r.text = addInfo.loadingListNo.toString()
                            //recordMessages.additionalInfo = "{\"text\":\"" + addInfo.loadingListNo.toString() + "\"}"
                            //messagesRepository.saveMsg(recordMessages)

                        }
                    }
                }
                else -> {
                }
            }
            val mapper = ObjectMapper()
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            recordMessages.additionalInfo = mapper.writeValueAsString(parcelAddInfo)
            messagesRepository.saveMsg(recordMessages)

        }
        if (!parcelProcessing.processMessages()) {
            log.error("Problem processing parcel-messages")
        }
    }

    fun saveImage(date: Date, location: Location, image: String?, number: String, userId: Int?, mimetype: String, locationOriginal: Location?): String {
        if (image != null) {
            val keepOriginal = (locationOriginal != null) //true else false
            val pathMobile = storage.mobileDataDirectory.toPath()

            val addInfo = userId.toString()//.substringBefore("-")
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
                    Files.write(pathFile, image.toByteArray(), java.nio.file.StandardOpenOption.CREATE_NEW).toString()
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
                        //ret = bmpFileMobile.toString().substringAfter(pathMobile.toString()).substring(1)
                        ret = bmpFileMobile.absoluteFile.name
                    } else
                    //ret = pathFile.toString().substringAfter(path.toString()).substring(1)
                        ret = pathFile.toFile().absoluteFile.name
                } else {
                    if (writePhotoAsBMP(imgPath, bmpFile.toPath())) {
                        Files.copy(bmpFile.toPath(), bmpFileMobile.toPath())
                        //ret = bmpFileMobile.toString().substringAfter(pathMobile.toString()).substring(1)
                        ret = bmpFileMobile.absoluteFile.name
                    } else
                    //ret = pathFile.toString().substringAfter(path.toString()).substring(1)
                        ret = pathFile.toFile().absoluteFile.name
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

    override fun getParcels2ExportByStationNo(stationNo: Int): List<ParcelServiceV1.Order2Export> {
        val orders = parcelRepository.getOrders2ExportByStation(stationNo)
        orders ?: throw DefaultProblem(
                status = Response.Status.NOT_FOUND,
                title = "no orders found"
        )
        val orders2export = orders.map { it.toOrder2Export() }
        orders2export.forEach {
            val parcels = parcelRepository.getParcels2ExportByOrderid(it.orderId)
            parcels ?: return@forEach
            if (parcels.count() == 0)
                return@forEach
            it.parcels = parcels.map { f -> f.toParcel2Export() }
        }
        val ordersFiltered = orders2export.filter { it.parcels != null }
        if (ordersFiltered.count() == 0)
            throw DefaultProblem(
                    status = Response.Status.NOT_FOUND,
                    title = "no parcels found"
            )
        return ordersFiltered

    }

    override fun getNewLoadinglistNo(): Long {
        val user = userService.get()

        return Routines.fTan(dslContext.configuration(), counter.LOADING_LIST.value) + 300000

    }

    override fun getParcels2ExportByLoadingList(loadinglistNo: Long): List<ParcelServiceV1.Order2Export> {
        val parcels = parcelRepository.getParcels2ExportByLoadingList(loadinglistNo)
        parcels ?: throw DefaultProblem(
                status = Response.Status.NOT_FOUND,
                title = "no parcels found for this list"
        )
        if (parcels.count() == 0)
            throw DefaultProblem(
                    status = Response.Status.NOT_FOUND,
                    title = "no parcels found for this list"
            )
        val orderIdList = parcels.map { it.orderid }.distinct()
        val orderList: MutableList<ParcelServiceV1.Order2Export> = mutableListOf<ParcelServiceV1.Order2Export>()
        orderIdList.forEach {
            val orderRecord = parcelRepository.getOrder2ExportById(it.toLong())
            if (orderRecord != null) {
                val order = orderRecord.toOrder2Export()
                val pp = parcels.filter { f -> f.orderid == it }
                if (pp.count() > 0) {
                    order.parcels = pp.map { it.toParcel2Export() }
                    orderList.add(order)
                }
            }
        }
        return orderList
    }

    override fun getLoadedParcels2ExportByStationNo(stationNo: Int): List<ParcelServiceV1.Order2Export> {
        val orders = parcelRepository.getOrders2ExportByStation(stationNo)
        orders ?: throw DefaultProblem(
                status = Response.Status.NOT_FOUND,
                title = "no orders found"
        )
        val orders2export = orders.map { it.toOrder2Export() }
        orders2export.forEach {
            val parcels = parcelRepository.getLoadedParcels2ExportByOrderid(it.orderId)
            parcels ?: return@forEach
            if (parcels.count() == 0)
                return@forEach
            it.parcels = parcels.map { f -> f.toParcel2Export() }
        }
        val ordersFiltered = orders2export.filter { it.parcels != null }
        if (ordersFiltered.count() == 0)
            throw DefaultProblem(
                    status = Response.Status.NOT_FOUND,
                    title = "no parcels found"
            )
        return ordersFiltered
    }

    override fun export(scanCode: String, loadingListNo: Long, stationNo: Int): Boolean {
        val user = userService.get()

        val un = UnitNumber.parseLabel(scanCode)
        var dekuNo: Long? = null
        when {
            un.hasError -> {
                val gun = GlsUnitNumber.parseLabel(scanCode)
                when {
                    gun.hasError -> {
                        val unitRecords = parcelRepository.getParcels2ExportByCreferenceAndStation(stationNo, scanCode)
                        unitRecords ?: throw DefaultProblem(
                                status = Response.Status.NOT_FOUND,
                                title = "no parcels found"
                        )
                        if (unitRecords.count() > 1) {
                            throw DefaultProblem(
                                    status = Response.Status.BAD_REQUEST,
                                    title = "more parcels found to this cReference"
                            )
                        }
                        if (unitRecords.count() == 0) {
                            throw DefaultProblem(
                                    status = Response.Status.NOT_FOUND,
                                    title = "no parcels found"
                            )
                        }
                        dekuNo = unitRecords[0].colliebelegnr.toLong()

                    }
                    else -> {
                        dekuNo = gun.value.toUnitNumber().value.toLong()
                    }
                }
            }
            else -> {
                dekuNo = un.value.value.toLong()
            }
        }

        val unitRecord = parcelRepository.findParcelByUnitNumber(dekuNo)
        unitRecord ?: throw DefaultProblem(
                status = Response.Status.NOT_FOUND,
                title = "parcel not found"
        )
        if (unitRecord.erstlieferstatus.toInt() == 4) {
            throw DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "parcel delivered"
            )
        }

        val orderRecord = parcelRepository.getOrderById(unitRecord.orderid.toLong())
        orderRecord ?: throw DefaultProblem(
                status = Response.Status.NOT_FOUND,
                title = "order not found"
        )

        if (orderRecord.kzTransportart.toInt() != 1) {
            throw DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "no ONS"
            )
        }
        if (orderRecord.depotnrabd != stationNo) {
            //if (!(stationNo==800 && unitRecord.colliebelegnr.toLong().toString().startsWith("8"))){
            if (!(stationNo == 800 && orderRecord.depotnrabd in (800..900))) {
                throw DefaultProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "station dismatch"
                )
            }
        }
        var checkOk = true
        val allUnitsOfOrder = parcelRepository.getParcelsByOrderId(orderRecord.orderid.toLong())
        allUnitsOfOrder ?: throw DefaultProblem(
                status = Response.Status.NOT_FOUND,
                title = "parcels not found"
        )
        allUnitsOfOrder.forEach {
            if (it.erstlieferstatus.toInt() == 8 && it.erstlieferfehler.toInt() != 30)
                checkOk = false
            if (it.erstlieferstatus.toInt() == 4)
                checkOk = false
        }
        val workDate = bagService.getWorkingDate()
        if (checkOk) {

            when (orderRecord.kzTransportart.toInt()) {
                0, 1, 2, 4, 8 -> {
                    if (orderRecord.verladedatum != workDate.toTimestamp()) {
                        //Test Verladedatum
                        //setRoute
                        //Korrektur Datensatz abhängig von tbloptionen nr=1212 Wert=-1 ???
                        ////Verladedatum=workdate
                        ////Feiertag_1
                        ////FeiertagShlS
                        ////if sendStatus!=0 sendstatus=0

                        //collies korregieren falls erstlieferstatus=0 oder 8-30


                    }
                    if (orderRecord.dtauslieferung == null
                            || orderRecord.dtauslieferung < java.time.LocalDateTime.now().toLocalDate().toTimestamp()
                            || orderRecord.dtauslieferung > java.time.LocalDateTime.now().plusDays(90).toLocalDate().toTimestamp()) {
                        //Test Lieferdatum=null,<now or >+90 Tage (referenzScan Teileinlieferung)
                        //setRoute
                        //Korrektur Datensatz abhängig von tbloptionen nr=1212 Wert=-1 ???
                        ////dtAuslieferung
                        ////Feiertag_2
                        ////FeiertagShlD
                        ////if sendStatus!=0 sendstatus=0
                        //collies korregieren falls erstlieferstatus=0 oder 8-30
                    }
                    if (orderRecord.lockflag.toInt() == 3) {
                        //Lockflag=0 if =3
                        //sdgstatus="S" if !="S"
                        //sendstatus=0 if !=0
                        //collies korregieren falls erstlieferstatus=0 oder 8-30

                    }
//                    allUnitsOfOrder.forEach {
//                        if (it.erstlieferstatus.toInt() == 8 && it.erstlieferfehler.toInt() == 30){
//                            it.lieferstatus=0
//                            it.lieferfehler=0
//                            it.erstlieferstatus=0
//                            it.store()
//                        }
//                    }
                    if (unitRecord.erstlieferstatus.toInt() == 8 && unitRecord.erstlieferfehler.toInt() == 30) {
                        unitRecord.lieferstatus = 0
                        unitRecord.lieferfehler = 0
                        unitRecord.erstlieferstatus = 0
                        unitRecord.store()
                    }

                }
            }
        }

        if (orderRecord.lockflag.toInt() == 3) {
            throw DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "parcel deleted"
            )
        }
        if (unitRecord.erstlieferstatus.toInt() == 8 && unitRecord.erstlieferfehler.toInt() == 30) {
            throw DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "parcel marked as missing - parts of order are delivered"
            )
        }
        if (orderRecord.verladedatum != workDate.toTimestamp()) {
            throw DefaultProblem(
                    status = Response.Status.BAD_REQUEST,
                    title = "invalid senddate"
            )
        }

        if (unitRecord.verpackungsart == 91) {//verpackungsart=Valore
            val station = stationService.getByStationNo(stationNo)
            if (!station.exportValuablesAllowed) {
                throw DefaultProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "valuables not allowed"
                )
            }
            if (!station.exportValuablesWithoutBagAllowed) {
                throw DefaultProblem(
                        status = Response.Status.BAD_REQUEST,
                        title = "valuables not allowed without bag"
                )
            }
        }
        var title = "ok"
        if (unitRecord.ladelistennummerd == null) {
        } else if (unitRecord.ladelistennummerd.toLong() == loadingListNo) {
            //doppelt gescannt
            throw DefaultProblem(
                    status = Response.Status.OK,
                    title = "already scanned"
            )
        } else {
            //umbuchen auf andere ladeliste
            title = "loadinglist changed"

        }
        val oldLoadinglist = unitRecord.ladelistennummerd?.toString() ?: ""
        unitRecord.ladelistennummerd = loadingListNo.toDouble()
        if (unitRecord.store() > 0) {
            fieldHistoryRepository.addEntry(
                    orderId = unitRecord.orderid.toLong(),
                    unitNo = unitRecord.colliebelegnr.toLong(),
                    fieldName = "ladelistennummerd",
                    oldValue = oldLoadinglist,
                    newValue = loadingListNo.toString(),
                    changer = "WEB",
                    point = "EX"
            )
        }
        val oldDtAusgangDepot2 = unitRecord.dtausgangdepot2?.toString_ddMMyyyy_PointSeparated() ?: ""
        unitRecord.dtausgangdepot2 = workDate.toTimestamp()
        if (unitRecord.store() > 0) {
            fieldHistoryRepository.addEntry(
                    orderId = unitRecord.orderid.toLong(),
                    unitNo = unitRecord.colliebelegnr.toLong(),
                    fieldName = "dtausgangdepot2",
                    oldValue = oldDtAusgangDepot2,
                    newValue = workDate.toTimestamp().toString_ddMMyyyy_PointSeparated(),
                    changer = "WEB",
                    point = "EX"
            )
        }
        val oldIscan = unitRecord.iScan?.toString() ?: ""
        unitRecord.iScan = -1
        if (unitRecord.store() > 0) {
            fieldHistoryRepository.addEntry(
                    orderId = unitRecord.orderid.toLong(),
                    unitNo = unitRecord.colliebelegnr.toLong(),
                    fieldName = "iScan",
                    oldValue = oldIscan,
                    newValue = "-1",
                    changer = "WEB",
                    point = "EX"
            )
        }
        val oldNueD2H2 = unitRecord.nued2h2?.toString() ?: ""
        unitRecord.nued2h2 = -1
        if (unitRecord.store() > 0) {
            fieldHistoryRepository.addEntry(
                    orderId = unitRecord.orderid.toLong(),
                    unitNo = unitRecord.colliebelegnr.toLong(),
                    fieldName = "nued2h2",
                    oldValue = oldNueD2H2,
                    newValue = "-1",
                    changer = "WEB",
                    point = "EX"
            )
        }
        val oldNueH2D2 = unitRecord.nueh2d2?.toString() ?: ""
        unitRecord.nueh2d2 = -1
        if (unitRecord.store() > 0) {
            fieldHistoryRepository.addEntry(
                    orderId = unitRecord.orderid.toLong(),
                    unitNo = unitRecord.colliebelegnr.toLong(),
                    fieldName = "nueh2d2",
                    oldValue = oldNueH2D2,
                    newValue = "-1",
                    changer = "WEB",
                    point = "EX"
            )
        }
        val scanTs = Date()
        val infotext = "WebExport"
        var existStatus = statusRepository.statusExist(unitRecord.colliebelegnr.toLong(), "A", 2, 0)
        if (!existStatus) {
            val r = dslContext.newRecord(Tables.TBLSTATUS)
            r.packstuecknummer = unitRecord.colliebelegnr
            r.setDate(scanTs)
            r.setTime(scanTs)
            r.infotext = infotext

            r.kzStatuserzeuger = "A"
            r.kzStatus = 2.toUInteger()
            r.timestamp2 = Date().toTimestamp()
            r.fehlercode = 0.toUInteger()

            r.erzeugerstation = stationNo.toString()
        }
        existStatus = statusRepository.statusExist(unitRecord.colliebelegnr.toLong(), "A", 4, 0)
        if (!existStatus) {
            val r = dslContext.newRecord(Tables.TBLSTATUS)
            r.packstuecknummer = unitRecord.colliebelegnr
            r.setDate(scanTs)
            r.setTime(scanTs)
            r.infotext = infotext

            r.kzStatuserzeuger = "A"
            r.kzStatus = 4.toUInteger()
            r.timestamp2 = Date().toTimestamp()
            r.fehlercode = 0.toUInteger()

            r.erzeugerstation = stationNo.toString()
        }

        throw DefaultProblem(
                status = Response.Status.OK,
                title = title
        )
        return true
    }
}

