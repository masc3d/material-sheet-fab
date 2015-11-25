package org.deku.leoz.config

import sx.ssh.SshHost
import sx.ssh.SshTunnelProvider

/**
 * Created by masc on 17.11.15.
 */
object SshConfiguration {
    val USERNAME = "leoz"
    val PASSWORD = "MhWLzHv0Z0E9hy8jAiBMRoO65qDBro2JH1csNlwGI3hXPY8P8NOY3NeRDHrApme8"

    val tunnelProvider = SshTunnelProvider(13100..13200,
            SshHost(hostname = "10.211.55.7",
                    sshPort = 13003,
                    sshUsername = USERNAME,
                    sshPassword = PASSWORD))
}