package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.config.RsyncConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.peer.RemotePeerSettings
import org.deku.leoz.node.services.FileSyncClientService
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import sx.ssh.SshTunnelProvider
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * Created by masc on 08-Mar-16.
 */
@Configuration
@Profile(App.PROFILE_CLIENT_NODE)
@Lazy(false)
open class FileSyncClientConfiguration {
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    private lateinit var remotePeerSettings: RemotePeerSettings

    @Inject
    private lateinit var sshTunnelProvider: SshTunnelProvider

    @Inject
    private lateinit var fileSyncClientService: FileSyncClientService

    @PostConstruct
    fun onInitialize() {
        this.fileSyncClientService.addTask(FileSyncClientService.Task(
                sourcePath = StorageConfiguration.instance.transferDataDirectory,
                rsyncModuleUri = RsyncConfiguration.createRsyncUri(
                        remotePeerSettings.host!!,
                        13002,
                        RsyncConfiguration.ModuleNames.TRANSFER),
                rsyncPassword = RsyncConfiguration.PASSWORD,
                sshTunnelProvider = this.sshTunnelProvider))

        this.fileSyncClientService.start()
    }

    @PreDestroy
    fun onDestroy() {
        this.fileSyncClientService.close()
    }
}