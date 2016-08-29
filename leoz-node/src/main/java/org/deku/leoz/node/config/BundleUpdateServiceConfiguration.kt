package org.deku.leoz.node.config

import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.update.BundleUpdateService
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.bundle.update.UpdateInfo
import org.deku.leoz.config.RsyncConfiguration
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.LifecycleController
import org.deku.leoz.node.peer.RemotePeerSettings
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.Channel
import sx.rsync.Rsync
import sx.ssh.SshTunnelProvider
import java.util.concurrent.ScheduledExecutorService
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Named

/**
 * Leoz updater configuration
 * Created by masc on 30-Oct-15.
 */
@Configuration
@Lazy(false)
open class BundleUpdateServiceConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Named
    @ConfigurationProperties(prefix = "update")
    class Settings {
        var enabled: Boolean = false
        var automatic: Boolean = true
        var cleanup: Boolean = true
    }

    @Inject
    private lateinit var settings: Settings

    @Inject
    private lateinit var remotePeerSettings: RemotePeerSettings

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration

    @Inject
    private lateinit var lifecycleController: LifecycleController

    /**
     * Local bundle repository
     * */
    val localRepository: BundleRepository by lazy({
        BundleRepository(
                Rsync.URI(StorageConfiguration.instance.bundleRepositoryDirectory))
    })

    /**
     * Remote bundle update repository
     * */
    val updateRepository: BundleRepository by lazy {
        val remoteHostname = remotePeerSettings.hostname
        if (remoteHostname != null && remoteHostname.length > 0) {
            BundleRepository(
                    rsyncModuleUri = RsyncConfiguration.createRsyncUri(
                            hostName = remoteHostname,
                            port = remotePeerSettings.rsync.port,
                            moduleName = RsyncConfiguration.ModuleNames.BUNDLES),
                    rsyncPassword = RsyncConfiguration.PASSWORD,
                    sshTunnelProvider = this.sshTunnelProvider)
        } else {
            this.localRepository
        }
    }

    /**
     * SSH tunnel provider
     */
    @Inject
    lateinit var sshTunnelProvider: SshTunnelProvider

    /**
     * Updater instance
     */
    @Bean
    open fun bundleUpdateService(): BundleUpdateService {
        val installer = BundleConfiguration.bundleInstaller()

        val updateService = BundleUpdateService(
                executorService = this.executorService,
                identity = App.instance.identity,
                installer = installer,
                remoteRepository = this.updateRepository,
                localRepository = this.localRepository,
                presets = listOf(
                        BundleUpdateService.Preset(
                                bundleName = App.instance.name,
                                install = true,
                                storeInLocalRepository = false,
                                requiresBoot = true),
                        BundleUpdateService.Preset(
                                bundleName = BundleType.LEOZ_BOOT.value,
                                install = true,
                                storeInLocalRepository = true)
                ),
                cleanup = this.settings.cleanup,
                requestChannel = Channel(ActiveMQConfiguration.instance.centralQueue)
        )
        updateService.enabled = this.settings.enabled
        return updateService
    }

    private val bundleUpdateService by lazy { bundleUpdateService() }


    @PostConstruct
    fun onInitialize() {
        this.lifecycleController.registerNetworkDependant(this.bundleUpdateService)

        // Register for update notifications (as long as automatic updates are enabled)
        if (this@BundleUpdateServiceConfiguration.settings.automatic) {
            this.messageListenerConfiguration.nodeNotificationListener.addDelegate(
                    this.bundleUpdateService)
        }
    }
}