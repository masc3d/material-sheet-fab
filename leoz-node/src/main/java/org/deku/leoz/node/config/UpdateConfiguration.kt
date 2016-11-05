package org.deku.leoz.node.config

import org.deku.leoz.bundle.BundleInstaller
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.service.update.BundleUpdateService
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.config.RsyncConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.LifecycleController
import org.deku.leoz.node.data.repository.system.*
import org.deku.leoz.node.config.RemotePeerConfiguration
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

/**
 * Leoz updater configuration
 * Created by masc on 30-Oct-15.
 */
@Configuration
@Lazy(false)
open class UpdateConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var settings: Settings
    @Inject
    private lateinit var remotePeerSettings: RemotePeerConfiguration
    @Inject
    private lateinit var executorService: ScheduledExecutorService
    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration
    @Inject
    private lateinit var lifecycleController: LifecycleController
    @Inject
    private lateinit var propertyRepository: PropertyRepository
    @Inject
    private lateinit var localBundleRepository: BundleRepository
    @Inject
    private lateinit var bundleInstaller: BundleInstaller
    @Inject
    lateinit var sshTunnelProvider: SshTunnelProvider

    @Configuration
    @ConfigurationProperties(prefix = "update")
    open class Settings {
        /** Enable/disbale updates */
        var enabled: Boolean = false
        /** Perform automatic updates/retrieve update notifications */
        var automatic: Boolean = true
        /** Automatically clean out outdated and non-relevant bundles */
        var cleanup: Boolean = true
        /** Override rsync uri (defaults to remote peer/host and rsync port) */
        var rsyncHost: String? = null
    }

    /**
     * Bundle update service state
     */
    @PropertyKey(PropertyKeys.BUNDLE_UPDATE_SERVICE)
    class State(
            var versionAlias: String = "") {
    }

    /**
     * State of this configuration
     */
    private var state: State = State()

    /**
     * Bundle repository used for retrieving updates
     **/
    private val updateRepository by lazy {
        val remoteHostname = this.settings.rsyncHost ?: remotePeerSettings.host

        if (remoteHostname != null && remoteHostname.length > 0) {
            BundleRepository(
                    rsyncModuleUri = RsyncConfiguration.createRsyncUri(
                            hostName = remoteHostname,
                            port = remotePeerSettings.rsync.port,
                            moduleName = RsyncConfiguration.ModuleNames.BUNDLES),
                    rsyncPassword = RsyncConfiguration.PASSWORD,
                    sshTunnelProvider = this.sshTunnelProvider)
        } else {
            this.localBundleRepository
        }
    }

    /**
     * The actual bundle update service
     */
    @Bean
    open fun bundleUpdateService(): BundleUpdateService {
        // Setup
        val updateService = BundleUpdateService(
                executorService = this.executorService,
                identity = App.instance.identity,
                installer = this.bundleInstaller,
                remoteRepository = this.updateRepository,
                localRepository = this.localBundleRepository,
                presets = listOf(
                        BundleUpdateService.Preset(
                                bundleName = App.instance.name,
                                install = true,
                                storeInLocalRepository = false,
                                requiresBoot = true),
                        BundleUpdateService.Preset(
                                bundleName = BundleType.LEOZ_UI.value,
                                install = false,
                                storeInLocalRepository = true),
                        BundleUpdateService.Preset(
                                bundleName = BundleType.LEOZ_BOOT.value,
                                install = true,
                                storeInLocalRepository = true)
                ),
                cleanup = this.settings.cleanup,
                requestChannel = Channel(ActiveMQConfiguration.instance.centralQueue)
        )

        updateService.enabled = this.settings.enabled

        // Event handlers
        updateService.ovInfoReceived.subscribe() {
            this.state.versionAlias = it.bundleVersionAlias

            this.propertyRepository.saveObject(this.state)
        }

        return updateService
    }

    @PostConstruct
    fun onInitialize() {
        this.state = this.propertyRepository.loadObject(State::class.java)

        this.lifecycleController.registerNetworkDependant(this.bundleUpdateService())

        // Register for update notifications (as long as automatic updates are enabled)
        if (this@UpdateConfiguration.settings.automatic) {
            this.messageListenerConfiguration.nodeNotificationListener.addDelegate(
                    this.bundleUpdateService())
        }
    }
}