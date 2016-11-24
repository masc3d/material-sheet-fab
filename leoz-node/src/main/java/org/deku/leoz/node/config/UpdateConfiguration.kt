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
import org.deku.leoz.rest.RestClient
import org.deku.leoz.rest.service.internal.v1.BundleService
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.Channel
import sx.rsync.Rsync
import sx.ssh.SshTunnelProvider
import java.util.*
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
    private lateinit var sshTunnelProvider: SshTunnelProvider
    @Inject
    private lateinit var lifecycleController: LifecycleController
    @Inject
    private lateinit var propertyRepository: PropertyRepository
    @Inject
    private lateinit var localBundleRepository: BundleRepository
    @Inject
    private lateinit var bundleInstaller: BundleInstaller
    @Inject
    private lateinit var restClient: Optional<RestClient>
    @Inject
    private lateinit var bundleService: org.deku.leoz.node.rest.service.internal.v1.BundleService

    /**
     * Bundle service proxy, either returns proxy via RestClient if available (leoz-node) or a
     * direct reference to the BundleService of this procesws (leoz-central)
     */
    private val bundleServiceProxy by lazy {
        if (this.restClient.isPresent) this.restClient.get().proxy(BundleService::class.java) else this.bundleService
    }

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
     * The version alias for this application instance.
     */
    val versionAlias: String get() = this.state.versionAlias

    /**
     * Bundle repository used for retrieving updates
     **/
    private val updateRepository by lazy {
        val remoteHostname = this.settings.rsyncHost ?: remotePeerSettings.host

        if (remoteHostname != null && remoteHostname.length > 0) {
            BundleRepository(
                    rsyncModuleUri = RsyncConfiguration.createRsyncUri(
                            hostName = remoteHostname,
                            port = remotePeerSettings.rsync.port ?: RsyncConfiguration.DEFAULT_PORT,
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
                bundleService = { this.bundleServiceProxy },
                identity = App.instance.identity,
                installer = this.bundleInstaller,
                remoteRepository = { this.updateRepository },
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
                                storeInLocalRepository = true)),
                cleanup = this.settings.cleanup)

        updateService.enabled = this.settings.enabled

        // Event handlers
        updateService.infoReceived.subscribe() {
            if (it.bundleName == App.instance.name) {
                // Store the version alias persistently.
                this.state.versionAlias = it.bundleVersionAlias
                this.propertyRepository.saveObject(this.state)
            }
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