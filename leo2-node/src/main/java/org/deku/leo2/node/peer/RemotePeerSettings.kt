package org.deku.leo2.node.peer

import org.deku.leo2.node.LocalStorage
import org.springframework.boot.context.properties.ConfigurationProperties
import javax.inject.Named

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

    public fun test() {
    }
}