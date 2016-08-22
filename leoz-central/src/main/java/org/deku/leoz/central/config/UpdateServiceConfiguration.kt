package org.deku.leoz.central.config

import org.deku.leoz.bundle.entities.UpdateInfoRequest
import org.deku.leoz.central.data.repositories.BundleVersionRepository
import org.deku.leoz.central.data.repositories.NodeRepository
import org.deku.leoz.central.services.UpdateService
import org.deku.leoz.node.config.BundleConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Created by masc on 15/03/16.
 */
@Configuration
@Lazy(false)
open class UpdateServiceConfiguration {

    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration

    @Inject
    private lateinit var nodeRepository: NodeRepository

    @Inject
    private lateinit var bundleVersionRepository: BundleVersionRepository

    fun updateService(): UpdateService {
        return UpdateService(
                nodeRepository = nodeRepository,
                bundleVersionRepository = bundleVersionRepository,
                localBundleRepository = BundleConfiguration.localRepository)
    }

    private val updateService by lazy { this.updateService() }

    @PostConstruct
    fun onInitialize() {
        this.messageListenerConfiguration.centralQueueListener.addDelegate(
                this.updateService)

    }
}