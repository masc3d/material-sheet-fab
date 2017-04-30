package org.deku.leoz.service.internal

import io.swagger.annotations.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by JT on 05.02.16.
 */
@Path("internal/v1/bundle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Bundle operations v1")
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
    ): org.deku.leoz.service.entity.internal.update.UpdateInfo

    /**
     * Download bundle
     * @return JAX/RS response. The input stream can be retrieved using by calling .readEntity(InputStream::class.java)
     */
    @GET
    @Path("/download/{${BUNDLE}}/{${VERSION}}")
    @ApiOperation(value = "Download bundle. Only supported for android bundles (for now)")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun download(
            @PathParam(BUNDLE) @ApiParam(example = "leoz-mobile", value = "Bundle name") bundleName: String,
            @PathParam(VERSION) version: String
    ): Response

    @GET
    @Path("/clean-repository")
    @ApiOperation(value = "Clean repository, removes all unused bundles")
    fun cleanRepository()
}