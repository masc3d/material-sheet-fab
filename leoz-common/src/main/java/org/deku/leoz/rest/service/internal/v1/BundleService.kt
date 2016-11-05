package org.deku.leoz.rest.service.internal.v1

import io.swagger.annotations.*
import org.deku.leoz.service.update.UpdateInfo
import org.deku.leoz.service.update.UpdateInfoRequest
import org.deku.leoz.rest.entity.v1.Routing
import org.deku.leoz.rest.entity.v1.RoutingRequest
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 05.02.16.
 */
@Path("internal/v1/bundle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Bundle operations")
interface BundleService {

    companion object {
        const val BUNDLE = "bundle"
        const val ALIAS = "alias"
    }

    /**
     * Get bundle information
     * @param bundleName Name of bundle to request update info for
     * @param versionAlias Version alias
     */
    @GET
    @Path("/info/{${BUNDLE}}/{${ALIAS}}")
    @ApiOperation(value = "Retrieve bundle information")
    fun info(@PathParam(BUNDLE) bundleName: String, @PathParam(ALIAS) versionAlias: String): UpdateInfo

    @POST
    @Path("/info/request")
    @ApiOperation(value = "Request update information")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun info(@ApiParam(value = "Request") request: UpdateInfoRequest): UpdateInfo
}