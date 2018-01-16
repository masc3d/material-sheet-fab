package org.deku.leoz.node.rest.swagger

import io.swagger.annotations.ApiOperation
import io.swagger.jaxrs.listing.BaseApiListingResource
import io.swagger.models.Swagger
import io.swagger.models.parameters.AbstractSerializableParameter
import org.apache.commons.lang3.StringUtils
import org.jboss.resteasy.specimpl.ResteasyUriBuilder
import org.jboss.resteasy.spi.ResteasyUriInfo
import org.slf4j.LoggerFactory
import sx.log.slf4j.trace
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
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var context: ServletContext

    @GET
    @Produces(MediaType.APPLICATION_JSON, "application/yaml")
    @ApiOperation(value = "The swagger definition in either JSON or YAML", hidden = true)
    @Path("/openapi-2.{type:json|yaml}")
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

        val path = uriInfo.pathSegments
                .take(uriInfo.pathSegments.count() - 1)
                .joinToString("/")

        val filename = uriInfo.pathSegments.last().path

        val newUriInfo = ResteasyUriInfo(
                // Path must be part of base URI for swagger spec distinction
                uriInfo.baseUri.resolve(path),
                ResteasyUriBuilder()
                        .uri(filename)
                        .replaceQuery(uriInfo.requestUri.query)
                        .build()
        )

        if (StringUtils.isNotBlank(type) && type.trim { it <= ' ' }.equals("yaml", ignoreCase = true)) {
            return getListingYamlResponse(app, context, sc, headers, newUriInfo)
        } else {
            return getListingJsonResponse(app, context, sc, headers, newUriInfo)
        }
    }

    override fun process(app: Application, servletContext: ServletContext, sc: ServletConfig, headers: HttpHeaders, uriInfo: UriInfo): Swagger {
        return super.process(app, servletContext, sc, headers, uriInfo).also { swagger ->
            // Support for `postman` query parameter, eg. placeholders (https://www.getpostman.com)
            swagger.host = when {
                uriInfo.queryParameters.containsKey("postman") -> "{{host}}"
                else -> ""
            }

            swagger.paths.values.forEach {
                it.operations?.forEach {
                    it.parameters?.forEach {
                        if (it is AbstractSerializableParameter<*>) {
                            val example = it.getExample()
                            val default = it.getDefaultValue()

                            // Set default values from example when default is not explicitly supplied.
                            // TODO: This is a workaround for pending swagger pull request https://github.com/swagger-api/swagger-ui/pull/3538
                            if (example != null && default == null) {
                                it.setDefaultValue(example.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}

