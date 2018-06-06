package org.deku.leoz.ui.bridge.services

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import org.deku.leoz.ui.bridge.IMessageService
import org.deku.leoz.ui.bridge.LeoBridge
import org.deku.leoz.ui.bridge.MediaType
import org.deku.leoz.ui.bridge.Message
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
    /** Leo bridge singgleton instance */
    val leoBridge: LeoBridge = Kodein.global.instance()

    interface Listener {
        fun onLeoBridgeServiceMessageReceived(message: Message)
    }

    override fun send(message: Message) {
        this.leoBridge.onLeoBridgeServiceMessageReceived(message)
    }
}
