package org.deku.leoz.service.internal.v1

import io.swagger.annotations.*
import org.deku.leoz.service.entity.internal.v1.*
import sx.rs.PATCH
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

//import org.deku.leoz.central.data.jooq.tables.records

/**
 * Created by 27694066 on 20.02.2017.
 **/
@javax.ws.rs.Path("internal/v1/bag")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(value = "Bag operations")
interface BagService {

    companion object {
        // REST parameter constants
        const val ID = "id"
        const val UNIT = "unit"
        const val DEPOT = "depot"
        const val POSITION = "position"
        const val WHITESEAL="whiteseal"
        const val YELLOWSEAL="yellowseal"
        const val SECTION="section"
    }

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{${ID}}")
    fun get(@javax.ws.rs.PathParam(ID) id: String): String

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
    @javax.ws.rs.Path("/{${ID}}/initialize")
    @io.swagger.annotations.ApiOperation("Initialize bag")
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun initialize(
            @io.swagger.annotations.ApiParam(value = "Bag id", example = "700100000008") @javax.ws.rs.PathParam(ID) bagId: String?,
            @io.swagger.annotations.ApiParam(value = "Bag init request") bagInitRequest: org.deku.leoz.service.entity.internal.v1.BagInitRequest): Boolean

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{${ID}}/is-free")
    @io.swagger.annotations.ApiOperation("Check if bag is free")
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun isFree(
            @io.swagger.annotations.ApiParam(value = "Bag id", example = "700100000008") @javax.ws.rs.PathParam(ID) bagId: String?,
            @io.swagger.annotations.ApiParam(value = "Depot", example = "20") @javax.ws.rs.QueryParam(DEPOT) depotNr: Int?): Boolean

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{${ID}}/is-ok")
    @io.swagger.annotations.ApiOperation("Check if bag is ok")
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun isOk(
            @io.swagger.annotations.ApiParam(value = "Bag id", example = "700100000008") @javax.ws.rs.PathParam(ID) bagId: String?,
            @io.swagger.annotations.ApiParam(value = "Bag unit number", example = "100710000007") @javax.ws.rs.QueryParam(UNIT) unitNo: String?): org.deku.leoz.service.entity.internal.v1.BagResponse

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/util/number-range")
    @io.swagger.annotations.ApiOperation("Get number range")
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun getNumberRange(): org.deku.leoz.service.entity.internal.v1.BagNumberRange

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/section/{${ID}}")
    @io.swagger.annotations.ApiOperation("Get all section depots")
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun getSectionDepots(
            @io.swagger.annotations.ApiParam(value = "Section", example = "1") @javax.ws.rs.PathParam(ID) section: Int?,
            @io.swagger.annotations.ApiParam(value = "Position", example = "1") @javax.ws.rs.QueryParam(POSITION) position: Int?): List<String>

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/section/{${ID}}/left")
    @io.swagger.annotations.ApiOperation("Get section depots left")
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun getSectionDepotsLeft(
            @io.swagger.annotations.ApiParam(value = "Section", example = "1") @javax.ws.rs.PathParam(ID) section: Int?,
            @io.swagger.annotations.ApiParam(value = "Position", example = "1") @javax.ws.rs.QueryParam(POSITION) position: Int?): org.deku.leoz.service.entity.internal.v1.SectionDepotsLeft

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/diff")
    @io.swagger.annotations.ApiOperation("Get diff list")
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun getDiff(): List<org.deku.leoz.service.entity.internal.v1.BagDiff>

    @sx.rs.PATCH
    @javax.ws.rs.Path("/{${ID}}/arrival")
    @io.swagger.annotations.ApiOperation("Line Arrival")
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun lineArrival(
            @io.swagger.annotations.ApiParam(value = "Scan id", example = "10055618") @javax.ws.rs.PathParam(ID) scanId: String?): org.deku.leoz.service.entity.internal.v1.BagResponse

    @sx.rs.PATCH
    @javax.ws.rs.Path("/{${ID}}/in")
    @io.swagger.annotations.ApiOperation("incoming bag")
    @io.swagger.annotations.ApiResponses(*arrayOf(
            io.swagger.annotations.ApiResponse(code = 400, message = "Bad request/parameter", response = Error::class))
    )
    fun bagIn(
            @io.swagger.annotations.ApiParam(value = "Bag unit number", example = "100720000004") @javax.ws.rs.PathParam(ID) unitNo: String?,
            @io.swagger.annotations.ApiParam(value = "Seal number", example = "900200000001") @javax.ws.rs.QueryParam(YELLOWSEAL) sealNo: String?): org.deku.leoz.service.entity.internal.v1.BagResponse

}
