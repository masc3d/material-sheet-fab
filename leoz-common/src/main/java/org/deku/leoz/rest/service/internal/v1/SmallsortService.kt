package org.deku.leoz.rest.service.internal.v1

import io.swagger.annotations.*
import org.deku.leoz.rest.entity.internal.v1.OutgoingBag
import sx.rs.ApiKey
import javax.ws.rs.core.MediaType
import javax.ws.rs.*

/**
 * Created by 27694066 on 20.02.2017.
 */
@Path("internal/v1/smallsort")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Smallsort operations")
interface SmallsortService {

    enum class ErrorCode private constructor(private val mValue: Int) {
        BAG_REFERENCE_MISSING(1000),
        BAG_REFERENCE_NOT_VALID(1050),
        BAG_REFERENCE_MISSING_CHECK_DIGIT(1100),
        BAG_REFERENCE_WRONG_CHECK_DIGIT(1150),
        LEAD_SEAL_MSSING(1500),
        LEAD_SEAL_NOT_VALID(1550),
        LEAD_SEAL_MISSING_CHECK_DIGIT(1600),
        LEAD_SEAL_WRONG_CHECK_DIGIT(1650)
    }

    @GET
    @Path("/close-bag")
    @ApiOperation("Close Bag")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun closebag(@ApiParam(value = "Bag") outgoingBag: OutgoingBag): Boolean
}