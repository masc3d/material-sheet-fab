package org.deku.leoz.node.peer

import org.springframework.boot.context.properties.ConfigurationProperties
import javax.inject.Named

/**
 * Configuration settings for connecting to a remote leoz peer/node
 */
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