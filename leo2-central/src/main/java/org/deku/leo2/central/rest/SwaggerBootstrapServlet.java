package org.deku.leo2.central.rest;

import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.jaxrs.config.ReflectiveJaxrsScanner;
import com.wordnik.swagger.models.Contact;
import com.wordnik.swagger.models.Info;
import com.wordnik.swagger.models.Swagger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.logging.Logger;

/**
 * Created by masc on 11.05.15.
 */
public class SwaggerBootstrapServlet extends HttpServlet {
    Logger mLog = Logger.getLogger(SwaggerBootstrapServlet.class.getName());

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        mLog.info("Bootstrapping swagger");

        Info info = new Info()
                .title("LEO2 webservice")
                .description("LEO2 public webservice API")
                .version("1.0.0")
                .contact(new Contact()
                        .email("masc@disappear.de"));

        ServletContext context = config.getServletContext();

        Swagger swagger = new Swagger().info(info);
        swagger.basePath("/leo2/rs/api");

//    swagger.securityDefinition("api_key", new ApiKeyAuthDefinition("api_key", In.HEADER));
//    swagger.securityDefinition("petstore_auth",
//      new OAuth2Definition()
//        .implicit("http://petstore.swagger.io/api/oauth/dialog")
//        .scope("read:pets", "read your pets")
//        .scope("write:pets", "modify pets in your account"));


        // Setting the scanner during bootstrap, no need for configuration servlet
        ReflectiveJaxrsScanner scanner = new ReflectiveJaxrsScanner();
        // Confusing method name, this can actually be a list of packages (comma separated)
        scanner.setResourcePackage("org.deku.leo2.rest.services.v1,org.deku.leo2.rest.services.internal.v1");
        ScannerFactory.setScanner(scanner);

        context.setAttribute("swagger", swagger);
    }
}
