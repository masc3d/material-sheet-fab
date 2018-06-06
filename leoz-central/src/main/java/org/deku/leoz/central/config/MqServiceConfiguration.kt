package org.deku.leoz.central.config

import org.deku.leoz.central.service.internal.*
import org.deku.leoz.central.service.internal.TourServiceV1
import org.deku.leoz.node.service.internal.*
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Message service configuration.
 *
 * Wires mq handlers to listeners.
 * TODO: add spring support for annotation based auto-wiring
 *
 * Created by masc on 15/03/16.
 */
@Configuration
@Lazy(false)
class MqServiceConfiguration {
    @Inject
    private lateinit var mqListenerConfiguration: MqListenerConfiguration

    @Inject
    private lateinit var logService: LogService
    @Inject
    private lateinit var authorizationService: AuthorizationService
    @Inject
    private lateinit var parcelService: ParcelServiceV1
    @Inject
    private lateinit var locationServiceV2: LocationServiceV2
    @Inject
    private lateinit var fileService: FileServiceV1
    @Inject
    private lateinit var nodeService: NodeServiceV1
    @Inject
    private lateinit var deliveryListService: DeliveryListService
    @Inject
    private lateinit var tourService: TourServiceV1
    @Inject
    private lateinit var userService: UserService

    /**
     * Central queue message handlers
     */
    private val centralMainDelegates by lazy {
        listOf(
                authorizationService,
                deliveryListService,
                fileService,
                nodeService,
                parcelService,
                tourService,
                userService
        )
    }

    /**
     * Central transient queue message handlers
     */
    private val centralTransientDelegates by lazy {
        listOf(
                logService,
                locationServiceV2
        )
    }

    @PostConstruct
    fun onInitialize() {
        // Register message handlers
        this.mqListenerConfiguration.apply {
            centralMainDelegates.forEach {
                centralMainListener.addDelegate(it)
            }

            centralTransientDelegates.forEach {
                centralTransientListener.addDelegate(it)
            }
        }
    }
}
