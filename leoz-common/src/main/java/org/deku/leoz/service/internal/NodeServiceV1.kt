package org.deku.leoz.service.internal

import io.swagger.annotations.*
import sx.io.serialization.Serializable
import sx.rs.auth.ApiKey
import javax.ws.rs.*
import javax.ws.rs.core.*

/**
 * Created by JT on 05.02.16.
 */
@Path("internal/v1/node")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(value = "Node operations")
@ApiKey
interface NodeServiceV1 {

    /**
     * Mobile device status message, sent by mobile nodes
     */
    @Serializable(0xd0479c08b29be4)
    data class MobileStatus(
            /** Node uid */
            val uid: String? = "",
            /** Mobile device serial number */
            val serialNumber: String = ""
    )
}