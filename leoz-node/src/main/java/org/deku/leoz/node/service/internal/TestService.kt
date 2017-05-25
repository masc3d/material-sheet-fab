package org.deku.leoz.node.service.internal

import org.deku.leoz.config.JmsChannels
import org.deku.leoz.service.internal.entity.update.UpdateInfo
import sx.mq.jms.channel

/**
 * Created by masc on 09.10.15.
 */
@javax.inject.Named
@sx.rs.auth.ApiKey(false)
@javax.ws.rs.Path("internal/v1/test")
class TestService : org.deku.leoz.service.internal.TestService {
    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    @javax.inject.Inject
    private lateinit var blzService: org.deku.leoz.ws.blz.BLZServicePortType

    @javax.inject.Inject
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
        JmsChannels.mobile.topic.channel().use {
            it.send(UpdateInfo(bundleName = "leoz-test-${++testCounter}"))
        }
    }
}