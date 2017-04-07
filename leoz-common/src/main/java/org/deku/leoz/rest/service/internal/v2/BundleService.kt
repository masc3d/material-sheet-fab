package org.deku.leoz.rest.service.internal.v2

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import sx.rs.PATCH
import java.io.File
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by JT on 05.02.16.
 */
@Path("internal/v2/bundle")
@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@Api(value = "Bundle operations v2")
interface BundleService {

    companion object {
        const val NAME = "name"
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
    @Path("/{$NAME}/info")
    @ApiOperation(value = "Retrieve bundle information by alias")
    fun info(
            @PathParam(NAME) @ApiParam(example = "leoz-boot", value = "Bundle name") bundleName: String,
            @QueryParam(ALIAS) versionAlias: String? = null,
            @QueryParam(KEY) nodeKey: String? = null
    ): org.deku.leoz.service.update.UpdateInfo

    /**
     * Download bundle
     * @return JAX/RS response. The input stream can be retrieved using by calling .readEntity(InputStream::class.java)
     */
    @GET
    @Path("/{$NAME}/{${VERSION}}/download")
    @ApiOperation(value = "Download bundle. Only supported for android bundles (for now)")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun download(
            @PathParam(NAME) @ApiParam(example = "leoz-mobile", value = "Bundle name") bundleName: String,
            @PathParam(VERSION) version: String
    ): Response

    @PATCH
    @Path("/clean-repository")
    @ApiOperation(value = "Clean repository, removes all unused bundles")
    fun cleanRepository()
}