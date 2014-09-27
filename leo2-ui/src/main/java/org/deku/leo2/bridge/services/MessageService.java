package org.deku.leo2.bridge.services;

import org.deku.leo2.bridge.IMessageService;
import org.deku.leo2.bridge.LeoBridge;
import org.deku.leo2.bridge.MediaType;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by masc on 17.09.14.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON_UTF8)
@Consumes(MediaType.APPLICATION_JSON_UTF8)
public class MessageService implements IMessageService {
    public interface Listener {
        void onLeoBridgeServiceMessageReceived(String message);
    }

    @Override
    public boolean send(String message) {
        Listener l = (Listener) LeoBridge.instance();
        l.onLeoBridgeServiceMessageReceived(message);
        return true;
    }
}
