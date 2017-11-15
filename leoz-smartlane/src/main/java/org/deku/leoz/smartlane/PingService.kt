package org.deku.leoz.smartlane

import io.reactivex.Completable
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import org.slf4j.LoggerFactory
import sx.Disposable
import java.io.IOException
import java.net.URI

/**
 * Created by masc on 12.11.17.
 */
class PingService(
        val uri: URI) : Disposable {

    companion object {
        val NAMESPACE = "/dispatch"
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val socket by lazy {
        // For supporting partial path as namespace, a specific setup is required
        // The URI should not contain any path component, in turn it has to
        // be set explicitly via {IO.Options}.
        val uri = URI(
                this.uri.scheme,
                this.uri.host,
                "",
                this.uri.fragment)

        val opts = IO.Options().also {
            // When setting the path manually, `socket.io´ has to be added
            it.path = this.uri.resolve("socket.io").path
            // Enforce websocket as the primary transport
            it.transports = arrayOf(WebSocket.NAME)
        }

        Socket(Manager(uri, opts), NAMESPACE, opts)
    }

    fun ping(): Completable {
        return Completable.create {
            this.socket
                    .on(Socket.EVENT_CONNECT_ERROR, { args ->
                        it.onError(IOException("Connect error: ${args.joinToString(", ")}"))
                    })
                    .on(Socket.EVENT_ERROR, { args ->
                        it.onError(IOException("Error: ${args.joinToString(", ")}"))
                    })
                    .on(Socket.EVENT_CONNECT, { args ->
                        socket.emit("ping_it")
                    })
                    .on(Socket.EVENT_PONG, { args ->
                        it.onComplete()
                    })

            this.socket.connect()
        }
    }

    override fun close() {
        this.socket.close()
    }
}