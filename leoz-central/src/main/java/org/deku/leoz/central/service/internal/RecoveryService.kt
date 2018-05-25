package org.deku.leoz.central.service.internal

import org.deku.leoz.bundle.BundleType
import org.deku.leoz.model.SalutationType
import org.deku.leoz.node.Storage
import org.deku.leoz.service.internal.ParcelServiceV1
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sx.log.slf4j.info
import sx.log.slf4j.warn
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType

/**
 * Recovery service
 * Created by masc on 25.05.18.
 */
@Component
@Path("internal/v1/recover")
class RecoveryService : org.deku.leoz.service.internal.RecoveryService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var storage: Storage

    @Inject
    private lateinit var parcelService: org.deku.leoz.central.service.internal.ParcelServiceV1

    /**
     * Mobile log parcel message parser
     */
    class MobileLogParcelMessageParser {

        private val log = LoggerFactory.getLogger(this.javaClass)

        companion object {

            private val log = LoggerFactory.getLogger(MobileLogParcelMessageParser::class.java)

            private val reParcelMessage by lazy {
                Regex(
                        "^.*\\[ParcelMessage\\(userId=(.*), nodeId=(.*),.* events=\\[(.*)\\], deliveredInfo=(.*), signatureOnPaperInfo=(.*), postboxDeliveryInfo=(.*)",
                        options = setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL))
            }

            private val reEvent by lazy {
                Regex(
                        "event=(.*), reason=(.*), parcelId=(.*), time=(.*), latitude=(.*), longitude=(.*), fromStation"
                )
            }

            private val reDeliveredInfo1 by lazy {
                Regex(
                        "DeliveredInfo\\(recipient=(.*), recipientStreet=(.*), recipientStreetNo=(.*), recipientSalutation=(.*), signature=(.*), mimetype=(.*)\\)",
                        options = setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL)
                )
            }

            private val reDeliveredInfo2 by lazy {
                Regex(
                        "DeliveredInfo\\(recipient=(.*), signature=(.*), mimetype=(.*)\\)",
                        options = setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL)
                )
            }

            /**
             * Parse parcel message from mobile log file (line)
             * @param line line to parse
             */
            fun parse(line: String): ParcelServiceV1.ParcelMessage? {

                fun String.nullableString(): String? = if (this == "null") null else this

                val mr = reParcelMessage.find(line)

                if (mr == null)
                    return null

                val userId = mr.groups[1]!!.value.toInt()
                val nodeId = mr.groups[2]!!.value


                val formatEn = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)

                fun parseDate(date: String): Date {
                    return formatEn.parse(date
                            // Deal with localized timezone names within timestamps
                            .replace("MESZ", "CEST")
                            .replace("MEZ", "CET")
                    )
                }

                val events = mr.groups[3]!!.value.split("Event(")
                        .mapNotNull {
                            reEvent.find(it)?.let {
                                ParcelServiceV1.Event(
                                        event = it.groups[1]!!.value.toInt(),
                                        reason = it.groups[2]!!.value.toInt(),
                                        parcelId = it.groups[3]!!.value.toLong(),
                                        time = parseDate(it.groups[4]!!.value),
                                        latitude = it.groups[5]!!.value.toDoubleOrNull(),
                                        longitude = it.groups[6]!!.value.toDoubleOrNull()
                                )
                            }
                        }

                val deliveredInfoText = mr.groups[4]!!.value.nullableString()

                val deliveredInfo = if (deliveredInfoText != null) {
                    reDeliveredInfo1.find(deliveredInfoText)
                            ?.let {
                                ParcelServiceV1.ParcelMessage.DeliveredInfo(
                                        recipient = it.groups[1]!!.value.nullableString(),
                                        recipientStreet = it.groups[2]!!.value.nullableString(),
                                        recipientStreetNo = it.groups[3]!!.value.nullableString(),
                                        recipientSalutation = try {
                                            SalutationType.valueOf(it.groups[4]!!.value)
                                        } catch (e: IllegalArgumentException) {
                                            null
                                        },
                                        signature = it.groups[5]!!.value.nullableString(),
                                        mimetype = it.groups[6]!!.value.nullableString()
                                                ?: MediaType.APPLICATION_SVG_XML
                                )
                            }
                            ?: reDeliveredInfo2.find(deliveredInfoText)
                                    ?.let {
                                        ParcelServiceV1.ParcelMessage.DeliveredInfo(
                                                recipient = it.groups[1]!!.value.nullableString(),
                                                signature = it.groups[2]!!.value.nullableString(),
                                                mimetype = it.groups[3]!!.value.nullableString()
                                                        ?: MediaType.APPLICATION_SVG_XML
                                        )
                                    }

                } else null

                if (!deliveredInfoText.isNullOrEmpty() && deliveredInfo == null) {
                    log.warn { "Skipping message, delivery info could not be parsed [${deliveredInfoText}]" }
                    return null
                }


                val reSignatureOnPaperInfo = Regex(
                        "SignatureOnPaperInfo\\(recipient=(.*), pictureFileUid=(.*)\\)"
                )

                val signatureOnPaperInfo = reSignatureOnPaperInfo.find(mr.groups[5]!!.value)
                        ?.let {
                            ParcelServiceV1.ParcelMessage.SignatureOnPaperInfo(
                                    recipient = it.groups[1]!!.value.nullableString(),
                                    pictureFileUid = it.groups[2]!!.value.nullableString()?.let { UUID.fromString(it) }
                            )
                        }

                val rePostboxDeliveryInfo = Regex(
                        "PostboxDeliveryInfo\\(pictureFileUid=(.*)\\)\\)"
                )

                val postboxDeliveryInfo = rePostboxDeliveryInfo.find(mr.groups[6]!!.value)
                        ?.let {
                            ParcelServiceV1.ParcelMessage.PostboxDeliveryInfo(
                                    pictureFileUid = it.groups[1]!!.value.nullableString()?.let { UUID.fromString(it) }
                            )
                        }

                val message = ParcelServiceV1.ParcelMessage(
                        userId = userId,
                        nodeId = nodeId,
                        events = events.toTypedArray(),
                        deliveredInfo = deliveredInfo,
                        signatureOnPaperInfo = signatureOnPaperInfo,
                        postboxDeliveryInfo = postboxDeliveryInfo
                )

                return message
            }
        }
    }

    override fun recoverMobileParcelMessages(dryRun: Boolean) {
        val mobileLogDir = storage.logDirectory.resolve(BundleType.LEOZ_MOBILE.value)

        val reFilename = Regex("^leoz-mobile-([0-9a-f]+).log$")

        val logFiles = mobileLogDir.listFiles { file: File ->
            reFilename.matches(file.name)
        }

        var recoveredMessageCount = 0

        logFiles.forEach { file ->
            log.info { "Scanning ${file} for parcel messages" }

            file.bufferedReader().use { reader ->
                while (true) {
                    val line = reader.readLine()

                    if (line == null)
                        break

                    if (line.indexOf("[ParcelMessage") >= 0) {
                        // Also include next line as svg contains CR
                        val lineToParse = line + (reader.readLine() ?: "")

                        val message = try {
                            MobileLogParcelMessageParser.parse(
                                    lineToParse
                            )
                        } catch (e: Throwable) {
                            log.error("Failed to parse [${lineToParse}]")
                            throw e
                        }

                        if (message != null) {
                            recoveredMessageCount++

                            log.info { message }

                            if (!dryRun) {
                                this.parcelService.onMessage(message, null)
                            }
                        }
                    }
                }
            }
        }

        log.info { "Recovered ${recoveredMessageCount} message from files: ${logFiles.map { it.name }.joinToString(", ")}" }
    }
}