package org.deku.leoz.config

import sx.ssh.SshHost
import sx.ssh.SshTunnelProvider

/**
 * Created by masc on 17.11.15.
 */
abstract class SshConfiguration {
    companion object {
        val USERNAME = "leoz"
        val PASSWORD = "MhWLzHv0Z0E9hy8jAiBMRoO65qDBro2JH1csNlwGI3hXPY8P8NOY3NeRDHrApme8"

        /**
         * Static tunnel provider
         */
        @JvmStatic val tunnelProvider by lazy {
            SshTunnelProvider(
                    localPortRange = IntRange(13300, 13400),

                    sshHosts = *arrayOf(SshHost(
                            hostname = "",
                            port = 13003,
                            username = USERNAME,
                            password = PASSWORD))
            )
        }
    }
}