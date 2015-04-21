package org.deku.leo2.bridge;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.deku.leo2.bridge.services.MessageService;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import sx.Disposable;
import sx.util.EventDelegate;
import sx.util.EventDispatcher;
import sx.util.EventListener;
import sx.util.ThreadSafeEventDispatcher;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * Created by masc on 26.09.14.
 */
public class LeoBridge implements Disposable, MessageService.Listener {
    private static LeoBridge mInstance;

    public interface Listener extends EventListener {
        void onLeoBridgeMessageReceived(Message message);
    }

    /**
     * Created by masc on 23.07.14.
     */
    private class WebserviceResourceConfig extends ResourceConfig {
        public WebserviceResourceConfig()
        {
            super(JacksonFeature.class);

            // Server debug logging
            // registerInstances(new LoggingFilter(Logger.getLogger(LeoBridge.class.getName()), true));

            packages("org.deku.leo2.bridge.services");
        }
    }

    private static final URI HOST_URI = URI.create("http://localhost:37420/");
    private static final URI CLIENT_URI = URI.create("http://localhost:37421/");
    private HttpServer mHttpServer;
    private IMessageService mMessageServiceClient;

    EventDispatcher<Listener> mListenerEventDispatcher = new ThreadSafeEventDispatcher();
    public EventDelegate<Listener> getListenerEventDelegate() {
        return mListenerEventDispatcher;
    }

    public static LeoBridge instance() {
        if (mInstance == null) {
            synchronized (LeoBridge.class) {
                mInstance = new LeoBridge();
            }
        }
        return mInstance;
    }

    private LeoBridge() {

    }

    /**
     * Start leo bridge
     * @throws IOException
     */
    public void start() throws IOException {
        if (mHttpServer == null) {
            mHttpServer = GrizzlyHttpServerFactory.createHttpServer(HOST_URI, new WebserviceResourceConfig());
            // Setup mClient
            Client c = ClientBuilder.newClient();
            c.register(JacksonJsonProvider.class);
            c.property(ClientProperties.CONNECT_TIMEOUT, 500);

            // Client debug logging
            // c.register(new LoggingFilter(Logger.getLogger(LeoBridge.class.getName()), true));

            mMessageServiceClient = WebResourceFactory.newResource(IMessageService.class, c.target(CLIENT_URI));
        }
        mHttpServer.start();
    }

    /**
     * Stop leo bridge
     */
    public void stop() {
        if (mHttpServer != null)
            mHttpServer.shutdownNow();
    }

    public void sendMessage(Message message) {
        mMessageServiceClient.send(message);
    }

    public void sendValue(Object value) {
        mMessageServiceClient.send(new Message(value));
    }

    @Override
    public void onLeoBridgeServiceMessageReceived(Message message) {
        mListenerEventDispatcher.emit(r -> r.onLeoBridgeMessageReceived(message));
    }

    @Override
    public void dispose() {
        this.stop();
    }
}
