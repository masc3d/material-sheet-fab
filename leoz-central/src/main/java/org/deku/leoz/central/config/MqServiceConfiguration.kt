package org.deku.leoz.central.config

import org.deku.leoz.central.service.internal.AuthorizationService
import org.deku.leoz.central.service.internal.DeliveryListService
import org.deku.leoz.central.service.internal.FileServiceV1
import org.deku.leoz.central.service.internal.LocationServiceV2
import org.deku.leoz.central.service.internal.LogService
import org.deku.leoz.central.service.internal.ParcelServiceV1
import org.deku.leoz.central.service.internal.NodeServiceV1
import org.deku.leoz.central.service.internal.TourServiceV1
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
open class MqServiceConfiguration {
    @Inject
    private lateinit var messageListenerConfiguration: MqListenerConfiguration

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

    /**
     * Central queue message handlers
     */
    private val centralQueueDelegates by lazy {
        listOf(
                authorizationService,
                deliveryListService,
                fileService,
                nodeService,
                parcelService,
                tourService
        )
    }

    /**
     * Central transient queue message handlers
     */
    private val centralTransientQueueDelegates by lazy {
        listOf(
                logService,
                locationServiceV2
        )
    }

    @PostConstruct
    fun onInitialize() {
        // Register message handlers
        this.messageListenerConfiguration.apply {
            centralQueueDelegates.forEach {
                centralQueueListener.addDelegate(it)
            }

            centralTransientQueueDelegates.forEach {
                centralTransientQueueListener.addDelegate(it)
            }
        }
    }
}
