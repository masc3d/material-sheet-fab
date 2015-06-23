package org.deku.leo2.node.rest.swagger;

import com.wordnik.swagger.config.Scanner;
import com.wordnik.swagger.jaxrs.config.ReflectiveJaxrsScanner;
import com.wordnik.swagger.models.Contact;
import com.wordnik.swagger.models.Info;
import com.wordnik.swagger.models.Swagger;
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
                .title("LEON internal webservice")
                .description("LEON internal webservice API")
                .version("1.0.0")
                .contact(new Contact()
                        .email("masc@disappear.de"));

        Swagger swagger = new Swagger().info(info);
        swagger.basePath("/rs/api");

//    swagger.securityDefinition("api_key", new ApiKeyAuthDefinition("api_key", In.HEADER));
//    swagger.securityDefinition("petstore_auth",
//      new OAuth2Definition()
//        .implicit("http://petstore.swagger.io/api/oauth/dialog")
//        .scope("read:pets", "read your pets")
//        .scope("write:pets", "modify pets in your account"));


        // Setting the scanner during bootstrap, no need for configuration servlet
        ReflectiveJaxrsScanner scanner = new ReflectiveJaxrsScanner();
        // Confusing method name, this can actually be a list of packages (comma separated)
        scanner.setResourcePackage("org.deku.leo2.rest.services.internal.v1");

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
