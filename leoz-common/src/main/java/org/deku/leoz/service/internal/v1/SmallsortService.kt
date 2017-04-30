package org.deku.leoz.service.internal.v1

import io.swagger.annotations.*
import org.deku.leoz.service.entity.internal.v1.OutgoingBag
import org.deku.leoz.service.entity.v1.ServiceError
import javax.ws.rs.core.MediaType
import javax.ws.rs.*

/**
 * Created by 27694066 on 20.02.2017.
 */
@javax.ws.rs.Path("internal/v1/smallsort")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(value = "Smallsort operations")
interface SmallsortService {

    enum class ErrorCode constructor(private val mValue: Int) {
        BAG_REFERENCE_MISSING(1000),
        BAG_REFERENCE_NOT_VALID(1050),
        BAG_REFERENCE_MISSING_CHECK_DIGIT(1100),
        BAG_REFERENCE_WRONG_CHECK_DIGIT(1150),
        LEAD_SEAL_MSSING(1500),
        LEAD_SEAL_NOT_VALID(1550),
        LEAD_SEAL_MISSING_CHECK_DIGIT(1600),
        LEAD_SEAL_WRONG_CHECK_DIGIT(1650)
    }

    @javax.ws.rs.POST
    @javax.ws.rs.Path("/close-bag")
    @io.swagger.annotations.ApiOperation("Close Bag")
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun closebag(@io.swagger.annotations.ApiParam(value = "Bag") outgoingBag: org.deku.leoz.service.entity.internal.v1.OutgoingBag): Boolean
}