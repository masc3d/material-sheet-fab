package org.deku.leo2.node.peer

import org.deku.leo2.node.LocalStorage
import org.springframework.boot.context.properties.ConfigurationProperties
import javax.inject.Named

@Named
@ConfigurationProperties(prefix = "remote")
class RemotePeerSettings {
    inner class Broker {

        var nativePort: Int? = null
        var httpPath: String? = null
    }

    var host: String? = null
    var httpPort: Int? = null
    var httpPath: String? = null
    var broker: Broker = Broker()
}