package org.deku.leoz.node.rest.swagger

import io.swagger.config.Scanner
import io.swagger.jaxrs.config.ReflectiveJaxrsScanner
import io.swagger.models.Contact
import io.swagger.models.Info
import io.swagger.models.Swagger
import org.deku.leoz.config.RestConfiguration
import sx.LazyInstance

import java.util.concurrent.atomic.AtomicReference

/**
 * Internal API documentation context/configuration
 * Created by masc on 20.05.15.
 */
object SwaggerContextInternal : SwaggerContext {

    override val swagger: Swagger
    override val scanner: Scanner

    init {
        val info = Info()
                .title("Leoz internal webservice")
                .description("Leoz internal webservice API")
                .version("1.0.1")
                .contact(Contact()
                        .email("wolfgang.drewelies@derkurier.de"))

        val swagger = Swagger().info(info)
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
        scanner.resourcePackage = org.deku.leoz.rest.service.internal.v1.Package.name

        this.swagger = swagger
        this.scanner = scanner
    }
}
