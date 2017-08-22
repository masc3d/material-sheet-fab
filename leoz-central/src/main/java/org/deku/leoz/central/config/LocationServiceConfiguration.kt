package org.deku.leoz.central.config

import org.deku.leoz.central.service.internal.LocationServiceV1
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Created by masc on 15/03/16.
 */
@Configuration
@Lazy(false)
open class LocationServiceConfiguration {
    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration
    @Inject
    private lateinit var locationService: LocationServiceV1

    @PostConstruct
    fun onInitialize() {
        this.messageListenerConfiguration.centralTransientQueueListener.addDelegate(
                locationService)
    }
}