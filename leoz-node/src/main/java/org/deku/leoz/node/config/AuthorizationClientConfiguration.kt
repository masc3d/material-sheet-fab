package org.deku.leoz.node.config

import org.deku.leoz.bundle.boot
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.node.Application
import org.deku.leoz.node.LifecycleController
import org.deku.leoz.node.service.internal.AuthorizationClientService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.packager.BundleInstaller
import java.util.concurrent.ScheduledExecutorService
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Leoz-node authorization configuration
 * Responsible for setting up identity of the node and initiating remote authorization task(s)
 * Created by masc on 30.06.15.
 */
@Configuration
@Lazy(false)
open class AuthorizationClientConfiguration {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration

    @Inject
    private lateinit var lifecycleController: LifecycleController

    @Inject
    private lateinit var bundleInstaller: BundleInstaller

    @Inject
    private lateinit var application: Application

    @get:Bean
    open val authorizationClientService: AuthorizationClientService
        get() = AuthorizationClientService(
                executorService = this.executorService,
                identitySupplier = { this.application.identity },
                onRejected = { identity ->
                    log.warn("Authorization rejected for identity [${identity}]")
                    // If rejected, create new identity and restart
                    this.application.initializeIdentity(recreate = true)

                    log.warn("Rebooting due to identity change")
                    this.bundleInstaller.boot(this.application.name)
                    this.application.shutdown()
                })

    @PostConstruct
    fun onInitialize() {
        this.lifecycleController.registerNetworkDependant(this.authorizationClientService)

        // Add message handler delegatess
        this.messageListenerConfiguration.nodeQueueListener.addDelegate(
                this.authorizationClientService)
    }
}
