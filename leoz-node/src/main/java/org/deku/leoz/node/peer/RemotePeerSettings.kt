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
        /** Native broker port */
        var nativePort: Int? = null
        /** HTTP broker path */
        var httpPath: String? = null
    }

    inner class Rsync {
        /** Rsync port */
        var port: Int? = null
    }

    /** Remote peer/host (name) */
    var host: String? = null
    /** Remote host http port */
    var httpPort: Int? = null
    /** Remote host http root path */
    var httpPath: String? = null

    /** Broker settings */
    var broker: Broker = Broker()

    /** Rsync settings*/
    var rsync: Rsync = Rsync()
}