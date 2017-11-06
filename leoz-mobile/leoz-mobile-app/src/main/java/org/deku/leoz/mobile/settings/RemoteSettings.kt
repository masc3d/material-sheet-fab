package org.deku.leoz.mobile.settings

/**
 * Remote settings block
 * Created by n3 on 10.05.17.
 */
@sx.ConfigurationMapPath("remote")
class RemoteSettings(private val map: sx.ConfigurationMap) {
    val host: String by map.value("")

    val hostIsProductive by lazy {
        this.host == "leoz.derkurier.de"
    }

    @sx.ConfigurationMapPath("remote.http")
    inner class Http {
        val port: Int by map.value(0)
        val ssl: Boolean by map.value(true)
    }

    @sx.ConfigurationMapPath("remote.broker")
    inner class Broker {
        val nativePort: Int by map.value(0)
    }

    @sx.ConfigurationMapPath("remote.ntp")
    inner class Ntp {
        val host: String by map.value("time.gls-group.eu")
    }

    val http = Http()
    val broker = Broker()
    val ntp = Ntp()

    override fun toString(): String {
        return "Host: $host\n" +
                "Port: ${Http().port}\n" +
                "SSL: ${Http().ssl}\n" +
                "NativePort (Broker): ${Broker().nativePort}\n" +
                "NTP-Host: ${ntp.host}"
    }
}