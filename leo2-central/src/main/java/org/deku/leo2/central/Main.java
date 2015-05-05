package org.deku.leo2.central;

import org.deku.leo2.central.rest.WebserviceResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URI;

/**
 * Created by masc on 30.07.14.
 */
public class Main {
    static ApplicationContext mContext;

    //private static final URI BASE_URI = URI.create("http://localhost:8080/leo2/");
    private static final URI BASE_URI = URI.create("http://0.0.0.0:8080/leo2/");

    public static void main(String[] args) throws IOException {
        //Persistence.instance().initialize();

        // TODO: verify why partial webservice paths may return empty result instead of 404
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, new WebserviceResourceConfig());

        System.out.println("Enter to stop webservice");
        System.in.read();

        server.shutdownNow();
    }
}
