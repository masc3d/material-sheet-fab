package org.deku.leoz.service.internal.v2

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import sx.rs.PATCH
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by JT on 05.02.16.
 */
@javax.ws.rs.Path("internal/v2/bundle")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(value = "Bundle operations v2")
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
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{${NAME}}/info")
    @io.swagger.annotations.ApiOperation(value = "Retrieve bundle information by alias")
    fun info(
            @javax.ws.rs.PathParam(NAME) @io.swagger.annotations.ApiParam(example = "leoz-boot", value = "Bundle name") bundleName: String,
            @javax.ws.rs.QueryParam(ALIAS) versionAlias: String? = null,
            @javax.ws.rs.QueryParam(KEY) nodeKey: String? = null
    ): org.deku.leoz.service.entity.internal.v1.update.UpdateInfo

    /**
     * Download bundle
     * @return JAX/RS response. The input stream can be retrieved using by calling .readEntity(InputStream::class.java)
     */
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{${NAME}}/{${VERSION}}/download")
    @io.swagger.annotations.ApiOperation(value = "Download bundle. Only supported for android bundles (for now)")
    @javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM)
    fun download(
            @javax.ws.rs.PathParam(NAME) @io.swagger.annotations.ApiParam(example = "leoz-mobile", value = "Bundle name") bundleName: String,
            @javax.ws.rs.PathParam(VERSION) version: String
    ): javax.ws.rs.core.Response

    @sx.rs.PATCH
    @javax.ws.rs.Path("/clean")
    @io.swagger.annotations.ApiOperation(value = "Clean repository, removes all unused bundles")
    fun clean()
}