package org.deku.leo2.bridge;

import org.deku.leo2.rest.entities.internal.v1.Depot;

/**
 * Factory for leobridge messages
 * Created by masc on 21.10.14.
 */
public final class MessageFactory {
    public static Message createViewDepotMessage(Depot depot) {
        Message m = new Message();
        m.put("view", "depot");
        m.put("id", depot.getDepotNr());
        return m;
    }
}
