package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.Authorization
import org.deku.leoz.config.Rest
import org.deku.leoz.model.UserRole
import org.deku.leoz.rest.RestrictRoles
import sx.io.serialization.Serializable
import sx.rs.auth.ApiKey
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 05.02.16.
 */
@ApiKey
@RestrictRoles(UserRole.ADMIN)
@Api(value = "Node operations", authorizations = arrayOf(Authorization(Rest.API_KEY)))
@Path("internal/v1/node")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface NodeServiceV1 {

    companion object {
        const val ID = "id"
        const val UID = "uid"
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
    ) {
        companion object
    }

    @Serializable(0x34e6c98d49e854)
    data class Node(
            val id: Long,
            val uid: String
    )

    /**
     * Message for requesting diagnostic data from a node
     */
    @Serializable(0xbf735f572c9029)
    class DiagnosticDataRequest

    /**
     * Get node(s)
     */
    @GET
    @Path("/")
    fun get(
            @QueryParam(ID)
            @ApiParam(value = "Node id(s)")
            id: List<Int>?
    ): List<Node>

    /**
     * Get node by uid
     * @param uid node uid
     */
    @GET
    @Path("{${UID}}")
    @ApiOperation(value = "Get node(s)")
    fun getByUid(
            @PathParam(UID)
            @ApiParam(value = "Node uid or short uid")
            uid: String
    ): Node

    /**
     * Request diagnostic data from a node
     */
    @PATCH
    @Path("/{${UID}}/request-diagnostic-data")
    @ApiOperation(value = "Request diagnostic data from remote node")
    fun requestDiagnosticData(
            @PathParam(UID) @ApiParam(value = "Node uid or short uid") nodeUid: String
    )

    /**
     * Get node configuration
     */
    @GET
    @Path("/{${UID}}/configuration")
    @ApiOperation(value = "Request configuration for specified node key")
    fun getConfiguration(
            @PathParam(value = UID) @ApiParam(value = "Node UID. Full UID required") nodeUid: String
    ): String
}