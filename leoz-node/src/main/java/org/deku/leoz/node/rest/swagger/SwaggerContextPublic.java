package org.deku.leoz.node.rest.swagger;

import io.swagger.config.Scanner;
import io.swagger.jaxrs.config.ReflectiveJaxrsScanner;
import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import sx.LazyInstance;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Public API documentation context/configuration
 * Created by masc on 20.05.15.
 */
public class SwaggerContextPublic implements SwaggerContext {
    private static final LazyInstance<AtomicReference<SwaggerContext>> mInstance = new LazyInstance<>(
            () -> new AtomicReference<>(
                    new SwaggerContextPublic()));

    Swagger mSwagger;
    Scanner mScanner;

    public static SwaggerContext instance() {
        return mInstance.get().get();
    }

    public SwaggerContextPublic() {
        Info info = new Info()
                .title("Leoz public webservice")
                .description("Leoz public webservice API")
                .version("1.0.2")
                .contact(new Contact()
                        .email("wolfgang.drewelies@derkurier.de"))
                ;

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
        scanner.setResourcePackage("org.deku.leoz.rest.services.v1");

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
