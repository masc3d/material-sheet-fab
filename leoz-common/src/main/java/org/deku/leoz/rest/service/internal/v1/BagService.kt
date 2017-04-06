package org.deku.leoz.rest.service.internal.v1

import io.swagger.annotations.*
import org.deku.leoz.rest.entity.internal.v1.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by 27694066 on 20.02.2017.
 **/
@Path("internal/v1/bag")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Bag operations")
interface BagService {

    companion object {
        // REST parameter constants
        const val ID = "id"
        const val COLLI = "colli"
        const val DEPOT = "depot"
        const val POSITION = "position"
    }

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
        BAG_FOR_DEPOT_ALREADY_EXISTS(2000),
        UPDATE_MOVEPOOL_FAILED(2050),
        INSERT_SEAL_MOVE_WHITE_FAILED(2051),
        INSERT_SEAL_MOVE_YELLOW_FAILED(2052),
        BAG_ALREADY_INITIALZED(2060),
        UPDATE_DEPOTLIST_FAILED(2061),
        SECTION_MISSING(2062),
        POSITION_MISSING(2063),
        BAG_COLLIENR_MISSING(2065),
        BAG_COLLIENR_NOT_VALID(2066),
        BAG_COLLIENR_MISSING_CHECK_DIGIT(2067),
        BAG_COLLIENR_WRONG_CHECK_DIGIT(2068),
        NO_DATA(2069)
    }

    @POST
    @Path("/{id}/initialize")
    @ApiOperation("Initialize bag")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun initialize(
            @ApiParam(value = "Bag id", example = "700100000008") @PathParam(ID) bagId: String?,
            @ApiParam(value = "Bag init request") bagInitRequest: BagInitRequest): Boolean

    @GET
    @Path("/{id}/is-free")
    @ApiOperation("Check if bag is free")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun isFree(
            @ApiParam(value = "Bag id", example = "700100000008") @PathParam(ID) bagId: String?,
            @ApiParam(value = "Depot", example = "20") @QueryParam(DEPOT) depotNr: Int?): Boolean

    @GET
    @Path("/{id}/is-ok")
    @ApiOperation("Check if bag is ok")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun isOk(
            @ApiParam(value = "Bag id", example = "700100000008") @PathParam(ID) bagId: String?,
            @ApiParam(value = "Bag colli nr", example = "100710000002") @QueryParam(COLLI) colliNr: String?): BagResponse

    @GET
    @Path("/util/number-range")
    @ApiOperation("Get number range")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun getNumberRange(): BagNumberRange

    @GET
    @Path("/section/{id}")
    @ApiOperation("Get all section depots")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun getSectionDepots(
            @ApiParam(value = "Section", example = "1") @PathParam(ID) section: Int?,
            @ApiParam(value = "Position", example = "1") @QueryParam(POSITION) position: Int?): List<String>

    @GET
    @Path("/section/{id}/left")
    @ApiOperation("Get section depots left")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun getSectionDepotsLeft(
            @ApiParam(value = "Section", example = "1") @PathParam(ID) section: Int?,
            @ApiParam(value = "Position", example = "1") @QueryParam(POSITION) position: Int?): SectionDepotsLeft
}
