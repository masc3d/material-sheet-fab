package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.config.RsyncConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.filesync.FileSyncTask
import org.deku.leoz.node.peer.RemotePeerSettings
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import sx.ssh.SshTunnelProvider
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * Created by n3 on 08-Mar-16.
 */
@Configuration
@Profile(App.PROFILE_CLIENT_NODE)
@Lazy(false)
open class FileSyncConfiguration {
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    private lateinit var remotePeerSettings: RemotePeerSettings

    @Inject
    private lateinit var sshTunnelProvider: SshTunnelProvider

    /** Scheduler  */
    private val executorService: ScheduledExecutorService

    init {
        this.executorService = Executors.newSingleThreadScheduledExecutor()
    }

    @PostConstruct
    fun onInitialize() {
        val fs = FileSyncTask(
                sourcePath = StorageConfiguration.instance.transferDataDirectory,
                rsyncModuleUri = RsyncConfiguration.createRsyncUri(
                        remotePeerSettings.host!!,
                        13002,
                        RsyncConfiguration.ModuleNames.TRANSFER),
                rsyncPassword = RsyncConfiguration.PASSWORD,
                sshTunnelProvider = this.sshTunnelProvider)

        log.info("Starting filesync scheduler")
        this.executorService.scheduleWithFixedDelay(fs,
                // Initial delay
                0,
                // Interval
                10, TimeUnit.SECONDS)
    }

    @PreDestroy
    fun onDestroy() {
        this.executorService.shutdown()
    }
}