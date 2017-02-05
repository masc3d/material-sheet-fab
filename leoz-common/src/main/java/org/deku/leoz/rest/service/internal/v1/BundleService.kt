package org.deku.leoz.rest.service.internal.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import java.io.File
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by JT on 05.02.16.
 */
@Path("internal/v1/bundle")
@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@Api(value = "Bundle operations")
interface BundleService {

    companion object {
        const val BUNDLE = "bundle"
        const val ALIAS = "alias"
        const val KEY = "key"
        const val VERSION = "version"
    }

    /**
     * Get bundle information
     * @param bundleName Name of bundle to request update info for
     * @param versionAlias Version alias
     */
    @GET
    @Path("/info/{${BUNDLE}}")
    @ApiOperation(value = "Retrieve bundle information by alias")
    fun info(
            @PathParam(BUNDLE) @ApiParam(example = "leoz-boot", value = "Bundle name") bundleName: String,
            @QueryParam(ALIAS) versionAlias: String? = null,
            @QueryParam(KEY) nodeKey: String? = null
    ): org.deku.leoz.service.update.UpdateInfo

    @GET
    @Path("/download/{${BUNDLE}}/{${VERSION}}")
    @ApiOperation(value = "Download bundle. Only supported for android bundles (for now)")
    //@Produces("application/vnd.android.package-archive")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun download(
            @PathParam(BUNDLE) @ApiParam(example = "leoz-mobile", value = "Bundle name") bundleName: String,
            @PathParam(VERSION) version: String
    ): Response
}