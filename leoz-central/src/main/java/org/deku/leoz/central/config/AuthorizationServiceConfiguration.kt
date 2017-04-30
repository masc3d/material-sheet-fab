package org.deku.leoz.central.config

import org.deku.leoz.central.service.internal.authorization.AuthorizationService
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Created by masc on 15/03/16.
 */
@Configuration
@Lazy(false)
open class AuthorizationServiceConfiguration {

    @Inject
    private lateinit var messageListenerConfiguration: MessageListenerConfiguration

    @Inject
    private lateinit var authorizationService: AuthorizationService

    @PostConstruct
    fun onInitialize() {
        this.messageListenerConfiguration.centralQueueListener.addDelegate(
                authorizationService)

    }
}