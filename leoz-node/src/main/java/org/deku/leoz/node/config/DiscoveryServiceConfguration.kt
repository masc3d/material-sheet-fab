package org.deku.leoz.node.config

import org.deku.leoz.node.Application
import org.deku.leoz.service.entity.internal.v1.discovery.DiscoveryInfo
import org.deku.leoz.service.internal.v1.DiscoveryService
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import java.util.concurrent.ScheduledExecutorService
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * Created by masc on 22/09/2016.
 */
@Configuration
@Lazy(false)
open class DiscoveryServiceConfguration {

    @Inject
    private lateinit var application: Application
    @Inject
    private lateinit var rsyncSettings: RsyncConfiguration.Settings
    @Inject
    private lateinit var brokerSettings: MessageBrokerConfiguration.Settings
    @Inject
    private lateinit var serverSettings: ServerProperties
    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @get:Bean
    open val discoveryService: DiscoveryService
        get() = DiscoveryService(
                executorService = this.executorService,
                uid = this.application.identity.shortKey,
                bundleType = this.application.bundleType)

    @PostConstruct
    fun onInitialize() {
        // Add service infos
        this.discoveryService.addServices(
                DiscoveryInfo.Service(
                        type = DiscoveryInfo.ServiceType.ACTIVEMQ_NATIVE,
                        port = brokerSettings.nativePort!!),
                DiscoveryInfo.Service(
                        type = if (this.serverSettings.ssl.isEnabled) DiscoveryInfo.ServiceType.HTTPS else DiscoveryInfo.ServiceType.HTTP,
                        port = serverSettings.port),
                DiscoveryInfo.Service(
                        type = DiscoveryInfo.ServiceType.RSYNC,
                        port = rsyncSettings.port!!),
                DiscoveryInfo.Service(
                        type = DiscoveryInfo.ServiceType.SSH,
                        port = SshServerConfiguration.DEFAULT_PORT)
        )

        this.discoveryService.start()
    }

    @PreDestroy
    fun onDestroy() {
        this.discoveryService.stop()
    }
}