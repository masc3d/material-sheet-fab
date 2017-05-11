package org.deku.leoz.mobile.model

import sx.ConfigurationMap
import sx.ConfigurationMapPath

/**
 * Remote settings block
 * Created by n3 on 10.05.17.
 */
@ConfigurationMapPath("remote")
class RemoteSettings(private val map: ConfigurationMap) {
    val host: String by map.value("")

    @ConfigurationMapPath("remote.http")
    inner class Http {
        val port: Int by map.value(0)
        val ssl: Boolean by map.value(true)
    }

    @ConfigurationMapPath("remote.broker")
    inner class Broker {
        val nativePort: Int by map.value(0)
    }

    val http = Http()
    val broker = Broker()
}