package org.deku.leoz.node.config

import org.apache.sshd.server.SshServer
import org.deku.leoz.service.discovery.DiscoveryInfo
import org.deku.leoz.service.discovery.DiscoveryService
import org.deku.leoz.node.App
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
    private lateinit var rsyncSettings: RsyncServerConfiguration.Settings

    @Inject
    private lateinit var brokerSettings: MessageBrokerConfiguration.Settings

    @Inject
    private lateinit var serverSettings: ServerProperties

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @Bean
    open fun discoveryService(): DiscoveryService {
        return DiscoveryService(
                executorService = this.executorService,
                uid = App.instance.identity.shortKey,
                bundleType = App.instance.bundleType)
    }

    @PostConstruct
    fun onInitialize() {
        val discoveryService = this.discoveryService()

        // Add service infos
        discoveryService.addServices(
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

        this.discoveryService().start()
    }

    @PreDestroy
    fun onDestroy() {
        this.discoveryService().stop()
    }
}