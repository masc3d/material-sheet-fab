package org.deku.leoz.service.internal

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import io.swagger.annotations.*
import org.deku.leoz.model.VehicleType
import org.deku.leoz.service.internal.entity.HEADERPARAM_APIKEY
import sx.io.serialization.Serializable
import java.util.*

/**
 * Location service V1
 * Created by helke on 24.05.17.
 */
@Path("internal/v1/location")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Location service")
interface LocationServiceV1 {
    companion object {
        const val EMAIL = "email"
        const val DEBITOR_ID = "debitor-id"
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
            val accuracy: Float? = null,
            var vehicleType: VehicleType? = null
    )

    /**
     *
     * Created by helke on 24.05.17.
     */
    @ApiModel(value = "GpsData Model")
    @Serializable(0x730a673f58f04d)
    data class GpsData(
            @get:ApiModelProperty(value = "User identifier")
            var userId: Int? = null,
            @get:ApiModelProperty(example = "foo@bar.com", required = true, value = "User email")
            var userEmail: String? = null,
            @get:ApiModelProperty(required = false, value = "Positions")
            var gpsDataPoints: List<GpsDataPoint>? = null
    )

    /**
     * GPS message sent by nodes/devices
     */
    @Serializable(0xd307ea744273ae)
    data class GpsMessage(
            var userId: Int? = null,
            var nodeId: String? = null,
            var dataPoints: Array<GpsDataPoint>? = null
    )

    /**
     * Get location
     * @param email User email
     */
    @GET
    @Path("/")
    @ApiOperation(value = "Get location data", hidden = true)
    @Deprecated(message = "This function is deprecated and replaced in LocationServiceV2.", replaceWith = ReplaceWith(expression = "LocationServiceV2.get"), level = DeprecationLevel.WARNING)
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
    @ApiOperation(value = "Get recent location data", hidden = true)
    @Deprecated(message = "This function is deprecated and replaced in LocationServiceV2.", replaceWith = ReplaceWith(expression = "LocationServiceV2.getRecent"), level = DeprecationLevel.WARNING)
    fun getRecent(
            @QueryParam(EMAIL) @ApiParam(value = "User email address") email: String? = null,
            @QueryParam(DEBITOR_ID) @ApiParam(value = "Debitor id") debitorId: Int? = null,
            @QueryParam(DURATION) @ApiParam(value = "Duration") duration: Int? = null,
            @HeaderParam(HEADERPARAM_APIKEY) @ApiParam(hidden = true) apiKey: String?
    ): List<GpsData>
}

/**
 * Location service V2
 * Created by helke on 24.05.17.
 */
@Path("internal/v2/location")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Location service")
interface LocationServiceV2 {
    companion object {
        const val USER_ID = "user-id"
        const val EMAIL = "email"
        const val DEBITOR_ID = "debitor-id"
        const val FROM = "from"
        const val TO = "to"
        const val DURATION = "duration"
    }

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
            var gpsDataPoints: List<LocationServiceV1.GpsDataPoint>? = null
    )

    /**
     * Get location
     * @param email User email
     */
    @GET
    @Path("/")
    @ApiOperation(value = "Get location data")
    fun get(
            @QueryParam(USER_ID) @ApiParam(value = "User id") userId: Int? = null,
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
            @QueryParam(USER_ID) @ApiParam(value = "User id") userId: Int? = null,
            @QueryParam(DEBITOR_ID) @ApiParam(value = "Debitor id") debitorId: Int? = null,
            @QueryParam(DURATION) @ApiParam(value = "Duration in Minutes") duration: Int? = null,
            @HeaderParam(HEADERPARAM_APIKEY) @ApiParam(hidden = true) apiKey: String?
    ): List<GpsData>
}