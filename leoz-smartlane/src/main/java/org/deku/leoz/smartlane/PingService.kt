package org.deku.leoz.smartlane

import io.socket.client.IO
import io.socket.client.Socket
import org.slf4j.LoggerFactory
import sx.Disposable
import java.net.URI

/**
 * Created by masc on 12.11.17.
 */
class PingService(
        val baseUrl: URI) : Disposable {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val socket by lazy {
        val opts = IO.Options()
        opts.forceNew = false
        opts.reconnection = false

        IO.socket(baseUrl.resolve("/dispatch"))
    }

    fun ping() {
        this.socket
                .on(Socket.EVENT_CONNECT_ERROR, { args ->
                    log.error("Connect error: ${args.joinToString(", ")}")
                })
                .on(Socket.EVENT_ERROR, { args ->
                    log.error("Error: ${args.joinToString(",")}")
                })
                .on(Socket.EVENT_CONNECT, { args ->
                    log.trace("Connected, sending ping")
                    socket.emit("ping_it")
                })
                .on(Socket.EVENT_PONG, { args ->
                    log.trace("Received pong")
                })

        log.info("Connect")
        this.socket.connect()
    }

    override fun close() {
        this.socket.close()
    }
}