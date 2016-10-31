package org.deku.leoz.node.config

import org.deku.leoz.config.RsyncConfiguration
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.LifecycleController
import org.deku.leoz.node.messaging.entities.FileSyncMessage
import org.deku.leoz.node.peer.RemotePeerSettings
import org.deku.leoz.node.services.FileSyncClientService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import sx.jms.Channel
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
@Profile(App.PROFILE_CLIENT_NODE)
@Lazy(false)
open class FileSyncClientConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @Inject
    private lateinit var remotePeerSettings: RemotePeerSettings

    @Inject
    private lateinit var sshTunnelProvider: SshTunnelProvider

    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration

    @Inject
    private lateinit var lifecycleController: LifecycleController

    @Bean
    open fun fileSyncClientService(): FileSyncClientService {
        return FileSyncClientService(
                executorService = this.executorService,
                baseDirectory = StorageConfiguration.instance.transferDirectory,
                identity = App.instance.identity,
                rsyncEndpoint = Rsync.Endpoint(
                        moduleUri = RsyncConfiguration.createRsyncUri(
                                remotePeerSettings.host!!,
                                remotePeerSettings.rsync.port!!,
                                RsyncConfiguration.ModuleNames.TRANSFER),
                        password = RsyncConfiguration.PASSWORD,
                        sshTunnelProvider = this.sshTunnelProvider),
                centralChannelSupplier = { Channel(ActiveMQConfiguration.instance.centralQueue) })
    }
    private val fileSyncClientService by lazy { this.fileSyncClientService() }

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