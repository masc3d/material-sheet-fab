package org.deku.leoz.bridge.services;

import org.deku.leoz.bridge.IMessageService;
import org.deku.leoz.bridge.LeoBridge;
import org.deku.leoz.bridge.MediaType;
import org.deku.leoz.bridge.Message;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Date;

/**
 * Created by masc on 17.09.14.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON_UTF8)
@Consumes(MediaType.APPLICATION_JSON_UTF8)
public class MessageService implements IMessageService {
    public interface Listener {
        void onLeoBridgeServiceMessageReceived(Message message);
    }

    @Override
    public void send(Message message) {
        Date test = new Date();
        Listener l = (Listener) LeoBridge.instance();
        l.onLeoBridgeServiceMessageReceived(message);
    }
}
