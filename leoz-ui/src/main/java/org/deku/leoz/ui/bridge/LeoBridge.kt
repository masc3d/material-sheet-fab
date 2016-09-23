package org.deku.leoz.ui.bridge

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import org.deku.leoz.ui.bridge.services.MessageService
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.server.ResourceConfig
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.synchronized
import sx.Disposable
import java.io.IOException
import java.net.URI
import javax.ws.rs.client.ClientBuilder

/**
 * Created by masc on 26.09.14.
 */
class LeoBridge : Disposable, MessageService.Listener {
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

    companion object {
        private val HOST_URI = URI.create("http://localhost:37420/")
        private val CLIENT_URI = URI.create("http://localhost:37421/")
    }

    val ovMessageReceived by lazy { PublishSubject<Message>().synchronized() }

    private val httpServer by lazy {
        GrizzlyHttpServerFactory.createHttpServer(HOST_URI, WebserviceResourceConfig())
    }

    private val httpClient by lazy {
        // Setup mClient
        val c = ClientBuilder.newClient()
        c.register(JacksonJsonProvider::class.java)
        c.property(ClientProperties.CONNECT_TIMEOUT, 500)
        c
    }

    private val webTarget by lazy {
        this.httpClient.target(CLIENT_URI)
    }

    private val messageServiceClient by lazy {
        WebResourceFactory.newResource(IMessageService::class.java, this.webTarget)
    }

    /**
     * Start leo bridge
     * @throws IOException
     */
    @Synchronized @Throws(IOException::class)
    fun start() {
        this.httpServer.start()
    }

    /**
     * Stop leo bridge
     */
    @Synchronized fun stop() {
        this.httpServer.shutdownNow()
    }

    fun sendMessage(message: Message) {
        this.messageServiceClient.send(message)
    }

    fun sendValue(value: Any) {
        this.messageServiceClient.send(Message(value))
    }

    override fun onLeoBridgeServiceMessageReceived(message: Message) {
        this.ovMessageReceived.onNext(message)
    }

    override fun close() {
        this.stop()
    }
}
