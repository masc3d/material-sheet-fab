package org.deku.leoz.node.config

import org.deku.leoz.config.JmsChannels
import org.deku.leoz.config.RsyncConfiguration
import org.deku.leoz.node.Application
import org.deku.leoz.node.LifecycleController
import org.deku.leoz.node.Storage
import org.deku.leoz.node.service.internal.filesync.FileSyncClientService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import sx.rsync.Rsync
import sx.ssh.SshTunnelProvider
import java.util.concurrent.ScheduledExecutorService
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * Created by masc on 08-Mar-16.
 */
@Configuration
@Profile(Application.PROFILE_CLIENT_NODE)
@Lazy(false)
open class FileSyncClientConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var application: Application
    @Inject
    private lateinit var executorService: ScheduledExecutorService
    @Inject
    private lateinit var remotePeerSettings: RemotePeerConfiguration
    @Inject
    private lateinit var sshTunnelProvider: SshTunnelProvider
    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration
    @Inject
    private lateinit var lifecycleController: LifecycleController
    @Inject
    private lateinit var storage: Storage

    @get:Bean
    open val fileSyncClientService: FileSyncClientService
        get() = FileSyncClientService(
                executorService = this.executorService,
                baseDirectory = storage.transferDirectory,
                identity = this.application.identity,
                rsyncEndpoint = Rsync.Endpoint(
                        moduleUri = RsyncConfiguration.createRsyncUri(
                                remotePeerSettings.host!!,
                                remotePeerSettings.rsync.port!!,
                                RsyncConfiguration.ModuleNames.TRANSFER),
                        password = RsyncConfiguration.PASSWORD,
                        sshTunnelProvider = this.sshTunnelProvider),
                centralEndpointSupplier = { JmsChannels.central.main.kryo })

    @PostConstruct
    fun onInitialize() {
        this.lifecycleController.registerNetworkDependant(this.fileSyncClientService)

        messageListenerConfiguration.nodeQueueListener.addDelegate(
                this.fileSyncClientService
        )
    }

    @PreDestroy
    fun onDestroy() {
        this.fileSyncClientService.close()
    }
}