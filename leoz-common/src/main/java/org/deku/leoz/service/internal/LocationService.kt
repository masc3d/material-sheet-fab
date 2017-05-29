package org.deku.leoz.service.internal

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import io.swagger.annotations.*
import org.deku.leoz.service.internal.entity.GpsData
import org.deku.leoz.service.internal.entity.HEADERPARAM_APIKEY

/**
 * Created by helke on 24.05.17.
 */
@Path("internal/v1/location")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Location service")
interface LocationService {
    companion object {
        const val EMAIL = "email"
        const val DEBITOR_ID = "debitor-id"
        //const val HEADERPARAM_APIKEY = "x-api-key"
    }

    /**
     * Get user
     * @param email User email
     */
    @GET
    @Path("/")
    @ApiOperation(value = "Get location data")
    fun get(
            @QueryParam(EMAIL) @ApiParam(value = "User email address") email: String? = null,
            @QueryParam(DEBITOR_ID) @ApiParam(value = "Debitor id") debitorId: Int? = null,
            @HeaderParam(HEADERPARAM_APIKEY) @ApiParam(hidden=true) apiKey: String?
    ): List<GpsData>
}