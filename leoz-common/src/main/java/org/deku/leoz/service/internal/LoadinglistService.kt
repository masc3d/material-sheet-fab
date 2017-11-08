package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.model.AdditionalInfo
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
        const val ID="loading-list"
    }

    @POST
    @Path("/")
    @ApiOperation(value = "Generate new loadinglistNo", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getNewLoadinglistNo(): Long

    @POST
    @Path("/bag")
    @ApiOperation(value = "Generate new loadinglistNo for bag", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getNewBagLoadinglistNo(): Long

    @GET
    @Path("/{$ID}")
    @ApiOperation(value = "Get parcels by loadinglist", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getParcels2ExportByLoadingList(
            @PathParam(ID) @ApiParam(value = "Loadinglist number", example = "300005", required = true) loadinglistNo: Long
    ): List<ParcelServiceV1.Order2Export>
}