package org.deku.leoz.node.ws

import org.deku.leoz.node.config.ApplicationTestConfiguration
import org.deku.leoz.node.config.SoapClientConfiguration
import org.deku.leoz.ws.blz.BLZServicePortType
import org.deku.leoz.ws.gls.shipment.CancelParcelResponse
import org.deku.leoz.ws.gls.shipment.ShipmentProcessingPortType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import javax.inject.Inject


/**
 * Created by masc on 12/03/2017.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        ApplicationTestConfiguration::class,
        SoapClientConfiguration::class
        ))
class SoapWebServiceTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var blzService: BLZServicePortType

    @Inject
    private lateinit var glsShipmentProcessingService: ShipmentProcessingPortType

    @Test
    fun testBlzService() {
        log.info(this.blzService.getBank("50661639").bezeichnung)
    }

    @Test
    fun testCancelParcelService() {
        val cancelResponse: CancelParcelResponse = glsShipmentProcessingService.cancelParcelByID("Z8TU3MWG")

        if (cancelResponse.result != null) {
            if (cancelResponse.result.equals("CANCELLATION_PENDING", ignoreCase = true) || cancelResponse.result.equals("CANCELLED", ignoreCase = true)) { //TODO Check for other possible results

            } else {
                throw Exception("Cancellation failed. Response: [" + cancelResponse.result + "]")
            }
        } else {
            Assert.fail()
        }
    }
}