package org.deku.leo2.central;

import org.deku.leo2.central.rest.WebserviceResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import sx.Disposable;

import java.io.IOException;
import java.net.URI;

/**
 * Created by masc on 30.07.14.
 */
public class Main implements Disposable {
    private volatile static Main mInstance = null;

    public static Main instance() {
        if (mInstance == null) {
            synchronized (Main.class) {
                mInstance = new Main();
            }
        }
        return mInstance;
    }

    /**
     * Spring application context
     */
    ApplicationContext mContext;

    public ApplicationContext getContext() {
        return mContext;
    }

    private Main() {
        // Spring application context
        mContext = new AnnotationConfigApplicationContext(Main.class.getPackage().getName());
    }

    public static void main(String[] args) throws IOException {
        //final URI BASE_URI = URI.create("http://localhost:8080/leo2/");
        final URI BASE_URI = URI.create("http://0.0.0.0:8080/leo2/");

        ResourceConfig config = new WebserviceResourceConfig();
        // Let spring/jersey bridge know the context
        config.property("contextConfig", instance().getContext());

        // TODO: verify why partial webservice paths may return empty result instead of 404
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, config);

        System.out.println("Enter to stop webservice");
        System.in.read();

        server.shutdownNow();
    }

    @Override
    public void dispose() {
    }
}
