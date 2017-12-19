package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.service.internal.entity.update.UpdateInfo
import sx.io.serialization.Serializable
import sx.mq.jms.channel
import sx.rs.PATCH
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

    companion object {
        const val NODE_UID = "node-uid"
    }

    /**
     * Node status/information message
     */
    @Serializable(0xd0479c08b29be4)
    data class Info(
            /** Node uid */
            val uid: String = "",
            /** Bundle name */
            val bundleName: String = "",
            /** Application bundle version */
            val bundleVersion: String = "",
            /** Node hardware device serial number */
            val hardwareSerialNumber: String? = null,
            /** Opaque system info (usually json blob) */
            var systemInformation: String = ""
    ) { companion object }

    /**
     * Message for requesting diagnostic data from a node
     */
    @Serializable(0xbf735f572c9029)
    class DiagnosticDataRequest

    @PATCH
    @Path("/{${NODE_UID}}/request-diagnostic-data")
    @ApiOperation(value = "Request diagnostic data from remote node", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun requestDiagnosticData(
            @PathParam(NODE_UID) @ApiParam(value = "Node uid") nodeUid: String
    )
}