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

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import java.io.IOException
import java.net.URI

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

    private var mHttpServer: HttpServer? = null
    private var mMessageServiceClient: IMessageService? = null

    internal var mListenerEventDispatcher: EventDispatcher<Listener> = ThreadSafeEventDispatcher()
    val listenerEventDelegate: EventDelegate<Listener>
        get() = mListenerEventDispatcher

    /**
     * Start leo bridge
     * @throws IOException
     */
    @Synchronized @Throws(IOException::class)
    fun start() {
        if (mHttpServer == null) {
            mHttpServer = GrizzlyHttpServerFactory.createHttpServer(HOST_URI, WebserviceResourceConfig())
            // Setup mClient
            val c = ClientBuilder.newClient()
            c.register(JacksonJsonProvider::class.java)
            c.property(ClientProperties.CONNECT_TIMEOUT, 500)

            // Client debug logging
            // c.register(new LoggingFilter(Logger.getLog(LeoBridge.class.getName()), true));

            mMessageServiceClient = WebResourceFactory.newResource(IMessageService::class.java, c.target(CLIENT_URI))
        }
        mHttpServer!!.start()
    }

    /**
     * Stop leo bridge
     */
    @Synchronized fun stop() {
        if (mHttpServer != null)
            mHttpServer!!.shutdownNow()
    }

    fun sendMessage(message: Message) {
        mMessageServiceClient!!.send(message)
    }

    fun sendValue(value: Any) {
        mMessageServiceClient!!.send(Message(value))
    }

    override fun onLeoBridgeServiceMessageReceived(message: Message) {
        mListenerEventDispatcher.emit { r -> r.onLeoBridgeMessageReceived(message) }
    }

    override fun close() {
        this.stop()
    }

    companion object {
        private var mInstance: LeoBridge? = null

        private val HOST_URI = URI.create("http://localhost:37420/")
        private val CLIENT_URI = URI.create("http://localhost:37421/")

        fun instance(): LeoBridge {
            if (mInstance == null) {
                synchronized (LeoBridge::class.java) {
                    mInstance = LeoBridge()
                }
            }
            return mInstance
        }
    }
}
