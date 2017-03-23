package org.deku.leoz.rest.service.internal.v1

import io.swagger.annotations.*
import org.deku.leoz.rest.entity.internal.v1.BagInitRequest
import sx.rs.ApiKey
import javax.ws.rs.core.MediaType
import javax.ws.rs.*

/**
 * Created by 27694066 on 20.02.2017.
 **/
@Path("internal/v1/bag")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Bag operations")
interface BagService {

    @GET
    @Path("/{id}")
    fun get(@PathParam("id") id: String): String

    enum class ErrorCode constructor(private val mValue: Int) {
        BAG_ID_MISSING(1000),
        BAG_ID_NOT_VALID(1050),
        BAG_ID_MISSING_CHECK_DIGIT(1100),
        BAG_ID_WRONG_CHECK_DIGIT(1150),
        WHITE_SEAL_MISSING(1500),
        WHITE_SEAL_NOT_VALID(1550),
        WHITE_SEAL_MISSING_CHECK_DIGIT(1600),
        WHITE_SEAL_WRONG_CHECK_DIGIT(1650),
        YELLOW_SEAL_MISSING(1700),
        YELLOW_SEAL_NOT_VALID(1750),
        YELLOW_SEAL_MISSING_CHECK_DIGIT(1800),
        YELLOW_SEAL_WRONG_CHECK_DIGIT(1850),
        DEPOT_NR_MISSING(1900),
        DEPOT_NR_NOT_VALID(1950),
        BAG_FOR_DEPOT_ALREADY_EXISTS(2000)
    }

    @POST
    @Path("/initialize")
    @ApiOperation("Initialize Bag")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun initialize(@ApiParam(value = "BagInitRequest") bagInitRequest: BagInitRequest): Boolean
}
