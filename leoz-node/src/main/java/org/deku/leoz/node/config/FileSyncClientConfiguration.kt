package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.config.RsyncConfiguration
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.messaging.entities.FileSyncMessage
import org.deku.leoz.node.peer.RemotePeerSettings
import org.deku.leoz.node.services.FileSyncClientService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import sx.jms.Channel
import sx.jms.embedded.Broker
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
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @Inject
    private lateinit var remotePeerSettings: RemotePeerSettings

    @Inject
    private lateinit var sshTunnelProvider: SshTunnelProvider

    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration

    @Bean
    open fun fileSyncClientService(): FileSyncClientService {
        return FileSyncClientService(
                executorService = this.executorService,
                baseDirectory = StorageConfiguration.instance.transferDirectory,
                identity = App.instance.identity,
                rsyncEndpoint = Rsync.Endpoint(
                        moduleUri = RsyncConfiguration.createRsyncUri(
                                remotePeerSettings.hostname!!,
                                remotePeerSettings.rsync.port!!,
                                RsyncConfiguration.ModuleNames.TRANSFER),
                        password = RsyncConfiguration.PASSWORD,
                        sshTunnelProvider = this.sshTunnelProvider),
                centralChannelSupplier = { Channel(ActiveMQConfiguration.instance.centralQueue) })
    }
    private val fileSyncClientService by lazy { this.fileSyncClientService() }

    /** Broker event listener */
    private val brokerEventListener = object : Broker.DefaultEventListener() {
        override fun onConnectedToBrokerNetwork() {
            fileSyncClientService.restart()
        }

        override fun onDisconnectedFromBrokerNetwork() {
            fileSyncClientService.stop()
        }

        override fun onStop() {
            fileSyncClientService.stop()
        }
    }
    @PostConstruct
    fun onInitialize() {
        ActiveMQConfiguration.instance.broker.delegate.add(this.brokerEventListener)

        messageListenerConfiguration.nodeQueueListener.addDelegate(
                FileSyncMessage::class.java,
                this.fileSyncClientService
        )
    }

    @PreDestroy
    fun onDestroy() {
        this.fileSyncClientService.close()
    }
}