package org.deku.leoz.central.config

import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.service.update.UpdateInfoRequest
import org.deku.leoz.central.data.repository.BundleVersionJooqRepository
import org.deku.leoz.central.data.repository.NodeJooqRepository
import org.deku.leoz.central.service.UpdateInfoService
import org.deku.leoz.config.RsyncConfiguration
import org.deku.leoz.config.SshConfiguration
import org.deku.leoz.node.config.BundleConfiguration
import org.deku.leoz.node.config.UpdateConfiguration
import org.deku.leoz.node.peer.RemotePeerSettings
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
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
open class UpdateConfiguration {

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
    @Inject
    private lateinit var localBundleRepository: BundleRepository

    @Bean
    open fun updateInfoService(): UpdateInfoService {
        val rsyncHost = settings.rsyncHost
        val bundleRepository = if (rsyncHost != null && rsyncHost.length > 0) {
            BundleRepository(
                    rsyncModuleUri = RsyncConfiguration.createRsyncUri(
                            hostName = rsyncHost,
                            port = RsyncConfiguration.DEFAULT_PORT,
                            moduleName = RsyncConfiguration.ModuleNames.BUNDLES),
                    rsyncPassword = RsyncConfiguration.PASSWORD,
                    sshTunnelProvider = SshConfiguration.tunnelProvider)
        } else {
            this.localBundleRepository
        }

        return UpdateInfoService(
                nodeJooqRepository = nodeJooqRepository,
                bundleVersionJooqRepository = bundleVersionJooqRepository,
                bundleRepository = bundleRepository)
    }

    private val updateService by lazy { this.updateInfoService() }

    @PostConstruct
    fun onInitialize() {
        this.messageListenerConfiguration.centralQueueListener.addDelegate(
                this.updateService)
    }
}