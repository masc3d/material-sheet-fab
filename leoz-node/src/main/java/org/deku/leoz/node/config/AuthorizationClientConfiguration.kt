package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.boot
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.messaging.entities.AuthorizationMessage
import org.deku.leoz.node.services.AuthorizationClientService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.embedded.Broker
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
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    private lateinit var executorService: ScheduledExecutorService

    @Inject
    private lateinit var messageListenerConfiguration : MessageListenerConfiguration

    @Bean
    open fun authorizationClientService(): AuthorizationClientService {
        return AuthorizationClientService(
                executorService = this.executorService,

                messagingConfiguration = ActiveMQConfiguration.instance,
                identitySupplier = { App.instance.identity },
                onRejected = { identity ->
                    log.warn("Authorization rejected for identity [${identity}]")
                    // If rejected, create new identity and restart
                    App.instance.initializeIdentity(recreate = true)

                    log.warn("Rebooting due to identity change")
                    val installer = BundleConfiguration.bundleInstaller()
                    installer.boot(App.instance.name)
                    App.instance.shutdown()
                })
    }
    private val authorizationClientService by lazy { authorizationClientService() }

    /** Broker event listener */
    private val brokerEventListener = object : Broker.DefaultEventListener() {
        override fun onConnectedToBrokerNetwork() {
            authorizationClientService.restart()
        }

        override fun onStop() {
            authorizationClientService.stop()
        }
    }

    @PostConstruct
    fun onInitialize() {
        // Start authorizer
        ActiveMQConfiguration.instance.broker.delegate.add(this.brokerEventListener)

        // Add message handler delegatess
        this.messageListenerConfiguration.nodeQueueListener.addDelegate(
                AuthorizationMessage::class.java,
                this.authorizationClientService)
    }
}
