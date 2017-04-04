package org.deku.leoz.node.rest.swagger

import io.swagger.jaxrs.config.ReflectiveJaxrsScanner
import io.swagger.jaxrs.config.SwaggerContextService
import io.swagger.models.Contact
import io.swagger.models.Info
import io.swagger.models.Swagger
import io.swagger.models.auth.ApiKeyAuthDefinition
import io.swagger.models.auth.In
import org.deku.leoz.config.RestConfiguration
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Named
import javax.servlet.ServletConfig
import javax.servlet.ServletContext
import javax.servlet.http.HttpServlet

/**
 * Public API documentation context/configuration
 * Created by masc on 20.05.15.
 */
@Named
class SwaggerBootstrapServlet : HttpServlet() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    // @Context
    // masc201505.
    // TODO: workaround for resteasy bug https://issues.jboss.org/browse/RESTEASY-828
    @Inject
    private lateinit var context: ServletContext

    override fun init(servletConfig: ServletConfig) {
        super.init(servletConfig)

        this.createSwagger(
                servletConfig = servletConfig,
                info = Info()
                        .title("Leoz public webservice")
                        .description("Leoz public webservice API")
                        .version("1.0.2")
                        .contact(Contact()
                                .email("it-service@derkurier.de")),
                mappingPrefix = RestConfiguration.MAPPING_PREFIX,
                packageName = org.deku.leoz.rest.service.v1.Package.name)

        this.createSwagger(
                servletConfig = servletConfig,
                info = Info()
                        .title("Leoz internal webservice")
                        .description("Leoz internal webservice API")
                        .version("1.0.1")
                        .contact(Contact()
                                .email("it-service@derkurier.de")),
                mappingPrefix = RestConfiguration.MAPPING_PREFIX,
                basePath = "/internal",
                packageName = org.deku.leoz.rest.service.internal.v1.Package.name)

        this.createSwagger(
                servletConfig = servletConfig,
                info = Info()
                        .title("Zalando LDN Webservice")
                        .description("Zalando LDN Webservice API")
                        .version("1.0.1")
                        .contact(Contact()
                                .email("philipp.prangenberg@gls-germany.com")),
                mappingPrefix = RestConfiguration.MAPPING_PREFIX,
                basePath = "/zalando",
                packageName = org.deku.leoz.rest.service.zalando.v1.Package.name)
    }


    /**
     * Creates a swagger and scanner instance and injects it into the servlet configuration.
     * This implementation is highly application specific, depending on how the web context is setup.
     */
    fun createSwagger(
            servletConfig: ServletConfig,
            info: Info,
            mappingPrefix: String,
            basePath: String = "",
            packageName: String) {

        val AUTH_APIKEY = RestConfiguration.AUTH_APIKEY_NAME
        val swagger = Swagger()
                .securityDefinition(AUTH_APIKEY, ApiKeyAuthDefinition(AUTH_APIKEY, In.HEADER))
                .info(info)
                // All our swagger instances are currently running under the same (web application) base URI.
                .basePath(mappingPrefix)

        //    swagger.securityDefinition("api_key", new ApiKeyAuthDefinition("api_key", In.HEADER));
        //    swagger.securityDefinition("petstore_auth",
        //      new OAuth2Definition()
        //        .implicit("http://petstore.swagger.io/api/oauth/dialog")
        //        .scope("read:pets", "read your pets")
        //        .scope("write:pets", "modify pets in your account"));

        // Setting the scanner during bootstrap, no need for configuration servlet
        val scanner = ReflectiveJaxrsScanner()
        // Confusing method name, this can actually be a list of packages (comma separated)
        scanner.resourcePackage = packageName

        SwaggerContextService()
                .withServletConfig(servletConfig)
                .withPathBasedConfig(true)
                // Construct aritical base URI, as that's the only way swagger supports multiple instances
                .withBasePath("${mappingPrefix.trimEnd('/')}/${basePath.trimStart('/')}")
                .withScanner(scanner)
                .initScanner()
                .updateSwagger(swagger)
    }
}
