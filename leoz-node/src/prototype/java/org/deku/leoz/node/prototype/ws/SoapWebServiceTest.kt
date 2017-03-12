package org.deku.leoz.node.prototype.ws

import org.deku.leoz.node.config.SoapClientConfiguration
import org.deku.leoz.node.test.config.ApplicationTestConfiguration
import org.deku.leoz.ws.blz.BLZService
import org.deku.leoz.ws.blz.BLZServicePortType
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

    @Test
    fun testBlzService() {
        log.info(this.blzService.getBank("50661639").bezeichnung)
    }
}