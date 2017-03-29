package org.deku.leoz.node.rest.swagger

import io.swagger.annotations.ApiOperation
import io.swagger.jaxrs.listing.BaseApiListingResource
import org.apache.commons.lang3.StringUtils
import org.jboss.resteasy.spi.ResteasyUriInfo
import java.net.URI
import java.nio.file.Paths
import javax.inject.Inject

import javax.servlet.ServletConfig
import javax.servlet.ServletContext
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.*

/**
 * Customized base class for swagger listing resources.
 * Based on {@link io.swagger.jaxrs.listing.BaseApiListingResource}
 */
abstract class SwaggerListingResourceBase : BaseApiListingResource() {
    @Inject
    private lateinit var context: ServletContext

    @GET
    @Produces(MediaType.APPLICATION_JSON, "application/yaml")
    @ApiOperation(value = "The swagger definition in either JSON or YAML", hidden = true)
    @Path("/swagger.{type:json|yaml}")
    fun getListing(
            @Context app: Application,
            @Context sc: ServletConfig,
            @Context headers: HttpHeaders,
            @Context uriInfo: UriInfo,
            @PathParam("type") type: String): Response {

        // masc20170328.
        // As we're running all swagger instances under a single web application/dispatcher servlet
        // Have to mangle the paths so they reflect distinct base URIs, so base class
        // implementation can locate swagger instances setup by {@link SwaggerBootstrapServlet} by base URI.
        val filenameIndex = uriInfo.path.lastIndexOf('/')
        val path = uriInfo.path.substring(0, filenameIndex).trimStart('/')
        val filename = uriInfo.path.substring(filenameIndex)
        val newUriInfo = ResteasyUriInfo(uriInfo.baseUri.resolve(path), URI.create(filename))

        if (StringUtils.isNotBlank(type) && type.trim { it <= ' ' }.equals("yaml", ignoreCase = true)) {
            return getListingYamlResponse(app, context, sc, headers, newUriInfo)
        } else {
            return getListingJsonResponse(app, context, sc, headers, newUriInfo)
        }
    }
}
