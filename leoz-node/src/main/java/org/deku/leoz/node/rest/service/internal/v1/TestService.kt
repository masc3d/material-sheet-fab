package org.deku.leoz.node.rest.service.internal.v1

import org.deku.leoz.ws.blz.BLZServicePortType
import org.slf4j.LoggerFactory
import sx.rs.auth.ApiKey
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * Created by masc on 09.10.15.
 */
@Named
@ApiKey(false)
@Path("internal/v1/test")
class TestService : org.deku.leoz.rest.service.internal.v1.TestService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var blzService: BLZServicePortType

    @Inject
    private lateinit var glsShipmentProcessingService: org.deku.leoz.ws.gls.shipment.ShipmentProcessingPortType

    /**
     *
     */
    override fun testSoapCall(): Response {
        class Result(val result: String)

        val result = Result(
                blzService.getBank("50661639").bezeichnung)

        return Response
                .ok(result)
                .build()
    }
}