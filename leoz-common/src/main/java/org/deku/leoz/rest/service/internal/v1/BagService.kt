package org.deku.leoz.rest.service.internal.v1

import io.swagger.annotations.*
import org.deku.leoz.rest.entity.internal.v1.*
import sx.rs.PATCH
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
//import org.deku.leoz.central.data.jooq.tables.records

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
        const val UNIT = "unit"
        const val DEPOT = "depot"
        const val POSITION = "position"
    }

    @GET
    @Path("/{$ID}")
    fun get(@PathParam(ID) id: String): String

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
        BAG_UNITNO_MISSING(2065),
        BAG_UNITNO_NOT_VALID(2066),
        BAG_UNITNO_MISSING_CHECK_DIGIT(2067),
        BAG_UNITNO_WRONG_CHECK_DIGIT(2068),
        NO_DATA(2069),
        NO_RUN_ID(2070),
        NO_DATA_TO_RUN_ID(2071),
        SCAN_ID_NOT_VALID(2072),
        SCAN_ID_MISSING(2073),
        SCAN_ID_WRONG_CHECK_DIGIT(2074)
    }

    @PATCH
    @Path("/{$ID}/initialize")
    @ApiOperation("Initialize bag")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun initialize(
            @ApiParam(value = "Bag id", example = "700100000008") @PathParam(ID) bagId: String?,
            @ApiParam(value = "Bag init request") bagInitRequest: BagInitRequest): Boolean

    @GET
    @Path("/{$ID}/is-free")
    @ApiOperation("Check if bag is free")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun isFree(
            @ApiParam(value = "Bag id", example = "700100000008") @PathParam(ID) bagId: String?,
            @ApiParam(value = "Depot", example = "20") @QueryParam(DEPOT) depotNr: Int?): Boolean

    @GET
    @Path("/{$ID}/is-ok")
    @ApiOperation("Check if bag is ok")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun isOk(
            @ApiParam(value = "Bag id", example = "700100000008") @PathParam(ID) bagId: String?,
            @ApiParam(value = "Bag unit number", example = "100710000007") @QueryParam(UNIT) unitNo: String?): BagResponse

    @GET
    @Path("/util/number-range")
    @ApiOperation("Get number range")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun getNumberRange(): BagNumberRange

    @GET
    @Path("/section/{$ID}")
    @ApiOperation("Get all section depots")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun getSectionDepots(
            @ApiParam(value = "Section", example = "1") @PathParam(ID) section: Int?,
            @ApiParam(value = "Position", example = "1") @QueryParam(POSITION) position: Int?): List<String>

    @GET
    @Path("/section/{$ID}/left")
    @ApiOperation("Get section depots left")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun getSectionDepotsLeft(
            @ApiParam(value = "Section", example = "1") @PathParam(ID) section: Int?,
            @ApiParam(value = "Position", example = "1") @QueryParam(POSITION) position: Int?): SectionDepotsLeft

    @GET
    @Path("/diff")
    @ApiOperation("Get diff list")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun getDiff():List<BagDiff>

    @PATCH
    @Path("/{$ID}/arrival")
    @ApiOperation("Line Arrival")
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun lineArrival(
            @ApiParam(value = "Scan id", example = "10055618") @PathParam(ID) scanId: String?): BagResponse


}
