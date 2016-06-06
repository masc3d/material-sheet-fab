package org.deku.leoz.bridge

import org.deku.leoz.rest.entities.internal.v1.Station

/**
 * Factory for leobridge messages
 * Created by masc on 21.10.14.
 */
object MessageFactory {
    fun createViewDepotMessage(station: Station): Message {
        val m = Message()
        m.put("view", "depot")
        m.put("id", station.depotNr!!)
        return m
    }
}
