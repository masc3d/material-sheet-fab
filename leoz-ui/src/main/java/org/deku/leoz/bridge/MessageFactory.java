package org.deku.leoz.bridge;

import org.deku.leoz.rest.entities.internal.v1.Station;

/**
 * Factory for leobridge messages
 * Created by masc on 21.10.14.
 */
public final class MessageFactory {
    public static Message createViewDepotMessage(Station station) {
        Message m = new Message();
        m.put("view", "depot");
        m.put("id", station.getDepotNr());
        return m;
    }
}
