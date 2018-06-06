package org.deku.leoz.node.config

import org.deku.leoz.node.Application
import org.deku.leoz.node.service.internal.*
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
@Profile(Application.PROFILE_NODE)
@Configuration
@Lazy(false)
open class MqServiceConfiguration {
    @Inject
    private lateinit var mqListenerConfiguration: MqListenerConfiguration

    @Inject
    private lateinit var nodeService: NodeServiceV1

    @Inject
    private lateinit var logService: LogService
    @Inject
    private lateinit var authorizationService: AuthorizationService
    @Inject
    private lateinit var locationService: LocationService
    @Inject
    private lateinit var fileService: FileServiceV1
    @Inject
    private lateinit var tourService: TourServiceV1
    @Inject
    private lateinit var userService: UserService

    /**
     * Central queue message handlers
     */
    private val mainDelegates by lazy {
        listOf(
                authorizationService,
                fileService,
                nodeService,
                tourService,
                userService
        )
    }

    /**
     * Central transient queue message handlers
     */
    private val transientDelegates by lazy {
        listOf(
                logService,
                locationService
        )
    }
    /**
     * Node message handlers
     */
    @Deprecated("Superseded by main / transient")
    private val nodeQueueDelegates by lazy {
        listOf(
                authorizationService,
                fileService,
                nodeService,
                tourService,
                userService
        )
    }

    @PostConstruct
    fun onInitialize() {
        // Register message handlers
        this.mqListenerConfiguration.apply {
            nodeQueueDelegates.forEach {
                nodeQueueListener.addDelegate(it)
            }

            mainDelegates.forEach {
                nodeMainListener.addDelegate(it)
            }

            transientDelegates.forEach {
                nodeTransientListener.addDelegate(it)
            }
        }
    }
}
