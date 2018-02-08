package org.deku.leoz.node.service.pub

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
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
import org.deku.leoz.node.Storage
import org.slf4j.LoggerFactory
import java.awt.Color
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@Named
@Path("v1/document")
class DocumentService: DocumentService {

    private val log = LoggerFactory.getLogger(this.javaClass)
    //private val storage: Storage = Kodein.global.instance()

    private val PPI: Float = 72f
    private val PPM: Float = 1 / (10 * 2.54f) * PPI

    override fun printParcelLabel(labelRequest: DocumentService.LabelRequest?, parcelId: Long?, parcelNo: String?): Response {


        return when {
            parcelId != null -> {
                throw RestProblem(title = "Not yet implemented", status = Response.Status.NOT_IMPLEMENTED)
            }

            parcelNo != null -> {
                throw RestProblem(title = "Not yet implemented", status = Response.Status.NOT_IMPLEMENTED)
            }

            labelRequest != null -> {
                val documentFile: File = generateLabelPdf(labelRequest, File("/"))

                Response
                        .ok(documentFile, MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"label.pdf\"")
                        .header(HttpHeaders.CONTENT_LENGTH, documentFile.length().toString())
                        .build()
            }

            else -> {
                throw RestProblem(title = "Not parameter given", status = Response.Status.BAD_REQUEST)
            }
        }
    }

