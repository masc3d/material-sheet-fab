package org.deku.leoz.central.service.internal

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.JPEGTranscoder
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.repository.JooqMessagesRepository
import org.deku.leoz.central.data.repository.JooqNodeRepository
import org.deku.leoz.model.*
import org.deku.leoz.node.Storage
import org.deku.leoz.node.service.pub.DocumentService
import org.deku.leoz.service.internal.OrderService
import org.deku.leoz.service.internal.ParcelServiceV1
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import sx.io.serialization.Serializable
import sx.log.slf4j.debug
import sx.log.slf4j.trace
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.rs.RestProblem
import sx.time.toTimestamp
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.*
import java.nio.file.Files
import java.util.*
import javax.imageio.ImageIO
import javax.inject.Inject
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


/**
 * Parcel service v1 implementation
 * Created by JT on 17.07.17.
 */
@Component
@Path("internal/v1/parcel")
class ParcelServiceV1 :
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
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var storage: Storage


    @Inject
    private lateinit var messagesRepository: JooqMessagesRepository

    @Inject
    private lateinit var parcelProcessingService: ParcelProcessingService

    @Inject
    private lateinit var nodeRepository: JooqNodeRepository

    @Inject
    private lateinit var documentService: DocumentService

    /** Dedicated object mapper */
    private val mapper by lazy {
        ObjectMapper()
    }

    /**
     * Parcel service message handler
     */
    @Transactional(PersistenceConfiguration.QUALIFIER)
    override fun onMessage(message: ParcelServiceV1.ParcelMessage, replyChannel: MqChannel?) {

        log.debug { message }

        val events = message.events?.toList()
                ?: throw IllegalArgumentException("Missing data")

        log.trace { "Received ${events.count()} from [${message.nodeId}] user [${message.userId}]" }
        val nodeKey = message.nodeId
        var nodeId: Int? = null
        if (nodeKey != null) {
            nodeId = nodeRepository.findByKey(nodeKey)?.nodeId
        }

        //val parcelIds = events.map { it.parcelId }.toList()
        //val mapParcels = orderRepository.getUnitNumbers(parcelIds)

        events.forEach {
            val scannedDate = it.time.toTimestamp()

            //var parcelNo = mapParcels[it.parcelId.toInt()]?.toLong()

//            if (parcelNo == null) {
//                parcelNo = 0
//                log.info("Deleted Parcel. Id= [${it.parcelId}]")
//            }
            //dodo  events[].parcelScancode shuld be filled to handle deleted or moved parcel in mysql between deliverylist and delivered event

            //val parcelScan = parcelNo.toString()
            val recordMessages = dsl.newRecord(Tables.TAD_PARCEL_MESSAGES)
            recordMessages.userId = message.userId
            //recordMessages.nodeId = message.nodeId
            recordMessages.nodeIdX = nodeId
            recordMessages.parcelId = it.parcelId
            //recordMessages.parcelNo = parcelScan
            recordMessages.scanned = scannedDate
            recordMessages.eventValue = if (it.event == 107 && it.reason == 0) 106 else 107
            recordMessages.reasonId = it.reason
            recordMessages.latitude = it.latitude
            recordMessages.longitude = it.longitude
            recordMessages.isProccessed = 0

            recordMessages.store()

            val parcelAddInfo = ParcelServiceV1.Additionalinfo()

            val damagedInfo = it.damagedInfo
            if (damagedInfo != null) {
                parcelAddInfo.damagedFileUIDs = damagedInfo.pictureFileUids.map { j -> j.toString() }.toList()
            }

            val eventId = if (it.event == 107 && it.reason == 0) 106 else 107
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
                        val sigFilename = saveImage(
                                date = scannedDate,
                                location = Location.SB,
                                image = signature,
                                number = UUID.randomUUID().toString(),
                                userId = message.userId,
                                mimetype = mimetype,
                                locationOriginal = Location.SB_Original)

                        if (sigFilename.isBlank())
                            throw IllegalStateException("saveImage returned empty filename")

                        parcelAddInfo.pictureLocation = Location.SB.toString()
                        parcelAddInfo.pictureFileName = sigFilename
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

            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            recordMessages.additionalInfo = mapper.writeValueAsString(parcelAddInfo)

            recordMessages.store()
        }

        try {
            parcelProcessingService.processMessages()
        } catch (e: Throwable) {
            log.error(e.message, e)
        }
    }

    /**
     * Save SVG primary image. Convert to BMP via JPG to be moved in Parcelprocessing
     * @return Destination path
     */
    open fun saveImage(date: Date, location: Location, image: String, number: String, userId: Int?, mimetype: String, locationOriginal: Location?): String {
        val keepOriginal = (locationOriginal != null) //true else false
        val pathMobile = storage.mobileDataDirectory.toPath()

        val addInfo = userId.toString()//.substringBefore("-")
        val mobileFilename = FileName(
                value = number,
                date = date,
                type = location,
                basePath = pathMobile,
                additionalInfo = addInfo)

        val relPathMobile = mobileFilename.getPath()

        val path = storage.workTmpDataDirectory.toPath()

        val mobileWorkFilename = FileName(
                value = number,
                date = date,
                type = location,
                basePath = path,
                additionalInfo = addInfo)
        val relPath = mobileWorkFilename.getPath()

        val fileExtension: String
        when (mimetype) {
            MediaType.APPLICATION_SVG_XML -> fileExtension = "svg"
            else -> throw UnsupportedOperationException("Mime type ${mimetype}] not supported")
        }

        val file = mobileFilename.getFilenameWithoutExtension() + "." + fileExtension
        val pathFile = relPath.resolve(file).toFile().toPath()
        val pathFileMobile = relPathMobile.resolve(file).toFile().toPath()

        var imgPath = pathFile

        Files.write(imgPath,
                image.toByteArray(),
                java.nio.file.StandardOpenOption.CREATE_NEW)

        val inFile = imgPath.toFile()
        val outFile = inFile.replaceExtension("jpg")

        FileInputStream(inFile).use { input ->
            FileOutputStream(outFile).use { output ->
                this.transcodeSvg2Jpg(input, output)
            }
        }

        imgPath = outFile.toPath()

        if (keepOriginal) {
            val mobileOriginalFilename = FileName(
                    value = number,
                    date = date,
                    type = locationOriginal!!,
                    basePath = pathMobile,
                    additionalInfo = addInfo
            )
            val relPathMobileOriginal = mobileOriginalFilename.getPath()
            val pathFileMobileOriginal = relPathMobileOriginal.resolve(file).toFile().toPath()
            Files.copy(pathFile, pathFileMobileOriginal)
        }

        val bmpFile = imgPath.toFile().parentFile.toPath()
                .resolve(imgPath.toFile().nameWithoutExtension + ".bmp").toFile()
        val bmpFileMobile = pathFileMobile.toFile().parentFile.toPath()
                .resolve(imgPath.toFile().nameWithoutExtension + ".bmp").toFile()


        writeAsBMP(imgPath, bmpFile.toPath())

        val ret: String = bmpFileMobile.absoluteFile.name

        Files.copy(bmpFile.toPath(), bmpFileMobile.toPath())

        if (!imgPath.equals(pathFile)) {
            Files.delete(imgPath)
        }
        Files.delete(pathFile)
        Files.delete(bmpFile.toPath())

        return ret
    }

    open fun writeAsBMP(pathFile: java.nio.file.Path, pathBmpFile: java.nio.file.Path) {
        val bufferedImageLoad = ImageIO.read(File(pathFile.toUri())) //ImageIO.read(ByteArrayInputStream(img))
        val fileObj = File(pathBmpFile.toUri())

        val bufferedImage = BufferedImage(bufferedImageLoad.width, bufferedImageLoad.height, BufferedImage.TYPE_BYTE_BINARY)

        for (y in 0..bufferedImageLoad.height - 1) {
            for (x in 0..bufferedImageLoad.width - 1) {
                bufferedImage.setRGB(x, y, bufferedImageLoad.getRGB(x, y))
            }
        }

        ImageIO.write(bufferedImage, "bmp", fileObj).also {
            if (it == false)
                throw IOException("No appropriate writer found")
        }
    }

    open fun File.replaceExtension(extension: String): File =
            File(this.parentFile, this.nameWithoutExtension + "." + extension)

    /**
     * Transcode svg to jpeg
     * @param input Input stream
     * @param output Output stream
     */
    open fun transcodeSvg2Jpg(input: InputStream, output: OutputStream) {
        val inputTranscoder = TranscoderInput(input)
        val outputTranscoder = TranscoderOutput(output)

        JPEGTranscoder().also {
            it.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, 0.9.toFloat())
            it.addTranscodingHint(JPEGTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE)
            it.transcode(inputTranscoder, outputTranscoder)
        }
    }

    override fun getStatus(scanCode: String): List<ParcelServiceV1.ParcelStatus> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLabel(parcelId: Long): Response {
        TODO("not implemented")
        val parcel = this.findParcel(parcelId)
        if (parcel != null) {
            return documentService.printParcelLabel(parcelId = parcelId)
        } else {
            throw RestProblem(title = "No parcel could be found with the id [$parcelId]", status = Response.Status.NOT_FOUND)
        }
    }

    override fun findParcels(scanCode: String, reference: String): List<OrderService.Order.Parcel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findParcel(parcelId: Long): OrderService.Order.Parcel? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getWebStatus(searchRef: String, zipCode: String?): List<ParcelServiceV1.ParcelWebStatus> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


