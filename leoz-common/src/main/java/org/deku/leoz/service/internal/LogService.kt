package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.Authorization
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.config.Rest
import org.deku.leoz.model.UserRole
import org.deku.leoz.rest.RestrictRoles
import org.deku.leoz.time.ShortDate
import sx.rs.auth.ApiKey
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Recovery service
 * Created by masc on 25.05.18.
 */
@Path("internal/v1/log")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Log operations", authorizations = arrayOf(Authorization(Rest.API_KEY)))
@ApiKey
@RestrictRoles(UserRole.ADMIN)
interface LogService {
    companion object {
        const val BUNDLE = "bundle"
        const val NODE_UID = "node-uid"
        const val USER_ID = "user-id"
    }

    /**
     * Dump stations
     */
    @GET
    @Path("/download")
    @ApiOperation(value = "Download log files")
    fun download(
            @QueryParam(BUNDLE)
            bundleType: BundleType? = null,
            @QueryParam(USER_ID)
            userId: Long? = null,
            @QueryParam(NODE_UID)
            nodeUid: String? = null
    ): Response
}
