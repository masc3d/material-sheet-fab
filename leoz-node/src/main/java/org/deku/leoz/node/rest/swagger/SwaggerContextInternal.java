package org.deku.leoz.node.rest.swagger;

import io.swagger.config.Scanner;
import io.swagger.jaxrs.config.ReflectiveJaxrsScanner;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import org.deku.leoz.config.RestClientConfiguration;
import org.deku.leoz.config.RestConfiguration;
import sx.LazyInstance;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Internal API documentation context/configuration
 * Created by masc on 20.05.15.
 */
public class SwaggerContextInternal implements SwaggerContext {
    private static final LazyInstance<AtomicReference<SwaggerContext>> mInstance = new LazyInstance<>(
                    () -> new AtomicReference<>(
                            new SwaggerContextInternal()));

    Swagger mSwagger;
    Scanner mScanner;

    public static SwaggerContext instance() {
        return mInstance.get().get();
    }

    public SwaggerContextInternal() {
        Info info = new Info()
                .title("Leoz internal webservice")
                .description("Leoz internal webservice API")
                .version("1.0.1")
                .contact(new Contact()
                        .email("wolfgang.drewelies@derkurier.de"));

        Swagger swagger = new Swagger().info(info);
        swagger.basePath(RestConfiguration.getMAPPING_PREFIX());

//    swagger.securityDefinition("api_key", new ApiKeyAuthDefinition("api_key", In.HEADER));
//    swagger.securityDefinition("petstore_auth",
//      new OAuth2Definition()
//        .implicit("http://petstore.swagger.io/api/oauth/dialog")
//        .scope("read:pets", "read your pets")
//        .scope("write:pets", "modify pets in your account"));


        // Setting the scanner during bootstrap, no need for configuration servlet
        ReflectiveJaxrsScanner scanner = new ReflectiveJaxrsScanner();
        // Confusing method name, this can actually be a list of packages (comma separated)
        scanner.setResourcePackage(org.deku.leoz.rest.service.internal.v1.Package.getName());

        mSwagger = swagger;
        mScanner = scanner;
    }

    @Override
    public Swagger getSwagger() {
        return mSwagger;
    }

    @Override
    public Scanner getScanner() {
        return mScanner;
    }
}
