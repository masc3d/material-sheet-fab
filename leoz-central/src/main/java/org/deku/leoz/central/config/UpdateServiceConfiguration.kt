package org.deku.leoz.central.config

import org.deku.leoz.bundle.entities.UpdateInfoRequest
import org.deku.leoz.central.services.UpdateService
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Created by masc on 15/03/16.
 */
@Configuration
@Lazy(false)
open class UpdateServiceConfiguration {

    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration

    @Inject
    private lateinit var updateService: UpdateService

    @PostConstruct
    fun onInitialize() {
        this.messageListenerConfiguration.centralQueueListener.addDelegate(
                UpdateInfoRequest::class.java,
                updateService)

    }
}