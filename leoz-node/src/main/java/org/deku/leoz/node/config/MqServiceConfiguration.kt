package org.deku.leoz.node.config

import org.deku.leoz.node.Application
import org.deku.leoz.node.service.internal.NodeServiceV1
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Message service configuration.
 *
 * Created by masc on 15/03/16.
 */
@Profile(Application.PROFILE_CLIENT_NODE)
@Configuration
@Lazy(false)
open class MqServiceConfiguration {
    @Inject
    private lateinit var mqListenerConfiguration: MqListenerConfiguration

    @Inject
    private lateinit var nodeService: NodeServiceV1

    /**
     * Node message handlers
     */
    private val nodeQueueDelegates by lazy {
        listOf(
                nodeService
        )
    }

    @PostConstruct
    fun onInitialize() {
        // Register message handlers
        this.mqListenerConfiguration.apply {
            nodeQueueDelegates.forEach {
                nodeQueueListener.addDelegate(it)
            }
        }
    }
}
