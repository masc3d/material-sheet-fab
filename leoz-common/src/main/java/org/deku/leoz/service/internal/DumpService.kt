package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.Authorization
import org.deku.leoz.config.Rest
import org.deku.leoz.model.UserRole
import org.deku.leoz.rest.RestrictRoles
import org.deku.leoz.time.ShortDate
import sx.rs.auth.ApiKey
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Dump service
 * Created by masc on 14.02.18.
 */
@Path("internal/v1/dump")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_OCTET_STREAM)
@Api(value = "Dump operations", authorizations = arrayOf(Authorization(Rest.API_KEY)))
@ApiKey
@RestrictRoles(UserRole.ADMIN)
interface DumpService {
    companion object {
        const val FROM = "from"
        const val STATION_NO = "station-no"
        const val TO = "to"
        const val WITH_STATUS = "with-status"
        const val NODE_UID = "node-uid"
        const val LOADING_DATE = "loading-date"
    }

    /**
     * Dump stations
     */
    @GET
    @Path("/central/station")
    @ApiOperation(value = "Dump central stations")
    fun dumpCentralStations(): Response

    /**
     * Dump routes
     */
    @GET
    @Path("/central/route")
    @ApiOperation(value = "Dump central routes")
    fun dumpCentralRoutes(): Response

    /**
     * Dump delivery list(s)
     */
    @GET
    @Path("/central/deliverylist")
    @ApiOperation(value = "Dump central delivery list(s)")
    fun dumpDeliveryLists(
            @QueryParam(STATION_NO) @ApiParam(value = "Station no", example = "20") stationNo: Int?,
            @QueryParam(FROM) @ApiParam(value = "From date", example = "2018-01-01") from: ShortDate?,
            @QueryParam(TO) @ApiParam(value = "To date", example = "2019-01-01") to: ShortDate?
    ): Response

    @GET
    @Path("/node/tours")
    @ApiOperation(value = "Dump node tour(s)")
    fun dumpTours(): Response

    @POST
    @Path("/central/orders")
    @ApiOperation(value = "Dump central (DEKU) orders by parcel no's")
    fun dumpOrders(
            @ApiParam(value = "Parcel no's") parcelNos: List<String>?,
            @QueryParam(WITH_STATUS) @ApiParam(value = "Export with status", defaultValue = "false", example = "false") @DefaultValue("false") withStatus: Boolean
    ): Response

    @GET
    @Path("/central/mobileLoadedOrders")
    @ApiOperation(value = "Dump node tour(s)")
    fun dumpMobileLoadedOrders(
            @QueryParam(NODE_UID) @ApiParam(value = "UID of mobile node (short)") nodeUidShort: String,
            @QueryParam(LOADING_DATE) @ApiParam(value = "Loading date", example = "2019-01-01") loadingDate: ShortDate
    ): Response
}
