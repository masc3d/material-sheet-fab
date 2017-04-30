package org.deku.leoz.ui.bridge

import org.deku.leoz.service.entity.internal.v1.Station

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
