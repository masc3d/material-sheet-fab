package org.deku.leoz.node.service

import org.deku.leoz.node.Application
import org.deku.leoz.node.config.ApplicationTestConfiguration
import org.deku.leoz.node.service.pub.DocumentService
import org.deku.leoz.service.internal.OrderService
import org.junit.Test
import org.junit.experimental.categories.Category
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import sx.junit.StandardTest
import sx.time.minusHours
import sx.time.plusHours
import java.io.File
import java.util.*

@Category(StandardTest::class)
open class DocumentTestService {

    @Test
    fun generateLabel() {
        val document = DocumentService().generateLabelPdf(
                request = org.deku.leoz.service.pub.DocumentService.LabelRequest().also {
                    it.appointment = OrderService.Order.Appointment(
                            dateStart = Date(),
                            dateEnd = Date(),
                            notBeforeStart = true
                    )
                    it.clientStationNo = "999"
                    it.orderNumber = "21850041041"
                    it.parcelNumber = "21850041042"
                    it.parcelAmount = 3
                    it.parcelPosition = 2
                    it.consignee = org.deku.leoz.service.pub.DocumentService.LabelRequest.LabelParticipant().also {
                        it.name1 = "Max"
                        it.name2 = "Mustermann"
                        it.name3 = "c/o Fr. Mustermann"
                        it.street = "Dörrwiese"
                        it.stationNo = "002"
                        it.country = "DE"
                        it.city = "Neuenstein"
                        it.zipCode = "36286"
                        it.phone = "+49 6677 950"
                        it.notice = "Bitte abstellen"
                    }
                    it.consignor = org.deku.leoz.service.pub.DocumentService.LabelRequest.LabelParticipant().also {
                        it.name1 = "Max"
                        it.name2 = "Mustermann"
                        it.name3 = "c/o Fr. Mustermann"
                        it.street = "Dörrwiese"
                        it.stationNo = "002"
                        it.country = "DE"
                        it.city = "Neuenstein"
                        it.zipCode = "36286"
                        it.phone = "+49 6677 950"
                    }
                },
                targetDirectory = File("/Users/prangenberg/Desktop/")
        )

        assert(document.exists())
    }
}