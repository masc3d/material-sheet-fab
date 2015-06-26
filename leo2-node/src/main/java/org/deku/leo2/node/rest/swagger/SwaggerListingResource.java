package org.deku.leo2.node.rest.swagger;

import com.wordnik.swagger.config.FilterFactory;
import com.wordnik.swagger.config.Scanner;
import com.wordnik.swagger.core.filter.SpecFilter;
import com.wordnik.swagger.core.filter.SwaggerSpecFilter;
import com.wordnik.swagger.jaxrs.Reader;
import com.wordnik.swagger.jaxrs.config.JaxrsScanner;
import com.wordnik.swagger.jaxrs.listing.ApiListingResource;
import com.wordnik.swagger.jaxrs.listing.SwaggerSerializers;
import com.wordnik.swagger.models.Swagger;
import com.wordnik.swagger.util.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a modified version of com.wordnik.swagger.jaxrs.listing.ApiListingResource with support
 * for non-static context/configuration, instead of storing swagger context within global servlet context.
 *
 * IMPORTANT: this class may require compatibility maintenance when swagger is updated
 */
@Path("/")
public abstract class SwaggerListingResource {
    Logger LOGGER = LoggerFactory.getLogger(ApiListingResource.class);

    SwaggerContext mConfig;

    // @Context
    // masc201505.
    // TODO: workaround for resteasy bug https://issues.jboss.org/browse/RESTEASY-828
    @Inject
    ServletContext context;

    public SwaggerListingResource(SwaggerContext config) {
        mConfig = config;
    }

    protected synchronized Swagger scan (Application app, ServletConfig sc) {
        Swagger swagger = mConfig.getSwagger();
        Scanner scanner = mConfig.getScanner();
        LOGGER.debug("using scanner " + scanner);

        if(scanner != null) {
            SwaggerSerializers.setPrettyPrint(scanner.getPrettyPrint());
            swagger = mConfig.getSwagger();

            Set<Class<?>> classes = null;
            if (scanner instanceof JaxrsScanner) {
                JaxrsScanner jaxrsScanner = (JaxrsScanner)scanner;
                classes = jaxrsScanner.classesFromContext(app, sc);
            }
            else {
                classes = scanner.classes();
            }
            if(classes != null) {
                Reader reader = new Reader(swagger);
                swagger = reader.read(classes);
                if(scanner instanceof com.wordnik.swagger.config.SwaggerConfig)
                    swagger = ((com.wordnik.swagger.config.SwaggerConfig)scanner).configure(swagger);
                else {
                    com.wordnik.swagger.config.SwaggerConfig configurator = (com.wordnik.swagger.config.SwaggerConfig)context.getAttribute("reader");
                    if(configurator != null) {
                        LOGGER.debug("configuring swagger with " + configurator);
                        configurator.configure(swagger);
                    }
                    else
                        LOGGER.debug("no configurator");
                }
                context.setAttribute("swagger", swagger);
            }
        }
        return swagger;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/swagger.json")
    public Response getListingJson(
            @Context Application app,
            @Context ServletConfig sc,
            @Context HttpHeaders headers,
            @Context UriInfo uriInfo) {
        Swagger swagger = (Swagger) mConfig.getSwagger();
        if(swagger.getPaths() == null)
            swagger = scan(app, sc);
        if(swagger != null) {
            SwaggerSpecFilter filterImpl = FilterFactory.getFilter();
            if(filterImpl != null) {
                SpecFilter f = new SpecFilter();
                swagger = f.filter(swagger,
                        filterImpl,
                        getQueryParams(uriInfo.getQueryParameters()),
                        getCookies(headers),
                        getHeaders(headers));
            }
            return Response.ok().entity(swagger).build();
        }
        else
            return Response.status(404).build();
    }

    @GET
    @Produces("application/yaml")
    @Path("/swagger.yaml")
    public Response getListingYaml(
            @Context Application app,
            @Context ServletConfig sc,
            @Context HttpHeaders headers,
            @Context UriInfo uriInfo) {
        Swagger swagger = (Swagger) mConfig.getSwagger();
        if(swagger.getPaths() == null)
            swagger = scan(app, sc);
        try{
            if(swagger != null) {
                SwaggerSpecFilter filterImpl = FilterFactory.getFilter();
                LOGGER.debug("using filter " + filterImpl);
                if(filterImpl != null) {
                    SpecFilter f = new SpecFilter();
                    swagger = f.filter(swagger,
                            filterImpl,
                            getQueryParams(uriInfo.getQueryParameters()),
                            getCookies(headers),
                            getHeaders(headers));
                }

                String yaml = Yaml.mapper().writeValueAsString(swagger);
                String[] parts = yaml.split("\n");
                StringBuilder b = new StringBuilder();
                for(String part : parts) {
                    int pos = part.indexOf("!<");
                    int endPos = part.indexOf(">");
                    b.append(part);
                    b.append("\n");
                }
                return Response.ok().entity(b.toString()).type("text/plain").build();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(404).build();
    }

    protected Map<String, List<String>> getQueryParams(MultivaluedMap<String, String> params) {
        Map<String, List<String>> output = new HashMap<String, List<String>>();
        if(params != null) {
            for(String key: params.keySet()) {
                List<String> values = params.get(key);
                output.put(key, values);
            }
        }
        return output;
    }

    protected Map<String, String> getCookies(HttpHeaders headers) {
        Map<String, String> output = new HashMap<String, String>();
        if(headers != null) {
            for(String key: headers.getCookies().keySet()) {
                Cookie cookie = headers.getCookies().get(key);
                output.put(key, cookie.getValue());
            }
        }
        return output;
    }


    protected Map<String, List<String>> getHeaders(HttpHeaders headers) {
        Map<String, List<String>> output = new HashMap<String, List<String>>();
        if(headers != null) {
            for(String key: headers.getRequestHeaders().keySet()) {
                List<String> values = headers.getRequestHeaders().get(key);
                output.put(key, values);
            }
        }
        return output;
    }
}