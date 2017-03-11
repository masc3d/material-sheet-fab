package org.deku.leoz.node.rest.swagger

import io.swagger.annotations.ApiKeyAuthDefinition
import io.swagger.config.Scanner
import io.swagger.jaxrs.config.ReflectiveJaxrsScanner
import io.swagger.models.Contact
import io.swagger.models.Info
import io.swagger.models.Swagger
import org.deku.leoz.config.RestConfiguration
import sx.LazyInstance

import java.util.concurrent.atomic.AtomicReference

/**
 * Public API documentation context/configuration
 * Created by masc on 20.05.15.
 */
object SwaggerContextPublic : SwaggerContext {

    override val swagger: Swagger
    override val scanner: Scanner

    init {
        val info = Info()
                .title("Leoz public webservice")
                .description("Leoz public webservice API")
                .version("1.0.2")
                .contact(Contact()
                        .email("wolfgang.drewelies@derkurier.de"))

        val AUTHNAME_APIKEY = "api-key"
        val swagger = Swagger()
                .securityDefinition(AUTHNAME_APIKEY, io.swagger.models.auth.ApiKeyAuthDefinition(AUTHNAME_APIKEY, io.swagger.models.auth.In.HEADER))
                .info(info)
        swagger.basePath(RestConfiguration.MAPPING_PREFIX)

        //    swagger.securityDefinition("api_key", new ApiKeyAuthDefinition("api_key", In.HEADER));
        //    swagger.securityDefinition("petstore_auth",
        //      new OAuth2Definition()
        //        .implicit("http://petstore.swagger.io/api/oauth/dialog")
        //        .scope("read:pets", "read your pets")
        //        .scope("write:pets", "modify pets in your account"));

        // Setting the scanner during bootstrap, no need for configuration servlet
        val scanner = ReflectiveJaxrsScanner()
        // Confusing method name, this can actually be a list of packages (comma separated)
        scanner.resourcePackage = org.deku.leoz.rest.service.v1.Package.name

        this.swagger = swagger
        this.scanner = scanner
    }
}
