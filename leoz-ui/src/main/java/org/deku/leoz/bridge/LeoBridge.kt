package org.deku.leoz.bridge

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import org.deku.leoz.bridge.services.MessageService
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.server.ResourceConfig
import sx.Disposable
import sx.event.EventDelegate
import sx.event.EventDispatcher
import sx.event.EventListener
import sx.event.ThreadSafeEventDispatcher
import java.io.IOException
import java.net.URI
import javax.ws.rs.client.ClientBuilder

/**
 * Created by masc on 26.09.14.
 */
class LeoBridge private constructor() : Disposable, MessageService.Listener {

    interface Listener : EventListener {
        fun onLeoBridgeMessageReceived(message: Message)
    }

    /**
     * Created by masc on 23.07.14.
     */
    private inner class WebserviceResourceConfig : ResourceConfig(JacksonFeature::class.java) {
        init {

            // Server debug logging
            // registerInstances(new LoggingFilter(Logger.getLog(LeoBridge.class.getName()), true));

            packages("org.deku.leoz.bridge.services")
        }
    }

    private var httpServer: HttpServer? = null
    private var messageServiceClient: IMessageService? = null

    internal var listenerEventDispatcher: EventDispatcher<Listener> = ThreadSafeEventDispatcher()
    val listenerEventDelegate: EventDelegate<Listener>
        get() = listenerEventDispatcher

    /**
     * Start leo bridge
     * @throws IOException
     */
    @Synchronized @Throws(IOException::class)
    fun start() {
        if (httpServer == null) {
            httpServer = GrizzlyHttpServerFactory.createHttpServer(HOST_URI, WebserviceResourceConfig())
            // Setup mClient
            val c = ClientBuilder.newClient()
            c.register(JacksonJsonProvider::class.java)
            c.property(ClientProperties.CONNECT_TIMEOUT, 500)

            // Client debug logging
            // c.register(new LoggingFilter(Logger.getLog(LeoBridge.class.getName()), true));

            messageServiceClient = WebResourceFactory.newResource(IMessageService::class.java, c.target(CLIENT_URI))
        }
        httpServer!!.start()
    }

    /**
     * Stop leo bridge
     */
    @Synchronized fun stop() {
        if (httpServer != null)
            httpServer!!.shutdownNow()
    }

    fun sendMessage(message: Message) {
        messageServiceClient!!.send(message)
    }

    fun sendValue(value: Any) {
        messageServiceClient!!.send(Message(value))
    }

    override fun onLeoBridgeServiceMessageReceived(message: Message) {
        listenerEventDispatcher.emit { r -> r.onLeoBridgeMessageReceived(message) }
    }

    override fun close() {
        this.stop()
    }

    companion object {
        private var instance: LeoBridge? = null

        private val HOST_URI = URI.create("http://localhost:37420/")
        private val CLIENT_URI = URI.create("http://localhost:37421/")

        fun instance(): LeoBridge {
            if (instance == null) {
                synchronized (LeoBridge::class.java) {
                    instance = LeoBridge()
                }
            }
            return instance!!
        }
    }
}
