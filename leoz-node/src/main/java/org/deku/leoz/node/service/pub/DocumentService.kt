package org.deku.leoz.node.service.pub

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.neovisionaries.i18n.CountryCode
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.deku.leoz.service.pub.DocumentService
import sx.rs.RestProblem
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.deku.leoz.model.DekuUnitNumber
import org.deku.leoz.node.Storage
import org.deku.leoz.service.internal.OrderService
import org.krysalis.barcode4j.ChecksumMode
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5Bean
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.ws.rs.core.StreamingOutput


@Named
@Path("v1/document")
class DocumentService : DocumentService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var storage: Storage

    private val PPI: Float = 72f
    private val PPM: Float = 1 / (10 * 2.54f) * PPI
    val dpi = 300

    override fun printParcelLabel(labelRequest: DocumentService.LabelRequest?, parcelId: Long?, parcelNo: String?, returnType: String?): Response {


        return when {
            parcelId != null -> {
                throw RestProblem(title = "Not yet implemented", status = Response.Status.NOT_IMPLEMENTED)
            }

            parcelNo != null -> {
                throw RestProblem(title = "Not yet implemented", status = Response.Status.NOT_IMPLEMENTED)
            }

            labelRequest != null -> {
                val stream = generateLabelPdf(labelRequest, storage.workTmpDataDirectory)

                when (returnType) {
                    DocumentService.BASE64 -> {
                        val base64 = Base64.getEncoder().encodeToString(stream.toByteArray())

                        Response
                                .ok(base64)
                                .type("application/text")
                                .header(HttpHeaders.CONTENT_LENGTH, base64.length)
                                .build()
                    }

                    DocumentService.PDF -> {
                        Response
                                .status(Response.Status.NOT_IMPLEMENTED)
                                .build()
                    }

                    else -> {
                        Response
                                .status(Response.Status.BAD_REQUEST)
                                .build()
                    }
                }
            }

            else -> {
                throw RestProblem(title = "Not parameter given", status = Response.Status.BAD_REQUEST)
            }
        }
    }

    fun generateLabelPdf(request: DocumentService.LabelRequest, targetDirectory: File): ByteArrayOutputStream {
        val outputStream = ByteArrayOutputStream()

        val document: PDDocument = PDDocument()
        val page: PDPage = PDPage(PDRectangle(4f * PPI, 6f * PPI))

        document.addPage(page)
        val contentStream = PDPageContentStream(document, page)

        contentStream.setNonStrokingColor(Color.BLACK)

        val out: ByteArrayOutputStream = ByteArrayOutputStream()
        val itfBean = Interleaved2Of5Bean().also {
            it.checksumMode = ChecksumMode.CP_IGNORE
            it.wideFactor = 1.8
            it.doQuietZone(true)
            it.fontSize = 2.0
        }

        val canvas = BitmapCanvasProvider(
                out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_GRAY, false, 0)

        itfBean.generateBarcode(canvas, DekuUnitNumber.parse(request.parcelNumber).value.label)
        canvas.finish()

        val barcodeImage: PDImageXObject = PDImageXObject.createFromByteArray(document, out.toByteArray(), null)

        contentStream.drawImage(barcodeImage, 0.6f * PPI, 4.8f * PPI, 2.9f * PPI, 0.8f * PPI)

        contentStream.addText(
                text = "Gewicht: ${request.weight} kg      Pkst. ${request.parcelPosition} / ${request.parcelAmount}",
                xOffset = 1f * PPI,
                yOffset = 4.6f * PPI,
                size = 10f
        )

        contentStream.addText(
                text = "Sendungsnummer: ${request.orderNumber}",
                xOffset = 1f * PPI,
                yOffset = 4.40f * PPI,
                font = PDType1Font.HELVETICA_BOLD,
                size = 8f
        )

        contentStream.putLine(0.4f * PPI, 4.3f * PPI, 3.6f * PPI, 4.3f * PPI)

        contentStream.addText(
                text = "NST 1    ${request.consignee.stationNo}",
                xOffset = 1.2f * PPI,
                yOffset = 4.0f * PPI,
                font = PDType1Font.HELVETICA_BOLD,
                size = 20f
        )

        contentStream.putLine(0.4f * PPI, 3.9f * PPI, 3.6f * PPI, 3.9f * PPI)

        contentStream.addText(
                text = "Absender 1           Auftraggeber: ${request.clientStationNo}            Abholer: ${request.clientStationNo}",
                xOffset = 0.4f * PPI,
                yOffset = 3.78f * PPI,
                font = PDType1Font.HELVETICA_BOLD
        )

        contentStream.addParagraph(
                xOffset = 0.4f * PPI,
                yOffset = 3.6f * PPI,
                font = PDType1Font.HELVETICA,
                size = 7f,
                text = arrayOf(
                        "${request.consignor.name1}",
                        "${request.consignor.name2}",
                        "${request.consignor.name3}           Tel.: ${request.consignor.phone}",
                        "${request.consignor.street} ${request.consignor.streetNo}"
                ))

        contentStream.addText(
                text = "${request.consignor.street} ${request.consignor.streetNo}",
                xOffset = 0.4f * PPI,
                yOffset = 3.3f * PPI
        )

        contentStream.addText(
                font = PDType1Font.HELVETICA_BOLD,
                text = "${request.consignor.country} - ${request.consignor.zipCode} ${request.consignor.city}",
                xOffset = 0.4f * PPI,
                yOffset = 3.2f * PPI
        )

        contentStream.addText(
                font = PDType1Font.HELVETICA_BOLD,
                size = 15f,
                text = "${if (request.appointment.notBeforeStart) "F " else ""}${SimpleDateFormat("dd.MM.yyyy    HH:mm", CountryCode.valueOf(request.consignee.country).toLocale()).format(request.appointment.dateStart)} - ${SimpleDateFormat("HH:mm", CountryCode.valueOf(request.consignee.country).toLocale()).format(request.appointment.dateEnd)}",
                xOffset = 0.4f * PPI,
                yOffset = 2.9f * PPI
        )

        contentStream.addText(
                font = PDType1Font.HELVETICA_BOLD,
                size = 9f,
                text = "Empfänger",
                xOffset = 0.4f * PPI,
                yOffset = 2.7f * PPI
        )

        contentStream.addParagraph(
                xOffset = 0.4f * PPI,
                yOffset = 2.55f * PPI,
                font = PDType1Font.HELVETICA,
                size = 9f,
                spacing = 0.15f * PPI,
                text = arrayOf(
                        "${request.consignee.name1}",
                        "${request.consignee.name2}",
                        "${request.consignee.name3}",
                        "Tel.: ${request.consignee.phone}",
                        "${request.consignee.street} ${request.consignee.streetNo}"
                ))

        contentStream.addText(
                font = PDType1Font.HELVETICA_BOLD,
                size = 9f,
                text = "${request.consignee.country} - ${request.consignor.zipCode} ${request.consignor.city}",
                xOffset = 0.4f * PPI,
                yOffset = 1.8f * PPI
        )

        contentStream.addText(
                font = PDType1Font.HELVETICA_BOLD,
                text = "Servicekennzeichen",
                xOffset = 0.4f * PPI,
                yOffset = 1.6f * PPI
        )

        contentStream.addParagraph(
                xOffset = 0.4f * PPI,
                yOffset = 1.5f * PPI,
                text = (request.services ?: listOf("")).toTypedArray()
        )

        // Make sure that the content stream is closed:
        contentStream.close()

        // Save the results and ensure that the document is properly closed:
        document.save(outputStream)
        document.close()

        return outputStream

    }
}

fun PDPageContentStream.addText(text: String, xOffset: Float, yOffset: Float, size: Float = 7f, font: PDFont = PDType1Font.HELVETICA) {
    this.beginText()
    this.setFont(font, size)
    this.newLineAtOffset(xOffset, yOffset)
    this.showText(text)
    this.endText()
}

fun PDPageContentStream.putLine(startX: Float, startY: Float, endX: Float, endY: Float) {
    this.moveTo(startX, startY)
    this.lineTo(endX, endY)
    this.stroke()
}

fun PDPageContentStream.addParagraph(xOffset: Float, yOffset: Float, spacing: Float = 0.1f * 72f, font: PDFont = PDType1Font.HELVETICA, size: Float = 7f, text: Array<String>) {
    var y = yOffset
    for ((i, line) in text.withIndex()) {
        this.beginText()
        if (i != 0)
            y -= spacing

        this.newLineAtOffset(xOffset, y)
        this.setFont(font, size)
        this.showText(line)
        this.endText()
    }
}

fun OrderService.Order.Parcel.toLabelRequest(order: OrderService.Order): DocumentService.LabelRequest {
    return DocumentService.LabelRequest()
}