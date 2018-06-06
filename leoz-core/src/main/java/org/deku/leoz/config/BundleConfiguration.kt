package org.deku.leoz.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import sx.packager.BundleInstaller
import sx.packager.BundleRepository
import sx.rsync.Rsync
import sx.ssh.SshTunnelProvider

/**
 * Created by masc on 22.08.15.
 */
abstract class BundleConfiguration {
    companion object {
        @JvmStatic val stagingRepository: BundleRepository by lazy {
            this.createRepository(HostConfiguration.CENTRAL_HOST)
        }

        @JvmStatic fun createRepository(hostname: String): BundleRepository {
            return BundleRepository(
                    rsyncModuleUri = Rsync.URI("rsync://${RsyncConfiguration.USERNAME}@${hostname}:${RsyncConfiguration.DEFAULT_PORT}/${RsyncConfiguration.ModuleNames.BUNDLES}"),
                    rsyncPassword = RsyncConfiguration.PASSWORD,
                    sshTunnelProvider = SshConfiguration.tunnelProvider)
        }
    }
}