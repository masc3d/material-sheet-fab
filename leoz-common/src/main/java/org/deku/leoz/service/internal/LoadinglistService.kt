package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.model.AdditionalInfo
import org.deku.leoz.model.DekuUnitNumber
import org.deku.leoz.model.LoadinglistType
import org.deku.leoz.service.internal.entity.Address
import sx.io.serialization.Serializable
import sx.rs.PATCH
import sx.rs.auth.ApiKey
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType


@Path("internal/v1/loadinglist")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Loadinglist service")
@ApiKey(false)
interface LoadinglistService {
    companion object {
        const val ID = "loading-list"
    }

    @Serializable(0x2e5b98b7a7694f)
    data class Loadinglist(val loadinglistNo: Long, val orders: List<ExportService.Order> = listOf()) {
        constructor(loadinglistlabel: String, orders: List<ExportService.Order>) : this(DekuUnitNumber.parseLabel(loadinglistlabel).value.value.toLong(), orders) {}

        val loadinglistType by lazy {
            if (this.loadinglistNo < 100000)
                LoadinglistType.BAG
            else LoadinglistType.NORMAL
        }
        val label by lazy {
            DekuUnitNumber.parse(this.loadinglistNo.toString().padStart(11, '0')).value.label
        }
    }


    @POST
    @Path("/")
    @ApiOperation(value = "Create new loadinglist", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getNewLoadinglistNo(): Loadinglist

    @POST
    @Path("/bag")
    @ApiOperation(value = "Create new loadinglist for bag", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getNewBagLoadinglistNo(): Loadinglist

    @GET
    @Path("/{$ID}")
    @ApiOperation(value = "Get loadinglist", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getParcels2ExportByLoadingList(
            @PathParam(ID) @ApiParam(value = "Loadinglist number", example = "300005", required = true) loadinglistNo: String
    ): Loadinglist?
}