    fun generateLabelPdf(request: DocumentService.LabelRequest, targetDirectory: File): File {
        val documentName: String = "${UUID.randomUUID().toString()}-label.pdf"
        val documentFile: File = targetDirectory.resolve(documentName)

        if (documentFile.exists())
            documentFile.delete()

        val font = PDType1Font.HELVETICA
        val document: PDDocument = PDDocument()
        val page: PDPage = PDPage(PDRectangle(4f * PPI, 6f * PPI))

        document.addPage(page)
        val contentStream = PDPageContentStream(document, page)

        contentStream.setNonStrokingColor(Color.BLACK);

//        contentStream.addRect(0.8f, 5.5f, 2.36f, 0.6f)
//        contentStream.fill()

        contentStream.writeText(
                text = request.parcelNumber,
                xOffset = 1f * PPI,
                yOffset = 4.88f * PPI
        )

        contentStream.writeText(
                text = "Gewicht",
                xOffset = 1f * PPI,
                yOffset = 4.68f * PPI,
                size = 10f
        )

        contentStream.writeText(
                text = "Sendungsnummer: ${request.orderNumber}",
                xOffset = 1f * PPI,
                yOffset = 4.40f * PPI,
                font = PDType1Font.HELVETICA_BOLD,
                size = 8f
        )

        contentStream.writeLine(0.4f * PPI, 4.3f * PPI, 3.6f * PPI, 4.3f * PPI)

        contentStream.writeText(
                text = "NST 1    ${request.consignee.stationNo}",
                xOffset = 1f * PPI,
                yOffset = 4.13f * PPI,
                font = PDType1Font.HELVETICA_BOLD,
                size = 12f
        )

        contentStream.writeLine(0.4f * PPI, 4.1f * PPI, 3.6f * PPI, 4.1f * PPI)

        contentStream.writeText(
                text = "Absender 1           Auftraggeber: ${request.clientStationNo}            Abholer: ${request.clientStationNo}",
                xOffset = 0.4f * PPI,
                yOffset = 3.78f * PPI,
                font = PDType1Font.HELVETICA_BOLD
        )

//        contentStream.writeText(
//                text = "${request.consignor.name1}",
//                xOffset = 0.4f * PPI,
//                yOffset = 3.6f * PPI
//        )

        contentStream.writeParagraph(xOffset = 0.4f * PPI, yOffset = 3.6f * PPI, font = PDType1Font.HELVETICA, size = 7f, text = arrayOf("${request.consignor.name1}", "${request.consignor.name2}", "${request.consignor.name3}           Tel.: ${request.consignor.phone}"))

//        contentStream.beginText()
//        contentStream.setFont( font, 7f )
//        contentStream.moveTextPositionByAmount( 0.4f * PPI, 3.5f * PPI )
//        contentStream.showText( "${request.consignor.name2}" )
//        contentStream.endText()
//
//        contentStream.beginText()
//        contentStream.setFont( font, 7f )
//        contentStream.moveTextPositionByAmount( 0.4f * PPI, 3.4f * PPI )
//        contentStream.showText( "${request.consignor.name3}           Tel.: ${request.consignor.phone}" )
//        contentStream.endText()

        contentStream.beginText()
        contentStream.setFont( font, 7f )
        contentStream.moveTextPositionByAmount( 0.4f * PPI, 3.3f * PPI )
        contentStream.showText( "${request.consignor.street} ${request.consignor.streetNo}" )
        contentStream.endText()

        contentStream.beginText()
        contentStream.setFont( PDType1Font.HELVETICA_BOLD, 7f )
        contentStream.moveTextPositionByAmount( 0.4f * PPI, 3.2f * PPI )
        contentStream.showText( "${request.consignor.country} - ${request.consignor.zipCode} ${request.consignor.city}" )
        contentStream.endText()

        contentStream.beginText()
        contentStream.setFont( PDType1Font.HELVETICA_BOLD, 15f )
        contentStream.moveTextPositionByAmount( 0.4f * PPI, 2.9f * PPI )
        contentStream.showText( "${if (request.appointment.notBeforeStart) "F " else ""}${SimpleDateFormat("dd.MM.yyyy    HH:mm", CountryCode.valueOf(request.consignee.country).toLocale()).format(request.appointment.dateStart) } - ${SimpleDateFormat("HH:mm", CountryCode.valueOf(request.consignee.country).toLocale()).format(request.appointment.dateEnd)}" )
        contentStream.endText()

        val fontSizeConsignee = 9f

        contentStream.beginText()
        contentStream.setFont( PDType1Font.HELVETICA_BOLD, fontSizeConsignee )
        contentStream.moveTextPositionByAmount( 0.4f * PPI, 2.7f * PPI )
        contentStream.showText( "Empfänger" )
        contentStream.endText()

        contentStream.beginText()
        contentStream.setFont( font, fontSizeConsignee )
        contentStream.moveTextPositionByAmount( 0.4f * PPI, 2.55f * PPI )
        contentStream.showText( "${request.consignee.name1}" )
        contentStream.endText()

        contentStream.beginText()
        contentStream.setFont( font, fontSizeConsignee )
        contentStream.moveTextPositionByAmount( 0.4f * PPI, 2.4f * PPI )
        contentStream.showText( "${request.consignee.name2}" )
        contentStream.endText()

        contentStream.beginText()
        contentStream.setFont( font, fontSizeConsignee )
        contentStream.moveTextPositionByAmount( 0.4f * PPI, 2.25f * PPI )
        contentStream.showText( "${request.consignee.name3}" )
        contentStream.endText()

        contentStream.beginText()
        contentStream.setFont( font, fontSizeConsignee )
        contentStream.moveTextPositionByAmount( 0.4f * PPI, 2.1f * PPI )
        contentStream.showText( "Tel.:${request.consignee.phone}" )
        contentStream.endText()

        contentStream.beginText()
        contentStream.setFont( font, fontSizeConsignee )
        contentStream.moveTextPositionByAmount( 0.4f * PPI, 1.95f * PPI )
        contentStream.showText( "${request.consignee.street} ${request.consignor.streetNo}" )
        contentStream.endText()

        contentStream.beginText()
        contentStream.setFont( PDType1Font.HELVETICA_BOLD, fontSizeConsignee )
        contentStream.moveTextPositionByAmount( 0.4f * PPI, 1.8f * PPI )
        contentStream.showText( "${request.consignee.country} - ${request.consignor.zipCode} ${request.consignor.city}" )
        contentStream.endText()

        // Make sure that the content stream is closed:
        contentStream.close()

// Save the results and ensure that the document is properly closed:
        document.save(documentFile)
        document.close()

        return documentFile
    }
}

fun PDPageContentStream.writeText(text: String, xOffset: Float, yOffset: Float, size: Float = 7f, font: PDFont = PDType1Font.HELVETICA) {
    this.beginText()
    this.setFont(font, size)
    this.newLineAtOffset(xOffset, yOffset)
    this.showText(text)
    this.endText()
}

fun PDPageContentStream.writeText(text: String, size: Float = 7f, font: PDFont = PDType1Font.HELVETICA) {
    this.beginText()
    this.setFont(font, size)
    this.newLine()
    this.showText(text)
    this.endText()
}

fun PDPageContentStream.writeLine(startX: Float, startY: Float, endX: Float, endY: Float) {
    this.moveTo(startX, startY)
    this.lineTo(endX, endY)
    this.stroke()
}

fun PDPageContentStream.writeParagraph(xOffset: Float, yOffset: Float, gap: Float = 0.1f, font: PDFont = PDType1Font.HELVETICA, size: Float = 7f, text: Array<String>) {
    for ((i, line) in text.withIndex()) {
        this.beginText()
        if (i != 0)
            yOffset -= gap

        this.newLineAtOffset(xOffset, yOffset)
        this.setFont(font, size)
        this.showText(line)
        this.endText()
    }
}