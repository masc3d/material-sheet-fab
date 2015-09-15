package org.deku.leoz.node.peer

import org.springframework.boot.context.properties.ConfigurationProperties
import javax.inject.Named

/**
 * Configuration settings for connecting to a remote leoz peer/node
 */
@Named
@ConfigurationProperties(prefix = "remote")
public class RemotePeerSettings {
    public inner class Broker {

        public var nativePort: Int? = null
        public var httpPath: String? = null
    }

    public var host: String? = null
    public var httpPort: Int? = null
    public var httpPath: String? = null
    public var broker: Broker = Broker()
}