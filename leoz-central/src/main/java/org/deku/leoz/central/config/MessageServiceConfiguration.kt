package org.deku.leoz.central.config

import org.deku.leoz.central.service.internal.AuthorizationService
import org.deku.leoz.central.service.internal.FileServiceV1
import org.deku.leoz.central.service.internal.LocationServiceV1
import org.deku.leoz.central.service.internal.LocationServiceV2
import org.deku.leoz.central.service.internal.LogService
import org.deku.leoz.central.service.internal.ParcelServiceV1
import org.deku.leoz.central.service.internal.NodeServiceV1
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Created by masc on 15/03/16.
 */
@Configuration
@Lazy(false)
open class MessageServiceConfiguration {
    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration

    @Inject
    private lateinit var logService: LogService

    @Inject
    private lateinit var authorizationService: AuthorizationService

    @Inject
    private lateinit var parcelService: ParcelServiceV1

    @Inject
    private lateinit var locationServiceV1: LocationServiceV1

    @Inject
    private lateinit var locationServiceV2: LocationServiceV2

    @Inject
    private lateinit var fileService: FileServiceV1

    @Inject
    private lateinit var nodeService: NodeServiceV1

    @PostConstruct
    fun onInitialize() {
        // Register message handlers
        this.messageListenerConfiguration.apply {
            centralQueueListener.addDelegate(
                    authorizationService)

            centralTransientQueueListener.addDelegate(
                    logService)

            centralTransientQueueListener.addDelegate(
                    locationServiceV1)

            centralTransientQueueListener.addDelegate(
                    locationServiceV2)

            centralQueueListener.addDelegate(
                    parcelService)

            centralQueueListener.addDelegate(
                    fileService)

            centralQueueListener.addDelegate(
                    nodeService
            )
        }
    }
}