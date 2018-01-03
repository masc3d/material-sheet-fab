package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.model.VehicleType
import sx.io.serialization.Serializable
import sx.rs.auth.ApiKey
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Location service V2
 * Created by helke on 24.05.17.
 */
@Path("internal/v2/location")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Location service")
@ApiKey(true)
interface LocationServiceV2 {
    companion object {
        const val USER_ID = "user-id"
        const val DEBITOR_ID = "debitor-id"
        const val FROM = "from"
        const val TO = "to"
        const val DURATION = "duration"
        const val LON_FIRST = "lon-first"
        const val LAT_FIRST = "lat-first"
        const val LON_SECOND = "lon-second"
        const val LAT_SECOND = "lat-second"
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
            val accuracy: Float? = null,
            var vehicleType: VehicleType? = null
    )

    /**
     *
     * Created by helke on 24.05.17.
     */
    @ApiModel(value = "GpsData Model V2")
    @Serializable(0x4f1c7194f28566)
    data class GpsData(
            @get:ApiModelProperty(value = "User identifier")
            var userId: Int? = null,
            @get:ApiModelProperty(required = false, value = "GPS data points")
            var gpsDataPoints: List<GpsDataPoint>? = null
    )

    /**
     * GPS message sent by nodes/devices
     */
    @Serializable(0x264688A4A20811)
    data class GpsMessage(
            var userId: Int? = null,
            var nodeKey: String? = null,
            var dataPoints: Array<GpsDataPoint>? = null
    )

    /**
     * Get location
     * @param email User email
     */
    @GET
    @Path("/")
    @ApiOperation(value = "Get location data", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun get(
            @QueryParam(USER_ID) @ApiParam(value = "User id") userId: Int? = null,
            @QueryParam(DEBITOR_ID) @ApiParam(value = "Debitor id") debitorId: Int? = null,
            @QueryParam(FROM) @ApiParam(value = "from", example = "05/31/2017 00:30:00 +0100") from: Date? = null,
            @QueryParam(TO) @ApiParam(value = "to") to: Date? = null,
            @HeaderParam(Rest.API_KEY) @ApiParam(hidden = true) apiKey: String?
    ): List<GpsData>

    /**
     * Get recent location
     * @param email User email
     */
    @GET
    @Path("/recent")
    @ApiOperation(value = "Get recent location data", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getRecent(
            @QueryParam(USER_ID) @ApiParam(value = "User id") userId: Int? = null,
            @QueryParam(DEBITOR_ID) @ApiParam(value = "Debitor id") debitorId: Int? = null,
            @QueryParam(DURATION) @ApiParam(value = "Duration in Minutes") duration: Int? = null,
            @HeaderParam(Rest.API_KEY) @ApiParam(hidden = true) apiKey: String?
    ): List<GpsData>

    @GET
    @Path("/distance")
    @ApiOperation(value = "Get distance between two locations in km", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiKey(false)
    fun getDistance(
            @QueryParam(LON_FIRST) @ApiParam(value = "first longitude", required = true, example = "8.3926") lonFirst: Double,
            @QueryParam(LAT_FIRST) @ApiParam(value = "first latitude", required = true, example = "49.5131") latFirst: Double,
            @QueryParam(LON_SECOND) @ApiParam(value = "first longitude", required = true, example = "9.585760") lonSecond: Double,
            @QueryParam(LAT_SECOND) @ApiParam(value = "first longitude", required = true, example = "50.9082916666667") latSecond: Double
    ): Double
}