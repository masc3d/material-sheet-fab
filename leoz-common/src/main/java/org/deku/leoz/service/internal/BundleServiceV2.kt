package org.deku.leoz.service.internal

import org.deku.leoz.service.entity.internal.update.UpdateInfo
import sx.rs.PATCH
import javax.ws.rs.*
import javax.ws.rs.core.*
import io.swagger.annotations.*

/**
 * Created by JT on 05.02.16.
 */
@Path("internal/v2/bundle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Bundle operations v2")
interface BundleServiceV2 {

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
    @Path("/{${NAME}}/info")
    @ApiOperation(value = "Retrieve bundle information by alias")
    fun info(
            @PathParam(NAME) @ApiParam(example = "leoz-boot", value = "Bundle name") bundleName: String,
            @QueryParam(ALIAS) versionAlias: String? = null,
            @QueryParam(KEY) nodeKey: String? = null
    ): UpdateInfo

    /**
     * Download bundle
     * @return JAX/RS response. The input stream can be retrieved using by calling .readEntity(InputStream::class.java)
     */
    @GET
    @Path("/{${NAME}}/{${VERSION}}/download")
    @ApiOperation(value = "Download bundle. Only supported for android bundles (for now)")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun download(
            @PathParam(NAME) @ApiParam(example = "leoz-mobile", value = "Bundle name") bundleName: String,
            @PathParam(VERSION) version: String
    ): Response

    @sx.rs.PATCH
    @Path("/clean")
    @ApiOperation(value = "Clean repository, removes all unused bundles")
    fun clean()
}