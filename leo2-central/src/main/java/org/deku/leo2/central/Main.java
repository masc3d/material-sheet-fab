package org.deku.leo2.central;

import org.deku.leo2.central.rest.WebserviceResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;
import java.net.URI;

/**
 * Created by masc on 30.07.14.
 */
public class Main {
    private static final URI BASE_URI = URI.create("http://leo2ws:8080/leo2/");

    public static void main(String[] args) throws IOException {
        Persistence.instance().initialize();

        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, new WebserviceResourceConfig());

        System.out.println("Enter to stop webservice");
        System.in.read();

        server.shutdownNow();
    }
}
