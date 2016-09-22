package org.deku.leoz.node.config

import org.deku.leoz.discovery.DiscoveryService
import org.deku.leoz.node.App
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Created by masc on 22/09/2016.
 */
@Configuration
@Lazy(false)
open class DiscoveryServiceConfguration {

    @Bean
    open fun discoveryService(): DiscoveryService {
        return DiscoveryService(bundleType = App.instance.bundleType)
    }

    @PostConstruct
    fun onInitialize() {
        this.discoveryService().start()
    }

    @PreDestroy
    fun onDestroy() {
        this.discoveryService().stop()
    }
}