package org.deku.leoz.node.service.internal

import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.service.internal.entity.update.UpdateInfo
import sx.mq.jms.channel
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Created by masc on 09.10.15.
 */
@Named
@Path("internal/v1/test")
class TestService : org.deku.leoz.service.internal.TestService {
    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var blzService: org.deku.leoz.ws.blz.BLZServicePortType

    @Inject
    private lateinit var glsShipmentProcessingService: org.deku.leoz.ws.gls.shipment.ShipmentProcessingPortType

    /**
     *
     */
    override fun testSoapCall(): javax.ws.rs.core.Response {
        class Result(val result: String)

        val result = Result(
                blzService.getBank("50661639").bezeichnung)

        return javax.ws.rs.core.Response
                .ok(result)
                .build()
    }

    private var testCounter: Int = 0

    override fun testPublishUpdateInfoToMobile() {
        log.trace("Publishing (test) update info to mobile")
        JmsEndpoints.mobile.broadcast.channel().use {
            it.send(UpdateInfo(bundleName = "leoz-test-${++testCounter}"))
        }
    }
}