package org.deku.leoz.central.config

import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.update.UpdateInfoRequest
import org.deku.leoz.central.data.repositories.BundleVersionJooqRepository
import org.deku.leoz.central.data.repositories.NodeJooqRepository
import org.deku.leoz.central.services.UpdateInfoService
import org.deku.leoz.config.RsyncConfiguration
import org.deku.leoz.config.SshConfiguration
import org.deku.leoz.node.config.BundleConfiguration
import org.deku.leoz.node.config.BundleUpdateServiceConfiguration
import org.deku.leoz.node.peer.RemotePeerSettings
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by masc on 15/03/16.
 */
@Configuration
@Lazy(false)
open class UpdateInfoServiceConfiguration {

    @Configuration
    @ConfigurationProperties(prefix = "update.info-service")
    open class Settings {
        /** Override local repostiory with remote leoz repository */
        var rsyncHost: String? = null
    }

    @Inject
    private lateinit var settings: Settings

    @Inject
    private lateinit var remotePeerSettings: RemotePeerSettings

    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration

    @Inject
    private lateinit var nodeJooqRepository: NodeJooqRepository

    @Inject
    private lateinit var bundleVersionJooqRepository: BundleVersionJooqRepository

    fun updateService(): UpdateInfoService {
        val rsyncHost = settings.rsyncHost
        val bundleRepository: BundleRepository
        if (rsyncHost != null && rsyncHost.length > 0) {
            bundleRepository = BundleRepository(
                    rsyncModuleUri = RsyncConfiguration.createRsyncUri(
                            hostName = rsyncHost,
                            port = RsyncConfiguration.DEFAULT_PORT,
                            moduleName = RsyncConfiguration.ModuleNames.BUNDLES),
                    rsyncPassword = RsyncConfiguration.PASSWORD,
                    sshTunnelProvider = SshConfiguration.tunnelProvider)
        } else {
            bundleRepository = BundleConfiguration.localRepository
        }


        return UpdateInfoService(
                nodeJooqRepository = nodeJooqRepository,
                bundleVersionJooqRepository = bundleVersionJooqRepository,
                bundleRepository = bundleRepository)
    }

    private val updateService by lazy { this.updateService() }

    @PostConstruct
    fun onInitialize() {
        this.messageListenerConfiguration.centralQueueListener.addDelegate(
                this.updateService)

    }
}