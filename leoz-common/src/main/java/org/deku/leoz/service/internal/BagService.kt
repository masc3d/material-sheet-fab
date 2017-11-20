package org.deku.leoz.service.internal

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.service.entity.ServiceError
import org.deku.leoz.service.internal.entity.BagDiff
import org.deku.leoz.service.internal.entity.BagInitRequest
import org.deku.leoz.service.internal.entity.BagNumberRange
import org.deku.leoz.service.internal.entity.BagResponse
import org.deku.leoz.service.internal.entity.SectionDepotsLeft
import sx.io.serialization.Serializable
import sx.rs.PATCH
import sx.rs.auth.ApiKey
import java.util.*

/**
 * Created by 27694066 on 20.02.2017.
 **/
@Path("internal/v1/bag")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Bag operations")
@ApiKey(false)
interface BagService {

    companion object {
        // REST parameter constants
        const val ID = "id"
        const val UNIT = "unit"
        const val DEPOT = "depot"
        const val POSITION = "position"
        const val WHITESEAL = "whiteseal"
        const val YELLOWSEAL = "yellowseal"
        const val SECTION = "section"
        const val STATION_NO = "station-no"
        const val REDSEAL = "redseal"
        const val TEXT = "text"
    }

    @ApiModel(description = "Bag Model")
    @Serializable(0x32028b91dda15f)
    data class Bag(
            val bagNumber: Long? = null,
            val sealNumberGreen: Long? = null,
            //var status: Int? = null,
            val status: org.deku.leoz.model.BagStatus? = null,
            val statusTimestamp: Date? = null,
            val lastStation: Long? = null,
            val sealNumberYellow: Long? = null,
            val sealNumberRed: Long? = null,
            val orderhub2depot: Long? = null,
            val orderdepot2hub: Long? = null,
            val initStatus: Int = 0,
            val workdate: Date? = null,
            val printed: Int? = null,
            val multibag: Int = 0,
            val movepool: String? = null
    ) {
        var unitNo: Long? = null
        var unitNoBack: Long? = null

    }


    @GET
    @Path("/{${ID}}")
    fun get(@PathParam(ID) id: Long): Bag

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

    @sx.rs.PATCH
    @Path("/{${ID}}/initialize")
    @ApiOperation("Initialize bag", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun initialize(
            @ApiParam(value = "Bag id", example = "700100000008") @PathParam(ID) bagId: String?,
            @ApiParam(value = "Bag init request") bagInitRequest: BagInitRequest): Boolean


    @GET
    @Path("/section/{$SECTION}")
    @ApiOperation("Get all section depots", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun getSectionDepots(
            @ApiParam(value = "Section", example = "1") @PathParam(SECTION) section: Int?,
            @ApiParam(value = "Position", example = "1") @QueryParam(POSITION) position: Int?): List<String>

    @GET
    @Path("/section/{$SECTION}}/left")
    @ApiOperation("Get section depots left", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun getSectionDepotsLeft(
            @ApiParam(value = "Section", example = "1") @PathParam(SECTION) section: Int?,
            @ApiParam(value = "Position", example = "1") @QueryParam(POSITION) position: Int?): SectionDepotsLeft

    @GET
    @Path("/diff")
    @ApiOperation("Get diff list", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun getDiff(): List<BagDiff>

    @sx.rs.PATCH
    @Path("/{$ID}/arrival")
    @ApiOperation("Line Arrival", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun lineArrival(
            @ApiParam(value = "Scan id", example = "10055618") @PathParam(ID) scanId: String?): BagResponse

    @sx.rs.PATCH
    @Path("/{$ID}/in")
    @ApiOperation("Incoming bag", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun bagIn(
            @ApiParam(value = "Bag unit number", example = "100720000004") @PathParam(ID) unitNo: String?,
            @ApiParam(value = "Seal number", example = "900200000001") @QueryParam(YELLOWSEAL) sealNo: String?): BagResponse

    @GET
    @Path("/station/{$STATION_NO}/send-back")
    @ApiOperation("Count bags to send back", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    @ApiResponses(*arrayOf(
            ApiResponse(code = 400, message = "Bad request/parameter", response = ServiceError::class))
    )
    fun getCount2SendBackByStation(
            @PathParam(STATION_NO) @ApiParam(value = "Station number", example = "220", required = true) stationNo: Int

    ): Int


}
