package org.deku.leoz.bridge.services

import org.deku.leoz.bridge.IMessageService
import org.deku.leoz.bridge.LeoBridge
import org.deku.leoz.bridge.MediaType
import org.deku.leoz.bridge.Message
import java.util.*
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces

/**
 * Created by masc on 17.09.14.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON_UTF8)
@Consumes(MediaType.APPLICATION_JSON_UTF8)
class MessageService : IMessageService {
    interface Listener {
        fun onLeoBridgeServiceMessageReceived(message: Message)
    }

    override fun send(message: Message) {
        val test = Date()
        val l = LeoBridge.instance()
        l.onLeoBridgeServiceMessageReceived(message)
    }
}
