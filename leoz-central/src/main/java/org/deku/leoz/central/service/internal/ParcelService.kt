package org.deku.leoz.central.service.internal

import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.JPEGTranscoder
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.ParcelProcessing
import org.deku.leoz.central.data.jooq.Tables
import org.deku.leoz.model.AdditionalInfo
import org.deku.leoz.model.Event
import org.deku.leoz.model.FileName
import org.deku.leoz.model.Location
import org.deku.leoz.model.Reason
import org.deku.leoz.node.Storage
import org.deku.leoz.node.rest.DefaultProblem
import org.deku.leoz.service.internal.ParcelServiceV1
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.rs.auth.ApiKey
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

    @Inject
    private lateinit var userRepository: UserJooqRepository

    @Inject
    private lateinit var messagesRepository: MessagesJooqRepository

    @Inject
    private lateinit var parcelProcessing: ParcelProcessing

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
            val parcelNo: Long?
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


            val recordMessages = dslContext.newRecord(Tables.TAD_PARCEL_MESSAGES)
            recordMessages.userId = message.userId
            recordMessages.nodeId = message.nodeId
            recordMessages.parcelId = it.parcelId
            recordMessages.parcelNo = parcelScan
            recordMessages.scanned = it.time.toTimestamp()
            recordMessages.eventValue = it.event
            recordMessages.reasonId = it.reason
            recordMessages.latitude = it.latitude
            recordMessages.longitude = it.longitude
            recordMessages.isProccessed=0
            if (!messagesRepository.saveMsg(recordMessages)) {
                log.error("Problem saving parcel-messages")
            }





            //TODO: Die Werte kz_status und -erzeuger sollten vermutlich über die Enumeration gesetzt werden, damit man die (aktuellen) Primärschlüssel nicht an mehreren Stellen pflegen muss, oder?
            val eventId = it.event
            val event = Event.values().find { it.value == eventId }!!
            val reasonId = it.reason
            val reason = Reason.values().find { it.id == reasonId }!!



            val parcelRecord = parcelRepository.findParcelByUnitNumber(parcelNo)
            parcelRecord ?:
                    throw DefaultProblem(
                            title = "Missing parcelRecord"
                    )
            val orderRecord = parcelRepository.findOrderByOrderNumber(parcelRecord.orderid.toLong())
            orderRecord ?:
                    throw DefaultProblem(
                            title = "Missing orderRecord"
                    )

            when (event) {
                Event.DELIVERED -> {
                    val recipientInfo = StringBuilder()
                    var signature: String? = null
                    var mimetype = "svg"
                    when (reason) {
                        Reason.POSTBOX -> {
                            recipientInfo.append("Postbox")
                        }
                        Reason.NORMAL -> {

                            val addInfo = message.deliveredInfo
                            addInfo ?:
                                    throw DefaultProblem(
                                            title = "Missing structure [DeliveredInfo] for event [$event].[$reason]"
                                    )

                            recipientInfo.append((addInfo.recipient ?: ""))
                            signature = addInfo.signature
                            mimetype = addInfo.mimetype

                            recordMessages.additionalInfo="{\"recipient\":\""+recipientInfo.toString()+"\"}"
                            messagesRepository.saveMsg(recordMessages)



                        }
                        Reason.NEIGHBOUR -> {
                            val addInfo = it.additionalInfo
                            when (addInfo) {
                                is AdditionalInfo.EmptyInfo -> throw DefaultProblem(
                                        title = "Missing structure [DeliveredInfo] for event [$event].[$reason]"
                                )
                                is AdditionalInfo.DeliveredAtNeighborInfo -> {
                                    recipientInfo.append(addInfo.name ?: "")//.append(";adr ").append(addInfo.address ?: "")
                                    signature = addInfo.signature
                                    mimetype = addInfo.mimetype

                                    recordMessages.additionalInfo="{\"name\":\""+recipientInfo.toString()+"\",\"address\":\""+(addInfo.address ?:"")+"\"}"
                                    messagesRepository.saveMsg(recordMessages)
                                }
                            }
                        }


                    }

                    if (signature != null) {
                        val sigPath = saveImage(it.time, Location.SB, signature, parcelScan, message.userId, mimetype, Location.SB_Original)
                        if (sigPath != "")
                            parcelRecord.bmpfilename = sigPath
                        parcelRecord.store()
                    }



                }

                Event.DELIVERY_FAIL -> {


                    val addInfo = it.additionalInfo
                    if (addInfo != null) {
                        when (addInfo) {
                            is AdditionalInfo.NotDeliveredInfo -> {
                                //r.infotext = addInfo.text ?: ""
                                recordMessages.additionalInfo="{\"text\":\""+addInfo.text+"\"}"
                                messagesRepository.saveMsg(recordMessages)
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
                                    //r.infotext = addInfo.cause ?: ""
                                    recordMessages.additionalInfo="{\"text\":\""+addInfo.cause+"\"}"
                                    messagesRepository.saveMsg(recordMessages)
                                }

                            }
                        }
                        Reason.PARCEL_DAMAGED -> {
                            when (addInfo) {
                                is AdditionalInfo.DamagedInfo -> {
                                    //r.infotext = addInfo.description ?: ""
                                    recordMessages.additionalInfo="{\"text\":\""+addInfo.description+"\"}"
                                    messagesRepository.saveMsg(recordMessages)
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



                }
                Event.IN_DELIVERY -> {



                }
                Event.NOT_IN_DEIVERY -> {


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
                            //r.text = addInfo.loadingListNo.toString()
                            recordMessages.additionalInfo="{\"text\":\""+addInfo.loadingListNo.toString()+"\"}"
                            messagesRepository.saveMsg(recordMessages)

                        }
                    }
                }
            }


        }
        if(!parcelProcessing.processMessages()){
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

