package org.deku.leoz.service.internal

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import io.swagger.annotations.*
import org.deku.leoz.service.internal.entity.HEADERPARAM_APIKEY
import sx.io.serialization.Serializable
import java.util.*

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
        const val FROM = "from"
        const val TO = "to"
        const val DURATION = "duration"
    }

    /**
     * A generic GPS data point used in leoz prototols
     * Created by 27694066 on 26.05.2017.
     */
    @Serializable(0x5af819e313304e)
    data class GpsDataPoint(
            val latitude: Double? = null,
            val longitude: Double? = null,
            val time: Date? = null,
            val speed: Float? = null,
            val bearing: Float? = null,
            val altitude: Double? = null,
            val accuracy: Float? = null)

    /**
     *
     * Created by helke on 24.05.17.
     */
    @ApiModel(description = "GpsData Model")
    @Serializable(0x730a673f58f04d)
    data class GpsData(
            @get:ApiModelProperty(example = "foo@bar.com", required = true, value = "User identifier")
            var userEmail: String? = null,
            @get:ApiModelProperty(required = false, value = "Positions")
            var gpsDataPoints: List<GpsDataPoint>? = null
    )

    /**
     * Get location
     * @param email User email
     */
    @GET
    @Path("/")
    @ApiOperation(value = "Get location data")
    fun get(
            @QueryParam(EMAIL) @ApiParam(value = "User email address") email: String? = null,
            @QueryParam(DEBITOR_ID) @ApiParam(value = "Debitor id") debitorId: Int? = null,
            @QueryParam(FROM) @ApiParam(value = "from", example = "05/31/2017 00:30:00 +0100") from: Date? = null,
            @QueryParam(TO) @ApiParam(value = "to") to: Date? = null,
            @HeaderParam(HEADERPARAM_APIKEY) @ApiParam(hidden = true) apiKey: String?
    ): List<GpsData>

    /**
     * Get recent location
     * @param email User email
     */
    @GET
    @Path("/recent/")
    @ApiOperation(value = "Get recent location data")
    fun getRecent(
            @QueryParam(EMAIL) @ApiParam(value = "User email address") email: String? = null,
            @QueryParam(DEBITOR_ID) @ApiParam(value = "Debitor id") debitorId: Int? = null,
            @QueryParam(DURATION) @ApiParam(value = "Duration") duration: Int? = null,
            @HeaderParam(HEADERPARAM_APIKEY) @ApiParam(hidden = true) apiKey: String?
    ): List<GpsData>
}