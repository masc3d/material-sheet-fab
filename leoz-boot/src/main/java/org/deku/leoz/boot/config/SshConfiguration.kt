package org.deku.leoz.boot.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import sx.ssh.SshTunnelProvider

/**
 * Created by n3 on 18-Nov-16.
 */
class SshConfiguration : org.deku.leoz.config.SshConfiguration() {
    companion object {
        val module = Kodein.Module {
            bind<SshConfiguration>() with singleton { SshConfiguration()  }
            bind<SshTunnelProvider>() with singleton {
                org.deku.leoz.config.SshConfiguration.tunnelProvider
            }
        }
    }
}